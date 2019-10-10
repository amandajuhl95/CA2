/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Phone;

/**
 *
 * @author Amanda
 */
public class PhoneDTO {

    private int phone;
    private String description;

    public PhoneDTO(Phone phone) {
        this.phone = phone.getNumber();
        this.description = phone.getDescription();
    }

    public PhoneDTO(int phone, String description) {
        this.phone = phone;
        this.description = description;
    }

    public PhoneDTO() {
    }

    public int getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

}
