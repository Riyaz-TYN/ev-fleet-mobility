package com.evfleetmobility.useronboarding.documentservices.repository;

import com.evfleetmobility.useronboarding.documentservices.entity.Document;
import com.evfleetmobility.useronboarding.documentservices.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, DocumentStatus status);
}
