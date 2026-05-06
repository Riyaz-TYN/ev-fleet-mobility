package com.evfleetmobility.useronboarding.vehicleservices.dto;

public class VehicleRequest {
    private Long userId;
    private String make;
    private String model;
    private String licensePlate;
    private String vin;
    private String status;
    private Integer yearOfManufacture;
    private Double batteryCapacityKwh;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public Double getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public void setBatteryCapacityKwh(Double batteryCapacityKwh) { this.batteryCapacityKwh = batteryCapacityKwh; }
}


