package com.evfleetmobility.useronboarding.documentservices.dto;

public class DocumentResponse {
    private Long id;
    private String documentType;
    private String fileUrl;
    private String status;
    private String stage;
    private String remarks;

    public DocumentResponse(Long id, String documentType, String fileUrl, String status, String stage, String remarks) {
        this.id = id;
        this.documentType = documentType;
        this.fileUrl = fileUrl;
        this.status = status;
        this.stage = stage;
        this.remarks = remarks;
    }

    public Long getId() { return id; }
    public String getDocumentType() { return documentType; }
    public String getFileUrl() { return fileUrl; }
    public String getStatus() { return status; }
    public String getStage() { return stage; }
    public String getRemarks() { return remarks; }
}
