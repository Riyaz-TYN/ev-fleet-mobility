package com.complaint_resolution.vendor.repository;

import com.complaint_resolution.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Fetch only available vendors
    List<Vendor> findByAvailabilityTrue();
    List<Vendor> findByExpertiseIgnoreCase(String expertise);

    // ✅ Get vendor by availability
    List<Vendor> findByAvailability(Boolean availability);
}