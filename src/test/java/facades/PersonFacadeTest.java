
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private Hobby hobby2;
    private Hobby hobby1;
    private Phone phone1;
    private Phone phone2;
    private Address address1;
    private Address address2;

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
        
        cityInfo = new CityInfo(2200, "testTown");
        
        address1 = new Address("streetname", "4 tv", cityInfo);
        address2 = new Address("gadevejen", "1 th", cityInfo);
        p1 = new Person("jim@gmail.com", "jim", "theMan", address1);
        p2 = new Person("bill@gmail.com", "bill", "LastName", address2);
        phone1 = new Phone(22112211, "workPhone");
        phone2 = new Phone(99889988, "privatePhone");
        hobby1 = new Hobby("programming", "the future of mankind is programming, also good for making a blog about your dog pictures");
        hobby2 = new Hobby("jumping", "super fun and easy");
        
        cityInfo.addAddress(address1);
        p1.addHobby(hobby1);
        p1.addHobby(hobby2);
        p1.addPhone(phone1);
    

        cityInfo.addAddress(address2);
        p2.addHobby(hobby2);
        p2.addHobby(hobby1);
        p2.addPhone(phone2);


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
        assertEquals("jim@gmail.com", result.getEmail());
        assertEquals(2, result.getHobbies().size());
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
    @Test
    public void testGetPersonsByCity() {
        System.out.println("getPersonsByCity");

        int expResult = 2;
        int result = facade.getPersonsByCity(p1.getAddress().getCityInfo().getCity()).size();
        assertEquals(expResult, result);

    }

    /**
     * Test of getPersonCountByHobby method, of class PersonFacade.
     */
    @Test
    public void testGetPersonCountByHobby() {
        System.out.println("getPersonCountByHobby");
        String hobby = "jumping";

        long expResult = 2;
        long result = facade.getPersonCountByHobby(hobby);
        assertEquals(expResult, result);

    }

    /**
     * Test of getZipcodes method, of class PersonFacade.
     */
    @Test
    public void testGetZipcodes() {
        System.out.println("getZipcodes");

        int expResult = 2200;
        List<Integer> result = facade.getZipcodes();
        assertEquals(expResult, (int) result.get(0));

    }

    /**
     * Test of getPersonsByAdress method, of class PersonFacade.
     */
    @Test
    public void testGetPersonsByAddress() {
        System.out.println("getPersonsByAddress");

        String street = "streetname";
        String addinfo = "4 tv";

        List<PersonDTO> persondto = facade.getPersonsByAddress(street, addinfo);
        assertEquals(1, persondto.size());

    }

    /**
     * Test of addPerson method, of class PersonFacade.
     */
    @Test
    public void testAddPerson() {
        System.out.println("addPerson");
      
        int personsbefore = facade.getAllPersons().size();
        //CityInfo info = new CityInfo(3223, "thetesttown");
      
        facade.addPerson("joe", "ordenary", "test@testmail.dk", address1.getStreet(), address1.getAddinfo(), cityInfo.getCity(), cityInfo.getZip());
        
        int personsafter = facade.getAllPersons().size();

        assertTrue(personsbefore < personsafter);
        
        
    }

    /**
     * Test of deletePerson method, of class PersonFacade.
     */
    @Test
    public void testDeletePerson() {
        System.out.println("deletePerson");
          
        int personsbefore = facade.getAllPersons().size();
        
        
        facade.deletePerson(p1.getId());
        
        int personsafter = facade.getAllPersons().size();

        assertTrue(personsbefore > personsafter);
        
    }

//    /**
//     * Test of editPerson method, of class PersonFacade.
//     */
//    @Test
//    public void testEditPerson() {
//        System.out.println("editPerson");
//        Person person = null;
//        PersonFacade instance = null;
//        Person expResult = null;
//        Person result = instance.editPerson(person);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of addHobby method, of class PersonFacade.
     */
    @Test
    public void testAddHobby() {

        System.out.println("addHobby");

        int hobbiesbefore = p1.getHobbies().size();
        p1 = facade.addHobby(p1.getId(), "Handball", "A team sport. The team with the highest score wins");
        int hobbiesafter = p1.getHobbies().size();
        
        assertTrue(hobbiesbefore < hobbiesafter);
    }

    /**
     * Test of deleteHobby method, of class PersonFacade.
     */
    @Test
    public void testDeleteHobby() {

        System.out.println("deleteHobby");

        int hobbiesbefore = p1.getHobbies().size();
        p1 = facade.deleteHobby(p1.getId(), hobby1.getId());
        int hobbiesafter = p1.getHobbies().size();
        assertTrue(hobbiesbefore > hobbiesafter);
    }

    /**
     * Test of addPhone method, of class PersonFacade.
     */
    @Test
    public void testAddPhone() {

        System.out.println("addPhone");

        int phonesbefore = p2.getPhones().size();
        p2 = facade.addPhone(p2.getId(), 22334455, "Work phone");
        int phonesafter = p2.getPhones().size();
        
        assertTrue(phonesbefore < phonesafter);
    }

    /**
     * Test of deletePhone method, of class PersonFacade.
     */
    @Test
    public void testDeletePhone() {

        System.out.println("deletePhone");

        int phonesbefore = p2.getPhones().size();
        p2 = facade.deletePhone(p2.getId(), phone2.getId());
        int phonesafter = p2.getPhones().size();
        assertTrue(phonesbefore > phonesafter);
    }

    /**
     * Test of getPersonById method, of class PersonFacade.
     */
    @Test
    public void testGetPersonById() {
        System.out.println("getPersonById");

        PersonDTO personDTO = facade.getPersonById(p1.getId());

        assertFalse(personDTO == null);
        assertEquals("jim@gmail.com", personDTO.getEmail());
        assertEquals("jim", personDTO.getFirstname());
        assertEquals(2, personDTO.getHobbies().size());
    }

}
