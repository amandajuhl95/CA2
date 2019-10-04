/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aamandajuhl
 */
public class PersonDTO {

    private long id;
    @Schema(required = true, example = "Kurt")
    private String firstname;
    @Schema(required = true, example = "Larsen")
    private String lastname;
    @Schema(required = true, example = "kurt_larsen@hotmail.dk")
    private String email;
    @Schema(required = true, example = "Svanevej 3")
    private String address;
    @Schema(example = "[\"65321345\",\"78987654\"]")
    private Set<String> phones = new HashSet();
    @Schema(example = "[\"Programming\",\"Beer\"]")
    private Set<String> hobbies = new HashSet();

    public PersonDTO(String firstname, String lastname, String email, String address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
    }

    public PersonDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getPhones() {
        return phones;
    }

    public void addPhone(String phone) {
        this.phones.add(phone);
    }

    public Set<String> getHobbies() {
        return hobbies;
    }

    public void addHobby(String hobby) {
        this.hobbies.add(hobby);
    }

}
