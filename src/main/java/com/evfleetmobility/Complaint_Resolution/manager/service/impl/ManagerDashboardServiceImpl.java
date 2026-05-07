package com.complaint_resolution.manager.service.impl;


import com.complaint_resolution.manager.service.ManagerDashboardService;
import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ManagerDashboardServiceImpl implements ManagerDashboardService {

    private final ComplaintRepository complaintRepository;

    private final ObjectMapper objectMapper;

    public ManagerDashboardServiceImpl(
            ComplaintRepository complaintRepository,
            ObjectMapper objectMapper
    ) {

        this.complaintRepository =
                complaintRepository;

        this.objectMapper =
                objectMapper;
    }

    public List<Map<String, Object>>
    getAllComplaintsForManager() {

        List<Complaint> complaints =
                complaintRepository.findAll();

        List<Map<String, Object>> managerList =
                new ArrayList<>();

        for (Complaint complaint : complaints) {

            try {

                Map<String, Object> data =
                        new HashMap<>();

                if (complaint.getData() != null
                        && !complaint.getData().isEmpty()) {

                    data = objectMapper.readValue(
                            complaint.getData(),
                            new TypeReference<Map<String, Object>>() {}
                    );
                }

                data.put(
                        "complaintId",
                        complaint.getId()
                );

                data.put(
                        "domain",
                        complaint.getDomain()
                );

                data.put(
                        "status",
                        complaint.getStatus()
                );

                data.put(
                        "issueCategory",
                        complaint.getIssueCategory()
                );

                data.put(
                        "priority",
                        complaint.getPriority()
                );

                data.put(
                        "assignedTeam",
                        complaint.getAssignedTeam()
                );

                data.put(
                        "createdAt",
                        complaint.getCreatedAt()
                );

                managerList.add(data);

            } catch (Exception e) {

                throw new RuntimeException(
                        "Error reading complaint data",
                        e
                );
            }
        }

        return managerList;
    }

    // ✅ GET ONLY ESCALATED COMPLAINTS
    public List<Map<String, Object>>
    getEscalatedComplaintsForManager() {

        List<Complaint> complaints =
                complaintRepository
                        .findByStatusOrderByCreatedAtDesc(
                                "ESCALATED_TO_MANAGER"
                        );

        List<Map<String, Object>> managerList =
                new ArrayList<>();

        for (Complaint complaint : complaints) {

            try {

                Map<String, Object> data =
                        new HashMap<>();

                if (complaint.getData() != null
                        && !complaint.getData().isEmpty()) {

                    data = objectMapper.readValue(
                            complaint.getData(),
                            new TypeReference<Map<String, Object>>() {}
                    );
                }

                data.put(
                        "complaintId",
                        complaint.getId()
                );

                data.put(
                        "domain",
                        complaint.getDomain()
                );

                data.put(
                        "status",
                        complaint.getStatus()
                );

                data.put(
                        "issueCategory",
                        complaint.getIssueCategory()
                );

                data.put(
                        "priority",
                        complaint.getPriority()
                );

                data.put(
                        "assignedTeam",
                        complaint.getAssignedTeam()
                );

                data.put(
                        "createdAt",
                        complaint.getCreatedAt()
                );

                managerList.add(data);

            } catch (Exception e) {

                throw new RuntimeException(
                        "Error reading complaint data",
                        e
                );
            }
        }

        return managerList;
    }

    public Complaint approveAndAssignComplaint(
            Long complaintId,
            String teamName
    ) {

        // Allowed resolution teams
        List<String> validTeams =
                Arrays.asList(

                        "Battery & Charging",
                        "Software & Tech",
                        "Hardware & Mechanics",
                        "Customer & Billing",
                        "Service & Delivery"
                );

        // Validate team
        if (!validTeams.contains(teamName)) {

            throw new RuntimeException(
                    "Invalid resolution team selected"
            );
        }

        Complaint complaint =
                complaintRepository.findById(
                                complaintId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        complaint.setStatus("APPROVED");

        complaint.setAssignedTeam(teamName);

        return complaintRepository.save(
                complaint
        );
    }

    public Complaint rejectComplaint(
            Long complaintId
    ) {

        Complaint complaint =
                complaintRepository.findById(
                                complaintId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        complaint.setStatus("REJECTED");

        complaint.setAssignedTeam(null);

        return complaintRepository.save(
                complaint
        );
    }
    // ✅ MANAGER HISTORY
    public List<Map<String, Object>>
    getManagerHistory() {

        List<Complaint> complaints =
                complaintRepository.findAll();

        List<Map<String, Object>> managerList =
                new ArrayList<>();

        for (Complaint complaint : complaints) {

            // Only manager handled complaints
            if (

                    "RESOLVED".equals(
                            complaint.getStatus()
                    )

                            ||

                            "REJECTED".equals(
                                    complaint.getStatus()
                            )

                            ||

                            "RETRY_VENDOR".equals(
                                    complaint.getStatus()
                            )
            ) {

                try {

                    Map<String, Object> data =
                            new HashMap<>();

                    if (complaint.getData() != null
                            && !complaint.getData().isEmpty()) {

                        data = objectMapper.readValue(
                                complaint.getData(),
                                new TypeReference<Map<String, Object>>() {}
                        );
                    }

                    data.put(
                            "complaintId",
                            complaint.getId()
                    );

                    data.put(
                            "domain",
                            complaint.getDomain()
                    );

                    data.put(
                            "status",
                            complaint.getStatus()
                    );

                    data.put(
                            "issueCategory",
                            complaint.getIssueCategory()
                    );

                    data.put(
                            "priority",
                            complaint.getPriority()
                    );

                    data.put(
                            "assignedTeam",
                            complaint.getAssignedTeam()
                    );

                    data.put(
                            "createdAt",
                            complaint.getCreatedAt()
                    );

                    managerList.add(data);

                } catch (Exception e) {

                    throw new RuntimeException(
                            "Error reading complaint data",
                            e
                    );
                }
            }
        }

        return managerList;
    }
}