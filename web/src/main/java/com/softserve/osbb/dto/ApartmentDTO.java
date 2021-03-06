package com.softserve.osbb.dto;

import com.softserve.osbb.model.House;
import com.softserve.osbb.model.User;

/**
 * Created by Oleg on 20.08.2016.
 */
public class ApartmentDTO {
    private Integer apartmentId;
    private Integer number;
    private Integer square;
    private Integer owner;
    private House house;
public ApartmentDTO(){}
    public ApartmentDTO(Integer apartmentId, Integer number, Integer square, Integer owner,House house) {
        this.apartmentId = apartmentId;
        this.number = number;
        this.square = square;
        this.owner = owner;
        this.house=house;
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSquare() {
        return square;
    }

    public void setSquare(Integer square) {
        this.square = square;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
