package com.evfleetmobility.complaintresolution.complaint.dto;

import java.util.List;

public class FieldConfigDTO {

    private String fieldName;
    private String label;
    private String type;
    private boolean required;
    private List<String> options;

    public FieldConfigDTO() {}

    public FieldConfigDTO(String fieldName, String label, String type,
                          boolean required, List<String> options) {
        this.fieldName = fieldName;
        this.label = label;
        this.type = type;
        this.required = required;
        this.options = options;
    }

    public String getFieldName() { return fieldName; }
    public String getLabel() { return label; }
    public String getType() { return type; }
    public boolean isRequired() { return required; }
    public List<String> getOptions() { return options; }

    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setLabel(String label) { this.label = label; }
    public void setType(String type) { this.type = type; }
    public void setRequired(boolean required) { this.required = required; }
    public void setOptions(List<String> options) { this.options = options; }
}