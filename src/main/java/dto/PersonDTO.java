/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.Map;

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
     @Schema(required = true, example = "2200 Copenhagen N")
    private String city;   
     @Schema(example = "[\"65321345\",\"78987654\"]")
    private Map<Integer,String> phones = new HashMap();
    @Schema(example = "[\"Programming\",\"Beer\"]")
    private Map<String,String> hobbies = new HashMap();

    public PersonDTO(Person person) {
        this.firstname = person.getFirstName();
        this.lastname = person.getLastName();
        this.email = person.getEmail();
        this.address = person.getAddress().getStreet() + " " + person.getAddress().getAddinfo();
        this.city = person.getAddress().getCityInfo().getZip() + " " + person.getAddress().getCityInfo().getCity();
        
        for (Hobby hobby : person.getHobbies()) {
           
            this.hobbies.put(hobby.getName(), hobby.getDescription());
        }
        
         for (Phone phone : person.getPhones()) {
           
            this.phones.put(phone.getNumber(), phone.getDescription());
        }
    }

    public PersonDTO() {
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public Map<Integer, String> getPhones() {
        return phones;
    }

    public Map<String, String> getHobbies() {
        return hobbies;
    }

}
