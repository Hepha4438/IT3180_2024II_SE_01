package com.prototype.arpartment_managing.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "apartment_id")
    private String apartmentId;

    @Column(name = "floor")
    private Integer floor;
    @Column(name = "area")
    private Float area;
    @Column(name = "apartmentType")
    private String apartmentType;
    @Column(name = "owner")
    private String owner;
    @Column(name = "occupants")
    private Integer occupants = 0;

    @Column(name = "is_occupied")
    private Boolean isOccupied = false;

    @OneToMany(mappedBy = "apartment", cascade = { CascadeType.ALL},
            fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonManagedReference
    private List<User> residents;

    @OneToMany(mappedBy = "apartment", cascade = { CascadeType.ALL},
            fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonManagedReference
    private List<Revenue> revenues;

    @Column( name = "total" )
    private Double total;

    // Constructors
    public Apartment() {
    }

    public Apartment(String apartmentId){

    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Float getArea() {
        return area;
    }

    public void setArea(Float area) {
        this.area = area;
    }
    public String getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(String apartmentType) {
        this.apartmentType = apartmentType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getOccupants() {
        return occupants;
    }

    public void setOccupants(Integer occupants) {
        this.occupants = occupants;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
    public List<User> getResidents() {
        return residents;
    }

    public void setResidents(List<User> residents) {
        this.residents = residents;
    }

    public List<Revenue> getRevenues() {
        return revenues;
    }

    public void setRevenues(List<Revenue> revenues) {
        this.revenues = revenues;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}