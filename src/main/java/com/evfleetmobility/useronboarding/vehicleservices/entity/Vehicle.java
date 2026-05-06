package com.evfleetmobility.useronboarding.vehicleservices.entity;

import com.evfleetmobility.useronboarding.authservices.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String licensePlate;
    private String vin;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private Integer yearOfManufacture;
    private Double batteryCapacityKwh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceHistory> serviceHistories = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    public Double getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public void setBatteryCapacityKwh(Double batteryCapacityKwh) { this.batteryCapacityKwh = batteryCapacityKwh; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<ServiceHistory> getServiceHistories() { return serviceHistories; }
    public void setServiceHistories(List<ServiceHistory> serviceHistories) { this.serviceHistories = serviceHistories; }
}


