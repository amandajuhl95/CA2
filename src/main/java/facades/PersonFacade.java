package facades;

import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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

    public List<PersonDTO> getPersonsByCity(CityInfo city) {
        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        String theCity = city.getCity();
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.address pa WHERE pa.cityInfo.city = :theCity", Person.class);
        List<Person> persons = query.setParameter("theCity", theCity).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));

        }
        return personsDTO;
    }

    public int getPersonCountByHobby(String hobby) {
        return 0;
    }

    public List<Integer> getZipcodes() {
        return new ArrayList<Integer>();
    }

    public List<Person> getPersonsByAdress(Address address) {
        return new ArrayList<Person>();
    }

}
