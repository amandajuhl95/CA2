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
    @Schema(required = true, example = "Svanevej")
    private String street;
    @Schema(required = true, example = "3")
    private String addInfo;
    @Schema(required = true, example = "2200")
    private int zip;
    @Schema(required = true, example = "Copenhagen N")
    private String city;   
    @Schema(example = "[\"65321345\",\"78987654\"]")
    private Set<PhoneDTO> phones = new HashSet();
    @Schema(example = "[\"Programming\",\"Beer\"]")
    private Set<HobbyDTO> hobbies = new HashSet();

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.firstname = person.getFirstName();
        this.lastname = person.getLastName();
        this.email = person.getEmail();
        this.street = person.getAddress().getStreet();
        this.addInfo = person.getAddress().getAddinfo();
        this.zip = person.getAddress().getCityInfo().getZip();
        this.city = person.getAddress().getCityInfo().getCity();
        
        for (Hobby hobby : person.getHobbies()) {
           
            this.hobbies.add(new HobbyDTO(hobby));
        }
        
         for (Phone phone : person.getPhones()) {
           
            this.phones.add(new PhoneDTO(phone));
        }
    }
    
     public PersonDTO(String firstname, String lastname, String email, String street, String addInfo, String zip, String city) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.street = street;
        this.addInfo = addInfo;
        this.zip = Integer.parseInt(zip);
        this.city = city;
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

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getStreet() {
        return street;
    }
    
    public String getAddInfo() {
        return addInfo;
    }
    
    public int getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public Set<PhoneDTO> getPhones() {
        return phones;
    }

    public Set<HobbyDTO> getHobbies() {
        return hobbies;
    }

}
