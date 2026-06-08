package com.baligh.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "issue_attachments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IssueAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    @JsonBackReference
    private Issue issue;

    @Column(nullable = false)
    private String url;

    private String fileName;

    private String mimeType;

    private long sizeBytes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
}
