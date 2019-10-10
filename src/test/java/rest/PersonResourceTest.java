package rest;

import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
// @Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private Person p1;
    private Person p2;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        // Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    //Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    // TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {

        EntityManager em = emf.createEntityManager();
        CityInfo cityInfo = new CityInfo(2200, "testTown");
        Address address = new Address("streetname", "4 tv", cityInfo);
        Phone phone = new Phone(22112211, "workPhone");
        Hobby hobby = new Hobby("programming", "the future of mankind is programming, also good for making a blog about your dog pictures");
        p1 = new Person("jim@gmail.com", "jim", "theMan", address);

        Address address2 = new Address("gadevejen", "1 th", cityInfo);
        Phone phone2 = new Phone(99889988, "privatePhone");
        Hobby hobby2 = new Hobby("jumping", "super fun and easy");
        p2 = new Person("bill@gmail.com", "bill", "LastName", address2);

        cityInfo.addAddress(address);
        p1.addHobby(hobby);
        p1.addHobby(hobby2);
        p1.addPhone(phone);
        address.addPerson(p1);
        hobby.addPerson(p1);
        phone.setPerson(p1);

        cityInfo.addAddress(address2);
        p2.addHobby(hobby2);
        p2.addHobby(hobby);
        p2.addPhone(phone2);
        address2.addPerson(p2);
        hobby.addPerson(p2);
        hobby2.addPerson(p2);
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

    private List<String> hobbyToString(List<Hobby> hobbies) {
        List<String> hobbyNames = new ArrayList();

        for (Hobby hobby : hobbies) {

            hobbyNames.add(hobby.getName());
        }

        return hobbyNames;
    }

    private List<String> phoneToString(List<Phone> phones) {
        List<String> phoneNumbers = new ArrayList();

        for (Phone phone : phones) {

            phoneNumbers.add(String.valueOf(phone.getNumber()));
        }

        return phoneNumbers;
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

    /**
     * Test of getPerson method, of class PersonResource.
     */
    @Test
    public void testGetPerson() {
        System.out.println("getPerson");
        given()
                .contentType("application/json")
                .get("/person/22112211").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo(p1.getFirstName()),
                        "lastname", equalTo(p1.getLastName()),
                        "hobbies.hobby", hasItems("programming"));
    }

    /**
     * Test of getPersonsByHobby method, of class PersonResource.
     */
    @Test
    public void testGetPersonsByHobby() {
        System.out.println("getPersonsByHobby");
        given()
                .contentType("application/json")
                .get("/person/hobby/programming").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", hasItems(p1.getFirstName(),
                        p2.getFirstName()),
                        "lastname", hasItems(p1.getLastName(),
                                p2.getLastName()));
    }

    /**
     * Test of getPersonsByCity method, of class PersonResource.
     */
//    @Test
//    public void testGetPersonsByCity() {
//        System.out.println("getPersonsByCity");
//        given()
//                .contentType("application/json")
//                .get("/person/city/" + p1.getAddress().getCityInfo().getZip()).then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("firstname", hasItems(p1.getFirstName(),
//                        p2.getFirstName()),
//                        "lastname", hasItems(p1.getLastName(),
//                                p2.getLastName()),
//                        "hobbies.hobby", hasItems(hobbyToString(p1.getHobbies()),
//                                hobbyToString(p2.getHobbies())));
//    }

//    /**
//     * Test of getNumberOfPersonsWithHobby method, of class PersonResource.
//     */
//    @Test
//    public void testGetPersonCountByHobby() {
//        System.out.println("getPersonCountByHobby");
//           given()
//                .contentType("application/json")
//                .get("/person/count/programming").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body(equalTo((int) 2));
//    }
    
//
//    /**
//     * Test of AllZipCodesInDenmark method, of class PersonResource.
//     */
//    @Test
//    public void testAllZipCodesInDenmark() {
//        System.out.println("AllZipCodesInDenmark");
//         given()
//                .contentType("application/json")
//                .get("/Person/allzip").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", hasItems("Somezip", 
//                        "Somezip",
//                        "Somezip"));
//    }
//
//    /**
//     * Test of DeleteUser method, of class PersonResource.
//     */
//    @Test
//    public void testDeleteUser() {
//        System.out.println("DeleteUser");
//        String id = "";
//        PersonResource instance = new PersonResource();
//        PersonDTO expResult = null;
//        PersonDTO result = instance.DeleteUser(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of EditUser method, of class PersonResource.
//     */
//    @Test
//    public void testEditUser() {
//        System.out.println("EditUser");
//        PersonDTO person = null;
//        PersonResource instance = new PersonResource();
//        PersonDTO expResult = null;
//        PersonDTO result = instance.EditUser(person);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of addPerson method, of class PersonResource.
     */
    @Test
    public void testAddPerson() {
        String payload = "{\"firstname\": \"Test\","
                + "\"lastname\": \"Testen\","
                + "\"email\": \"bum@hotmail.com\","
                + "\"street\": \"Testvej\","
                + "\"addInfo\": \"1 tv\","
                + "\"city\": \"Testby\","
                + "\"zip\": \"2230\"}";

        given().contentType("application/json")
                .body(payload)
                .post("/person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Test"), "lastname", equalTo("Testen"), "email", equalTo("bum@hotmail.com"), "street", equalTo("Testvej"), "addInfo", equalTo("1 tv"), "city", equalTo("Testby"));
    
    }
}
