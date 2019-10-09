package facades;

import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;
import errorhandling.ExceptionDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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

    public Person addPerson(Person person) {

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            return person;
        } finally {
            em.close();
        }
    }

    public Person deletePerson(long id) {

        EntityManager em = getEntityManager();
        Person person;

        try {
            em.getTransaction().begin();
            person = em.find(Person.class, id);
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return person;
    }

    public Person editPerson(Person person) {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return person;
        } finally {
            em.close();
        }

    }

    public PersonDTO getPersonById(long id) {
        
        EntityManager em = getEntityManager();
        
        try {
            Person person = em.find(Person.class, id);
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

    public PersonDTO getPerson(int number) {

        EntityManager em = getEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.phones ph WHERE ph.number = :number", Person.class);
        Person person = query.setParameter("number", number).getSingleResult();
        PersonDTO personDTO = new PersonDTO(person);

        return personDTO;
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

    private CityInfo getCityInfo(String city) {

        EntityManager em = getEntityManager();

        if (city.matches("[0-9]+")) {
            int zip = Integer.parseInt(city);
            TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c WHERE c.zip = :zip", CityInfo.class);
            CityInfo cityInfo = query.setParameter("zip", zip).getSingleResult();
            return cityInfo;
        } else {
            TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c WHERE c.city = :city", CityInfo.class);
            CityInfo cityInfo = query.setParameter("city", city).getSingleResult();
            return cityInfo;
        }

    }

    public List<PersonDTO> getPersonsByCity(String city) {

        EntityManager em = getEntityManager();

        CityInfo cityInfo = getCityInfo(city);
        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.address a WHERE a.cityInfo.id = :city", Person.class);
        List<Person> persons = query.setParameter("city", cityInfo.getId()).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

    public Long getPersonCountByHobby(String hobby) {

        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT COUNT(p) FROM Person p INNER JOIN p.hobbies pho WHERE pho.name = :hobby");
        Long count = (Long) query.setParameter("hobby", hobby).getSingleResult();
        return count;
    }

    public List<Integer> getZipcodes() {

        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT c.zip FROM CityInfo c");
        List<Integer> zipcodes = query.getResultList();
        return zipcodes;

    }

    public List<PersonDTO> getPersonsByAdress(String street, String addinfo) {

        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.address a WHERE a.street = :street AND a.addinfo = :addinfo", Person.class);
        List<Person> persons = query.setParameter("street", street).setParameter("addinfo", addinfo).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

}
