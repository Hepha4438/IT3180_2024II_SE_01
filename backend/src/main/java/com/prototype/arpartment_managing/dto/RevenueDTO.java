package com.prototype.arpartment_managing.dto;

import com.prototype.arpartment_managing.model.Revenue;

public class RevenueDTO {

    private long id;

    private String apartmentId;

    private String type;

    private String status;

    public RevenueDTO() {

    }

    public RevenueDTO(Revenue revenue) {
        this.id = revenue.getId();
        this.type = revenue.getType();
        this.status = revenue.getStatus();
        this.apartmentId = revenue.getApartment().getApartmentId();
    }
}
