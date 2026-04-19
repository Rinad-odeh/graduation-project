package com.baligh.backend.service;

import com.baligh.backend.dto.request.CreateIssueRequest;
import com.baligh.backend.dto.request.RateIssueRequest;
import com.baligh.backend.dto.request.UpdateIssueStatusRequest;
import com.baligh.backend.dto.response.AttachmentResponse;
import com.baligh.backend.dto.response.IssueResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.exception.BusinessException;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.*;
import com.baligh.backend.model.enums.*;
import com.baligh.backend.repository.*;
import com.baligh.backend.util.IssueStatusValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueAttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository orgRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;

    @Transactional
    public IssueResponse create(Long reporterId, CreateIssueRequest request) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", reporterId));
        Organization org = orgRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Organization", request.getOrganizationId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId()));

        if (org.getStatus() != OrgStatus.APPROVED) {
            throw new BusinessException("Organization is not accepting issues");
        }

        Issue issue = Issue.builder()
                .reporter(reporter)
                .organization(org)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .locationLat(request.getLocationLat())
                .locationLng(request.getLocationLng())
                .locationAddress(request.getLocationAddress())
                .build();

        Issue saved = issueRepository.save(issue);

        // Notify after commit so the issue row is no longer locked (avoids FK lock contention)
        final User recipient = org.getRepresentative();
        final Long issueId = saved.getId();
        final String issueTitle = saved.getTitle();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    notificationService.send(recipient, saved,
                            NotificationType.NEW_ISSUE_RECEIVED,
                            "بلاغ جديد",
                            "تم استلام بلاغ جديد: " + issueTitle);
                } catch (Exception e) {
                    log.warn("Failed to send NEW_ISSUE_RECEIVED notification for issue {}: {}", issueId, e.getMessage());
                }
            }
        });

        return IssueResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> getMyIssues(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        Pageable pageable = PageRequest.of(page, size);
        Page<IssueResponse> result = issueRepository
                .findByReporterOrderByCreatedAtDesc(user, pageable)
                .map(IssueResponse::from);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public IssueResponse getById(Long id) {
        return IssueResponse.from(findOrThrow(id));
    }

    @Transactional
    public IssueResponse updateStatus(Long issueId, Long orgUserId, UpdateIssueStatusRequest request) {
        Issue issue = findOrThrow(issueId);
        User orgUser = userRepository.findById(orgUserId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", orgUserId));

        IssueStatusValidator.validate(issue.getStatus(), request.getNewStatus());

        IssueUpdate update = IssueUpdate.builder()
                .issue(issue)
                .updatedBy(orgUser)
                .newStatus(request.getNewStatus())
                .comment(request.getComment())
                .authorType(UpdateAuthorType.ORG)
                .build();

        issue.getUpdates().add(update);
        issue.setStatus(request.getNewStatus());

        if (request.getExpectedResolutionDate() != null) {
            issue.setExpectedResolutionDate(request.getExpectedResolutionDate());
        }

        if (request.getNewStatus() == IssueStatus.RESOLVED) {
            issue.setResolvedAt(LocalDateTime.now());
        }

        Issue saved = issueRepository.save(issue);

        final User reporter = saved.getReporter();
        final Long savedId = saved.getId();
        final String statusName = request.getNewStatus().name();
        final String title = saved.getTitle();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    notificationService.send(reporter, saved,
                            NotificationType.ISSUE_STATUS_CHANGED,
                            "تحديث حالة بلاغك",
                            "تم تغيير حالة بلاغ \"" + title + "\" إلى " + statusName);
                } catch (Exception e) {
                    log.warn("Failed to send ISSUE_STATUS_CHANGED notification for issue {}: {}", savedId, e.getMessage());
                }
            }
        });

        return IssueResponse.from(saved);
    }

    @Transactional
    public IssueResponse rate(Long issueId, Long userId, RateIssueRequest request) {
        Issue issue = findOrThrow(issueId);

        if (!issue.getReporter().getId().equals(userId)) {
            throw new BusinessException("You can only rate your own issues", HttpStatus.FORBIDDEN);
        }
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new BusinessException("Issue must be resolved before rating");
        }
        if (issue.getRating() != null) {
            throw new BusinessException("Issue has already been rated");
        }

        issue.setRating(request.getRating());
        issue.setRatingComment(request.getComment());

        Issue saved = issueRepository.save(issue);

        final User orgRep = saved.getOrganization().getRepresentative();
        final Long ratedIssueId = saved.getId();
        final int stars = request.getRating();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    notificationService.send(orgRep, saved,
                            NotificationType.ISSUE_RATED,
                            "تقييم جديد",
                            "قيّم المستخدم بلاغه بـ " + stars + " نجوم");
                } catch (Exception e) {
                    log.warn("Failed to send ISSUE_RATED notification for issue {}: {}", ratedIssueId, e.getMessage());
                }
            }
        });

        return IssueResponse.from(saved);
    }

    @Transactional
    public List<AttachmentResponse> uploadAttachments(Long issueId, Long userId, List<MultipartFile> files) {
        Issue issue = findOrThrow(issueId);

        if (!issue.getReporter().getId().equals(userId)) {
            throw new BusinessException("You can only add attachments to your own issues", HttpStatus.FORBIDDEN);
        }

        return files.stream().map(file -> {
            String url = storageService.store(file, issueId);
            IssueAttachment attachment = IssueAttachment.builder()
                    .issue(issue)
                    .url(url)
                    .fileName(file.getOriginalFilename())
                    .mimeType(file.getContentType())
                    .sizeBytes(file.getSize())
                    .build();
            return AttachmentResponse.from(attachmentRepository.save(attachment));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachments(Long issueId) {
        Issue issue = findOrThrow(issueId);
        return attachmentRepository.findByIssue(issue).stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> getOrgIssues(Long orgId, IssueStatus status, int page, int size) {
        Organization org = orgRepository.findById(orgId)
                .orElseThrow(() -> ResourceNotFoundException.of("Organization", orgId));
        Pageable pageable = PageRequest.of(page, size);
        Page<IssueResponse> result = status != null
                ? issueRepository.findByOrganizationAndStatusOrderByCreatedAtDesc(org, status, pageable).map(IssueResponse::from)
                : issueRepository.findByOrganizationOrderByCreatedAtDesc(org, pageable).map(IssueResponse::from);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> getAllIssues(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(issueRepository.findAll(pageable).map(IssueResponse::from));
    }

    private Issue findOrThrow(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Issue", id));
    }
}
