package facades;

import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
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
    
    public PersonDTO getPerson(int number)
    {
        EntityManager em = getEntityManager();
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.phones.number = :number", Person.class);
        Person person = query.setParameter("number", number).getSingleResult();
        PersonDTO personDTO = new PersonDTO(person);

        return personDTO;
    }
    
    public List<Person> getPersonsByHobby(String hobby)
    {
        return new ArrayList<Person>();
    }
    
    public List<Person> getPersonsByCity(CityInfo city)
    {
       return new ArrayList<Person>();
    } 
    
    public int getPersonCountByHobby(String hobby)
    {
        return 0;
    }
    
    public List<Integer> getZipcodes()
    {
        return new ArrayList<Integer>();
    }
    
    
    public List<Person> getPersonsByAdress(Address address)
    {
       return new ArrayList<Person>();
    } 
    

}
