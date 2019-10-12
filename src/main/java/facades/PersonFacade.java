package facades;

import dto.HobbyDTO;
import dto.PersonDTO;
import dto.PhoneDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Amanda
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonDTO addPerson(PersonDTO p) {

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            //Checking if cityinfo already exsists in Database
            CityInfo cityInfo;
            List<CityInfo> cityDB = getCityInfo(p.getCity(), p.getZip());
            if (cityDB.size() > 0) {
                cityInfo = cityDB.get(0);
            } else {
                cityInfo = new CityInfo(p.getZip(), p.getCity());
            }

            //Checking if address already exsists in Database
            Address address;
            List<Address> addressDB = getAddress(p.getStreet(), p.getAddInfo(), p.getCity(), p.getZip());
            if (addressDB.size() > 0) {
                address = addressDB.get(0);
            } else {
                address = new Address(p.getStreet(), p.getAddInfo(), cityInfo);
            }

            //Merging city and address
            CityInfo mergedCity = em.merge(cityInfo);
            Address mergedAddress = em.merge(address);
            mergedAddress.setCityInfo(mergedCity);

            Person person = new Person(p.getEmail(), p.getFirstname(), p.getLastname(), mergedAddress);
            person.setAddress(mergedAddress);

            em.persist(person);
            em.getTransaction().commit();

            return new PersonDTO(person);

        } finally {
            em.close();
        }
    }

    public PersonDTO editPerson(PersonDTO p) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, p.getId());
        if (person == null) {

            throw new WebApplicationException("No person with the given id was found");
        }

        //Checking if cityinfo already exsists in Database
        CityInfo cityInfo;
        List<CityInfo> cityDB = getCityInfo(p.getCity(), p.getZip());
        if (cityDB.size() > 0) {
            cityInfo = cityDB.get(0);
        } else {
            cityInfo = new CityInfo(p.getZip(), p.getCity());
        }

        //Checking if address already exsists in Database
        Address address;
        List<Address> addressDB = getAddress(p.getStreet(), p.getAddInfo(), p.getCity(), p.getZip());
        if (addressDB.size() > 0) {
            address = addressDB.get(0);
        } else {
            address = new Address(p.getStreet(), p.getAddInfo(), cityInfo);
        }

        //Setting edits on the person with the given id
        person.setFirstName(p.getFirstname());
        person.setLastName(p.getLastname());
        person.setEmail(p.getEmail());

        //Removing old address relations, for it to be deleted, if no one lives there
        Address oldAddress = person.getAddress();
        int numOfPeople = getPersonsByAddress(oldAddress.getStreet(), oldAddress.getAddinfo()).size();

        if (numOfPeople == 1 && !oldAddress.equals(address)) {
            oldAddress.removePerson(person);
            oldAddress.getCityInfo().removeAddress(oldAddress);
        }

        try {
            em.getTransaction().begin();

            //Merging the new address and city
            CityInfo mergedCity = em.merge(cityInfo);
            Address mergedAddress = em.merge(address);

            //Merging address and city
            mergedAddress.setCityInfo(mergedCity);
            person.setAddress(mergedAddress);

            em.merge(person);
            em.persist(person);

            if (numOfPeople == 1 && !oldAddress.equals(person.getAddress())) {
                em.remove(oldAddress);
            }

            em.getTransaction().commit();

            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    private List<CityInfo> getCityInfo(String city, int zip) {

        EntityManager em = getEntityManager();
        List<CityInfo> cityInfo;
        TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c WHERE c.zip = :zip AND c.city = :city", CityInfo.class);
        cityInfo = query.setParameter("zip", zip).setParameter("city", city).getResultList();
        return cityInfo;
    }

    private List<Address> getAddress(String street, String addinfo, String city, int zip) {

        EntityManager em = getEntityManager();
        List<Address> adr;
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a INNER JOIN a.cityInfo c WHERE a.street = :street AND a.addinfo = :addinfo AND c.city = :city AND c.zip = :zip", Address.class);
        adr = query.setParameter("street", street).setParameter("addinfo", addinfo).setParameter("city", city).setParameter("zip", zip).getResultList();
        return adr;
    }

    public void deletePerson(long person_id) throws WebApplicationException {

        EntityManager em = getEntityManager();
        Person person;

        try {
            em.getTransaction().begin();
            person = em.find(Person.class, person_id);
            int numOfPeople = getPersonsByAddress(person.getAddress().getStreet(), person.getAddress().getAddinfo()).size();
            if (numOfPeople == 1) {
                em.remove(person.getAddress());
            }

            em.remove(person);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new WebApplicationException("Could not delete person", 400);
        } finally {
            em.close();
        }
    }

    private List<PersonDTO> getPersonsByAddress(String street, String addinfo) {

        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.address a WHERE a.street = :street AND a.addinfo = :addinfo", Person.class);
        List<Person> persons = query.setParameter("street", street).setParameter("addinfo", addinfo).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

    public PersonDTO getPerson(int number) {

        EntityManager em = getEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.phones ph WHERE ph.number = :number", Person.class);
        Person person = query.setParameter("number", number).getSingleResult();
        if (person == null) {
            throw new WebApplicationException("No person found with the given phonenumber", 400);
        }

        PersonDTO personDTO = new PersonDTO(person);

        return personDTO;

    }

    public PersonDTO getPersonById(long id) {

        EntityManager em = getEntityManager();

        try {
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new WebApplicationException("No person found with the given id", 400);
            }
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAllPersons() {
        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> persons = query.getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));
        }

        return personsDTO;
    }

    public List<PersonDTO> getPersonsByCity(String city) {

        EntityManager em = getEntityManager();

        CityInfo cityInfo = getCityInfo(city).get(0);
        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.address a WHERE a.cityInfo.id = :city", Person.class);
        List<Person> persons = query.setParameter("city", cityInfo.getId()).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

    //This method is only used for check in in the Rest endpoints editperson and addperson
    public List<PersonDTO> getPersonByEmail(String email) {
        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.email = :email", Person.class);
        List<Person> persons = query.setParameter("email", email).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

    public List<PersonDTO> getPersonsByHobby(String hobby) {

        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.hobbies pho WHERE pho.name = :hobby", Person.class);
        List<Person> persons = query.setParameter("hobby", hobby).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));
        }
        return personsDTO;
    }

    public int getPersonCountByHobby(String hobby) {

        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT COUNT(p) FROM Person p INNER JOIN p.hobbies pho WHERE pho.name = :hobby");
        long c = (long) query.setParameter("hobby", hobby).getSingleResult();
        int count = (int) c;
        return count;
    }

    public List<CityInfo> getCityInfo(String city) {

        EntityManager em = getEntityManager();

        if (city.matches("[0-9]+")) {
            int zip = Integer.parseInt(city);
            TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c WHERE c.zip = :zip", CityInfo.class);
            List<CityInfo> cityInfo = query.setParameter("zip", zip).getResultList();
            return cityInfo;
        } else {
            TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c WHERE c.city = :city", CityInfo.class);
            List<CityInfo> cityInfo = query.setParameter("city", city).getResultList();
            return cityInfo;
        }
    }

    public List<Integer> getZipcodes() {

        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT c.zip FROM CityInfo c");
        List<Integer> zipcodes = query.getResultList();
        return zipcodes;

    }

    public PersonDTO addHobby(long person_id, HobbyDTO h) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, person_id);
        if (person == null) {
            throw new WebApplicationException("No person with the given id was found");
        }

        Hobby hobby;
        List<Hobby> hobbies = getHobby(h.getHobby());
        if (hobbies.size() > 0) {
            hobby = hobbies.get(0);
        } else {
            hobby = new Hobby(h.getHobby().toLowerCase(), h.getDescription());
        }

        person.addHobby(hobby);

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    public PersonDTO deleteHobby(Long person_id, Long hobby_id) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, person_id);
        if (person == null) {
            throw new WebApplicationException("No person with the given id was found");
        }

        Hobby hobby = null;
        for (Hobby h : person.getHobbies()) {
            if (h.getId().equals(hobby_id)) {
                hobby = h;
            }
        }
        if (hobby == null) {
            throw new WebApplicationException("The hobby is not found in " + person.getFirstName() + " " + person.getLastName() + "'s hobbylist", 400);
        }

        person.removeHobby(hobby);

        int numOfPeople = getPersonCountByHobby(hobby.getName());

        try {
            em.getTransaction().begin();
            em.merge(person);

            if (numOfPeople == 1) {
                em.remove(hobby);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return new PersonDTO(person);

    }

    //This method is only public for use in test
    public List<Hobby> getHobby(String hobby) {
        EntityManager em = getEntityManager();

        TypedQuery<Hobby> query = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
        return query.setParameter("name", hobby).getResultList();

    }

    public PersonDTO addPhone(long person_id, PhoneDTO ph) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, person_id);
        if (person == null) {
            throw new WebApplicationException("No person with the given id was found");
        }

        Phone phone;
        List<Phone> phones = getPhones(ph.getPhone());
        if (phones.size() > 0) {

            throw new WebApplicationException("Phonenumber is already in use", 400);

        } else {
            phone = new Phone(ph.getPhone(), ph.getDescription());
        }
        person.addPhone(phone);

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();

            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    public PersonDTO deletePhone(long person_id, long phone_id) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, person_id);
        if (person == null) {
            throw new WebApplicationException("No person with the given id was found");
        }

        Phone phone = null;
        for (Phone p : person.getPhones()) {
            if (p.getId().equals(phone_id)) {
                phone = p;
            }
        }
        if (phone == null) {
            throw new WebApplicationException("The phone is not found in " + person.getFirstName() + " " + person.getLastName() + "'s phonelist", 400);
        }
        person.removePhone(phone);

        try {
            em.getTransaction().begin();
            em.remove(phone);
            em.merge(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    private List<Phone> getPhones(int phone) {
        EntityManager em = getEntityManager();

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.number = :number", Phone.class);
        return query.setParameter("number", phone).getResultList();

    }

}
