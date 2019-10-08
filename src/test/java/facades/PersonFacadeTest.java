package facades;

import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.List;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.Settings;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    
     private Person p1;
    private Person p2;
    private CityInfo cityInfo;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
     
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
  public void setUp() {
        
         EntityManager em = emf.createEntityManager();
         cityInfo = new CityInfo(2200,"testTown");
         Address address = new Address("streetname", "4 tv", cityInfo);
         Phone phone = new Phone(22112211, "workPhone");
         Hobby hoppy = new Hobby("programming", "the future of mankind is programming, also good for making a blog about your dog pictures");
         p1 = new Person("jim@gmail.com", "jim", "theMan", address);
         
       
         
         
         Address address2 = new Address("gadevejen", "1 th", cityInfo);       
         Phone phone2 = new Phone(99889988, "privatePhone");
         Hobby hoppy2 = new Hobby("jumping", "super fun and easy");
         p2 = new Person("bill@gmail.com", "bill", "LastName", address2);
         
         cityInfo.addAddress(address);
         p1.addHobby(hoppy);
         p1.addHobby(hoppy2);
         p1.addPhone(phone);
         address.addPerson(p1);
         hoppy.addPerson(p1);
         phone.setPerson(p1);
         
         cityInfo.addAddress(address2);
         p2.addHobby(hoppy2);
         p2.addHobby(hoppy);
         p2.addPhone(phone2);
         address2.addPerson(p2);
         hoppy.addPerson(p2);
         hoppy2.addPerson(p2);
         phone2.setPerson(p2);
         
         

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();

              em.persist(p1);
               em.persist(p2);
          
           
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        
    }
  
    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    /**
     * Test of getFacadeExample method, of class PersonFacade.
     */
//    @Test
//    public void testGetFacadeExample() {
//        System.out.println("getFacadeExample");
//        EntityManagerFactory _emf = null;
//        PersonFacade expResult = null;
//        PersonFacade result = PersonFacade.getFacadeExample(_emf);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getPerson method, of class PersonFacade.
     */
    @Test
    public void testGetPerson() {
        System.out.println("getPerson");
        int number = 22112211;

        String expResult = "jim";
        PersonDTO result = facade.getPerson(number);
        assertEquals(expResult, result.getFirstname());
        
        assertEquals("the future of mankind is programming, also good for making a blog about your dog pictures", result.getHobbies().get("programming"));
    
    }

    /**
     * Test of getPersonsByHobby method, of class PersonFacade.
     */
    @Test
    public void testGetPersonsByHobby() {
        System.out.println("getPersonsByHobby");
        String hobby = "jumping";
        
        int expResult = 2;
        int result = facade.getPersonsByHobby(hobby).size();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getPersonsByCity method, of class PersonFacade.
     */
//    @Test
//    public void testGetPersonsByCity() {
//        System.out.println("getPersonsByCity");
//        
//        CityInfo info = p1.getAddress().getCityInfo();
//        int expResult = 2;
//        int result = facade.getPersonsByCity(info).size();
//        assertEquals(expResult, result);
//        
//    }

    /**
     * Test of getPersonCountByHobby method, of class PersonFacade.
     */
//    @Test
//    public void testGetPersonCountByHobby() {
//        System.out.println("getPersonCountByHobby");
//        String hobby = "";
//        PersonFacade instance = null;
//        int expResult = 0;
//        int result = instance.getPersonCountByHobby(hobby);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getZipcodes method, of class PersonFacade.
//     */
//    @Test
//    public void testGetZipcodes() {
//        System.out.println("getZipcodes");
//        PersonFacade instance = null;
//        List<Integer> expResult = null;
//        List<Integer> result = instance.getZipcodes();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPersonsByAdress method, of class PersonFacade.
//     */
//    @Test
//    public void testGetPersonsByAdress() {
//        System.out.println("getPersonsByAdress");
//        Address address = null;
//        PersonFacade instance = null;
//        List<Person> expResult = null;
//        List<Person> result = instance.getPersonsByAdress(address);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
