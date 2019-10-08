package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


@Entity
@NamedQuery(name = "Person.deleteAllRows", query = "DELETE from Person")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @ManyToOne(cascade = {CascadeType.ALL})
    private Address address;
    @ManyToMany(cascade = {CascadeType.ALL})
    private List<Hobby> hobbies = new ArrayList();
    @OneToMany(mappedBy = "person", cascade = {CascadeType.ALL})
    private List<Phone> phones = new ArrayList();
    
    public Person() {
    }

    public Person(String email, String firstName, String lastName, Address address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public List<Phone> getPhones() {
        return phones;
    }
    
    public void addHobby(Hobby hobby)
    {
        this.hobbies.add(hobby);
        hobby.addPerson(this);
    }
    
    public void removeHobby(Hobby hobby)
    {
        this.hobbies.remove(hobby);
        hobby.removePerson(this);
    }
    
    public void addPhone(Phone phone)
    {
        this.phones.add(phone);
        phone.setPerson(this);
    }
    
     public void removePhone(Phone phone)
    {
        this.phones.remove(phone);
    }
   
}
