package com.baligh.backend.service;

import com.baligh.backend.dto.request.RegisterOrgRequest;
import com.baligh.backend.dto.request.RejectOrgRequest;
import com.baligh.backend.dto.response.OrgResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.exception.BusinessException;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.NotificationType;
import com.baligh.backend.model.enums.OrgStatus;
import com.baligh.backend.repository.IssueRepository;
import com.baligh.backend.repository.OrganizationRepository;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository orgRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public OrgResponse register(Long representativeId, RegisterOrgRequest request) {
        User representative = userRepository.findById(representativeId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", representativeId));

        orgRepository.findByRepresentative(representative).ifPresent(o -> {
            throw new BusinessException("User already has a registered organization");
        });

        Organization org = Organization.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .representative(representative)
                .build();

        return toResponse(orgRepository.save(org));
    }

    public PageResponse<OrgResponse> getApproved(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrgResponse> result;
        if (search != null && !search.isBlank()) {
            result = orgRepository.searchApproved(search, pageable).map(this::toResponse);
        } else {
            result = orgRepository.findByStatus(OrgStatus.APPROVED, pageable).map(this::toResponse);
        }
        return PageResponse.from(result);
    }

    public OrgResponse getById(Long id) {
        Organization org = orgRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Organization", id));
        return toResponse(org);
    }

    public PageResponse<OrgResponse> getPending(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(
                orgRepository.findByStatusOrderByCreatedAtDesc(OrgStatus.PENDING, pageable).map(this::toResponse)
        );
    }

    public PageResponse<OrgResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(orgRepository.findAll(pageable).map(this::toResponse));
    }

    @Transactional
    public OrgResponse approve(Long id) {
        Organization org = findOrThrow(id);
        assertPending(org);
        org.setStatus(OrgStatus.APPROVED);
        Organization saved = orgRepository.save(org);
        final User rep = org.getRepresentative();
        final Long orgId = org.getId();
        final String orgName = org.getName();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    notificationService.send(rep, null, NotificationType.ORG_APPROVED,
                            "تهانينا! تم اعتماد جهتك",
                            "تم قبول طلب تسجيل جهة " + orgName + " في منصة بلّغ.");
                } catch (Exception e) {
                    log.warn("Failed to send ORG_APPROVED notification for org {}: {}", orgId, e.getMessage());
                }
            }
        });
        return toResponse(saved);
    }

    @Transactional
    public OrgResponse reject(Long id, RejectOrgRequest request) {
        Organization org = findOrThrow(id);
        assertPending(org);
        org.setStatus(OrgStatus.REJECTED);
        org.setRejectionReason(request.getRejectionReason());
        Organization saved = orgRepository.save(org);
        final User rep = org.getRepresentative();
        final Long orgId = org.getId();
        final String orgName = org.getName();
        final String reason = request.getRejectionReason();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    notificationService.send(rep, null, NotificationType.ORG_REJECTED,
                            "عذراً، لم يتم قبول طلبك",
                            "تم رفض طلب تسجيل جهة " + orgName + ". السبب: " + reason);
                } catch (Exception e) {
                    log.warn("Failed to send ORG_REJECTED notification for org {}: {}", orgId, e.getMessage());
                }
            }
        });
        return toResponse(saved);
    }

    @Transactional
    public OrgResponse suspend(Long id) {
        Organization org = findOrThrow(id);
        if (org.getStatus() != OrgStatus.APPROVED) {
            throw new BusinessException("Only approved organizations can be suspended", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        org.setStatus(OrgStatus.SUSPENDED);
        return toResponse(orgRepository.save(org));
    }

    public OrgResponse getMyOrg(Long representativeId) {
        User representative = userRepository.findById(representativeId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", representativeId));
        Organization org = orgRepository.findByRepresentative(representative)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found for this user"));
        return toResponse(org);
    }

    @Transactional
    public OrgResponse updateMyOrg(Long representativeId, RegisterOrgRequest request) {
        User representative = userRepository.findById(representativeId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", representativeId));
        Organization org = orgRepository.findByRepresentative(representative)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found for this user"));

        if (request.getName() != null) org.setName(request.getName());
        if (request.getDescription() != null) org.setDescription(request.getDescription());
        if (request.getContactEmail() != null) org.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null) org.setContactPhone(request.getContactPhone());

        return toResponse(orgRepository.save(org));
    }

    private void assertPending(Organization org) {
        if (org.getStatus() != OrgStatus.PENDING) {
            throw new BusinessException("Organization is not in PENDING status", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private Organization findOrThrow(Long id) {
        return orgRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Organization", id));
    }

    private OrgResponse toResponse(Organization org) {
        long total = issueRepository.countByOrganization(org);
        long resolved = issueRepository.countResolvedByOrganization(org);
        Double avgRating = issueRepository.avgRatingByOrganization(org);
        return OrgResponse.from(org, total, resolved, avgRating);
    }
}
