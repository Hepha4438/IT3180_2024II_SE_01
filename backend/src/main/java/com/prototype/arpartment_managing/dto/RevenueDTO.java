package com.prototype.arpartment_managing.dto;

import com.prototype.arpartment_managing.model.Revenue;

public class RevenueDTO {

    private long id;

    private String apartmentId;

    private String type;

    private String status;

    private double used;

    private double total;
    public RevenueDTO() {

    }
    public RevenueDTO(Revenue revenue) {
        this.id = revenue.getId();
        this.type = revenue.getType();
        this.status = revenue.getStatus();
        this.apartmentId = (revenue.getApartment() != null) ? revenue.getApartment().getApartmentId() : null;
        this.used = revenue.getUsed();
        this.total = revenue.getTotal();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getUsed() {
        return used;
    }

    public void setUsed(double used) {
        this.used = used;
    }
}
