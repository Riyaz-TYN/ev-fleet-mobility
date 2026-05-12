package com.evfleetmobility.useronboarding.documentservices.service;

import com.evfleetmobility.useronboarding.documentservices.dto.DocumentDownloadResponse;
import com.evfleetmobility.useronboarding.documentservices.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.data.domain.Page;

public interface DocumentService {
    DocumentResponse upload(Long userId, MultipartFile file, String documentType);
    Page<DocumentResponse> getMyDocuments(Long userId, int page, int size);
    String getPresignedUrl(Long userId, Long documentId);
    DocumentDownloadResponse getPanCard(Long userId);
    DocumentDownloadResponse getPanCardForAdmin(Long userId);
}
