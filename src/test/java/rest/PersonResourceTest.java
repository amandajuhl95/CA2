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
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {

        EntityManager em = emf.createEntityManager();
        CityInfo cityInfo = new CityInfo(2200, "testTown");
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
//         

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

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

//    /**
//     * Test of getPerson (with a given id) method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testGetPerson() {
//        System.out.println("getPerson");
//          given()
//                .contentType("application/json")
//                .get("/Person/1").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", hasItems("SomeHobby", 
//                        "SomeHobby"),
//                        "firstName", equalTo("name"),
//                        "lastName", equalTo("name"));
//
//    }
//    
//
//    /**
//     * Test of getPersonByPhone method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testGetPersonByPhone() {
//        System.out.println("getPersonByPhone");
//     given()
//                .contentType("application/json")
//                .get("/Person/phone/{phone}").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", hasItems("SomeHobby", 
//                        "SomeHobby"),
//                        "firstName", equalTo("name"),
//                        "lastName", equalTo("name"));
//    }
//
//    /**
//     * Test of getPersonsByHobby method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testGetPersonsByHobby() {
//        System.out.println("getPersonsByHobby");
//         given()
//                .contentType("application/json")
//                .get("/Person/persons/{hobby}").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", hasItems("SomeHobby", 
//                        "SomeHobby"),
//                        "firstName", equalTo("name"),
//                        "lastName", equalTo("name"));
//    }
//
//    /**
//     * Test of getPersonsByCity method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testGetPersonsByCity() {
//        System.out.println("getPersonsByCity");
//         given()
//                .contentType("application/json")
//                .get("/Person/city/{city}").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", hasItems("SomeHobby", 
//                        "SomeHobby"),
//                        "firstName", equalTo("name"),
//                        "lastName", equalTo("name"));
//    }
//
//    /**
//     * Test of getNumberOfPersonsWithHobby method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testGetNumberOfPersonsWithHobby() {
//        System.out.println("getNumberOfPersonsWithHobby");
//           given()
//                .contentType("application/json")
//                .get("/Person/hobby/count/{hobby}").then()
//                .assertThat()
//                .statusCode(HttpStatus.OK_200.getStatusCode())
//                .body("hobby", equalTo("NUMBER OF PEOPLE WITH HOBBY"));
//    }
//    
//
//    /**
//     * Test of AllZipCodesInDenmark method, of class PersonResource.
//     */
//    @org.junit.Test
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
//    @org.junit.Test
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
//
//    /**
//     * Test of EditUser method, of class PersonResource.
//     */
//    @org.junit.Test
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
//
//    /**
//     * Test of addPerson method, of class PersonResource.
//     */
//    @org.junit.Test
//    public void testAddPerson() {
//        System.out.println("addPerson");
//        PersonDTO person = null;
//        PersonResource instance = new PersonResource();
//        PersonDTO expResult = null;
//        PersonDTO result = instance.addPerson(person);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
