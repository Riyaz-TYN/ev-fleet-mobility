package com.evfleetmobility.useronboarding.documentservices.service.impl;

import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.documentservices.dto.DocumentDownloadResponse;
import com.evfleetmobility.useronboarding.documentservices.dto.DocumentResponse;
import com.evfleetmobility.useronboarding.documentservices.entity.Document;
import com.evfleetmobility.useronboarding.documentservices.entity.DocumentStage;
import com.evfleetmobility.useronboarding.documentservices.entity.DocumentStatus;
import com.evfleetmobility.useronboarding.documentservices.entity.DocumentType;
import com.evfleetmobility.useronboarding.documentservices.repository.DocumentRepository;
import com.evfleetmobility.useronboarding.documentservices.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public DocumentResponse upload(Long userId, MultipartFile file, String documentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String s3Key = uploadToS3(file, user.getId(), documentType);

        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(DocumentType.valueOf(documentType.toUpperCase()));
        document.setFileUrl(s3Key);
        document.setStatus(DocumentStatus.PENDING);
        document.setStage(DocumentStage.UPLOADED);
        document.setUploadedAt(LocalDateTime.now());

        Document savedDoc = documentRepository.save(document);
        return mapToResponse(savedDoc);
    }

    @Override
    public List<DocumentResponse> getMyDocuments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return documentRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String getPresignedUrl(Long userId, Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: this document does not belong to you");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(document.getFileUrl())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public DocumentDownloadResponse getPanCard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return fetchPanCard(user);
    }

    @Override
    public DocumentDownloadResponse getPanCardForAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return fetchPanCard(user);
    }

    private DocumentDownloadResponse fetchPanCard(User user) {
        byte[] content = null;
        String contentType = null;

        if (user.getUserType() == UserType.INDIVIDUAL && user.getIndividualDetails() != null) {
            content = user.getIndividualDetails().getPanCardContent();
            contentType = user.getIndividualDetails().getPanCardContentType();
        } else if (user.getUserType() == UserType.ORGANIZATION && user.getOrganizationDetails() != null) {
            content = user.getOrganizationDetails().getPanCardContent();
            contentType = user.getOrganizationDetails().getPanCardContentType();
        }

        if (content == null) {
            throw new RuntimeException("PAN card document not found in database for this user");
        }

        return new DocumentDownloadResponse(content, contentType);
    }

    private String uploadToS3(MultipartFile file, Long userId, String documentType) {
        try {
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String s3Key = "documents/" + userId + "/" + documentType.toUpperCase() + "/" + uniqueFileName;

            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return s3Key;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private DocumentResponse mapToResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getDocumentType().name(),
                doc.getFileUrl(),
                doc.getStatus().name(),
                doc.getStage().name(),
                doc.getRemarks()
        );
    }
}
