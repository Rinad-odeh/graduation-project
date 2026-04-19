package com.baligh.backend.dto.response;

import com.baligh.backend.model.IssueAttachment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentResponse {
    private Long id;
    private String url;
    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private LocalDateTime uploadedAt;

    public static AttachmentResponse from(IssueAttachment a) {
        AttachmentResponse r = new AttachmentResponse();
        r.id = a.getId();
        r.url = a.getUrl();
        r.fileName = a.getFileName();
        r.mimeType = a.getMimeType();
        r.sizeBytes = a.getSizeBytes();
        r.uploadedAt = a.getUploadedAt();
        return r;
    }
}
