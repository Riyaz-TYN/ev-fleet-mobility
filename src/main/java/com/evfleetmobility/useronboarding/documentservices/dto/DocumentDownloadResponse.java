package com.evfleetmobility.useronboarding.documentservices.dto;

public class DocumentDownloadResponse {
    private byte[] content;
    private String contentType;

    public DocumentDownloadResponse(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public byte[] getContent() { return content; }
    public String getContentType() { return contentType; }
}
