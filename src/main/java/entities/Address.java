/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author benja, amalie and amanda
 */
@Entity
@NamedQuery(name = "Address.deleteAllRows", query = "DELETE from Address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String addinfo;
    @ManyToOne
    private CityInfo cityInfo;
    @OneToMany(mappedBy = "address")
    private List<Person> persons = new ArrayList();
    
    public Address() {
    }

    public Address(String street, String addinfo, CityInfo cityInfo) {
        this.street = street;
        this.addinfo = addinfo;
        this.cityInfo = cityInfo;
    }
   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public CityInfo getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(CityInfo cityInfo) {
        this.cityInfo = cityInfo;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddinfo() {
        return addinfo;
    }

    public void setAddinfo(String addinfo) {
        this.addinfo = addinfo;
    }
    
    public void addPerson(Person person)
    {
        this.persons.add(person);
    }
    
    public void removePerson(Person person)
    {
        this.persons.remove(person);
    }
    
}
