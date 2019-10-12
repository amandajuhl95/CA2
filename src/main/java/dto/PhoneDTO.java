/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author Amanda
 */
public class PhoneDTO {
    
    @Schema(required = true, example = "1")
    private long id;
    @Schema(required = true, example = "22222222")
    private int phone;
    @Schema(required = true, example = "Home")
    private String description;

    public PhoneDTO(Phone phone) {
        this.id = phone.getId();
        this.phone = phone.getNumber();
        this.description = phone.getDescription();
    }

    public PhoneDTO(int phone, String description) {
        this.phone = phone;
        this.description = description;
    }

    public PhoneDTO() {
    }

    public long getId() {
        return id;
    }

    public int getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

}
