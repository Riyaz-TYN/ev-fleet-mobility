package com.evfleetmobility.useronboarding.documentservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.documentservices.dto.DocumentDownloadResponse;
import com.evfleetmobility.useronboarding.documentservices.dto.DocumentResponse;
import com.evfleetmobility.useronboarding.documentservices.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        DocumentResponse response = documentService.upload(userId, file, documentType);
        return ResponseEntity.ok(new ApiResponse<>("Document uploaded successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getMyDocuments(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        List<DocumentResponse> response = documentService.getMyDocuments(userId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched documents successfully", response));
    }

    @GetMapping("/presign/{documentId}")
    public ResponseEntity<ApiResponse<String>> getPresignedUrl(
            @PathVariable Long documentId,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        String url = documentService.getPresignedUrl(userId, documentId);
        return ResponseEntity.ok(new ApiResponse<>("Pre-signed URL generated (valid 15 minutes)", url));
    }

    @GetMapping("/download/pan")
    public ResponseEntity<byte[]> downloadPanCard(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        DocumentDownloadResponse response = documentService.getPanCard(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(response.getContent());
    }

    @GetMapping("/download/pan/{targetUserId}")
    public ResponseEntity<byte[]> downloadPanCardForAdmin(
            @PathVariable Long targetUserId,
            Principal principal) {
        DocumentDownloadResponse response = documentService.getPanCardForAdmin(targetUserId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(response.getContent());
    }
}
