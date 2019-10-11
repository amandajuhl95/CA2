package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.HobbyDTO;
import dto.PersonDTO;
import entities.CityInfo;
import entities.Person;
import utils.EMF_Creator;
import facades.PersonFacade;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.eclipse.persistence.jpa.jpql.parser.ElseExpressionBNF;

@OpenAPIDefinition(
        info = @Info(
                title = "Person API",
                version = "0.1",
                description = "Simple API to get info about persons with hobbies.",
                contact = @Contact(name = "Choko Bananen", email = "cph-ah433@cphbusiness.dk")),
        tags = {
            @Tag(name = "Person", description = "API related to Person Info")},
        servers = {
            @Server(description = "For Local host testing",
                    url = "http://localhost:8080/CA2"),
            @Server(description = "Server API",
                    url = "http://mydroplet")}
)

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public PersonDTO getPerson(@PathParam("id") long id) {

        return FACADE.getPersonById(id);
    }

    @GET
    @Path("/{number}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get Person info by phonenumber",
            tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested Person"),
                @ApiResponse(responseCode = "400", description = "Person not found")})

    public PersonDTO getPerson(@PathParam("number") int number) {
        PersonDTO p = FACADE.getPerson(number);

        if (p == null) {
            throw new WebApplicationException("No person found with the given phonenumber", 400);
        }

        return p;
    }

    @GET
    @Path("/hobby/{hobby}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get all persons info by hobby",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested List of persons"),
                @ApiResponse(responseCode = "400", description = "List of Persons not found")})

    public List<PersonDTO> getPersonsByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {

            throw new WebApplicationException("Hobby must be defined", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonsByHobby(hobby);
        return persons;
    }

    //get all persons from a specific city
    @GET
    @Path("/city/{city}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get all persons info by city",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested List of persons"),
                @ApiResponse(responseCode = "400", description = "List of Persons not found")})

    public List<PersonDTO> getPersonsByCity(@PathParam("city") String city) {

        if (city == null || "".equals(city)) {

            throw new WebApplicationException("City must be defined", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonsByCity(city);
        return persons;
    }

    //count of persons with given hobby
    @GET
    @Path("/count/{hobby}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "number of persons with a given hobby",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "the number of people with that hobby"),
                @ApiResponse(responseCode = "400", description = "List of Persons not found")})

    public int getPersonCountByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {
            throw new WebApplicationException("Hobby must be defined", 400);
        }

        return FACADE.getPersonCountByHobby(hobby);
    }

    @GET
    @Path("/zipcodes")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "a list of all zipcodes in Denmark",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "all zipcodes in denmark"),
                @ApiResponse(responseCode = "400", description = "List of zipcodes not found")})

    public List<Integer> AllZipCodesInDenmark() {

        return FACADE.getZipcodes();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Delete a person with a given id",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "person was deleted"),
                @ApiResponse(responseCode = "400", description = "wrong id passed")})

    public String DeleteUser(@PathParam("id") long id) {

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }

        FACADE.deletePerson(id);

        return "{\"status\": \"Person has been deleted\"}";
    }

    @PUT
    @Path("/edit/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Edit a person with a given id",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "person was edited"),
                @ApiResponse(responseCode = "400", description = "person was NOT editet")})

    public PersonDTO EditUser(@PathParam("id") long id, String personAsJSON) {
        
        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);
        
        if (person.getId() == 0){
              
            throw new WebApplicationException("Id not passed correctly",400);
        }
        if(person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().contains("[0-9]+") || person.getFirstname().length() < 2){
            
            throw new WebApplicationException("Firstname must be 2 characters",400);
        }
        
        if(person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().contains("[0-9]+") || person.getLastname().length() < 2){
            
            throw new WebApplicationException("Lastname must be 2 characters",400);
        }
        if(person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") && !person.getEmail().contains(".")){
            
            throw new WebApplicationException("Please enter valid email",400);
        }
        
        if(person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().contains("[0-9]+") || person.getStreet().length() < 3){
            
            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters",400);
        }
        
        if(person.getAddInfo() == null || person.getAddInfo().isEmpty() || !person.getAddInfo().contains("[0-9]+")){
            
            throw new WebApplicationException("Housenumber must be included",400);
        }
        if(person.getCity() == null || person.getCity().isEmpty() || person.getCity().contains("[0-9]+") || person.getCity().length() < 3){
            
            throw new WebApplicationException("City must be at least 3 characters",400);
        }
        if(person.getZip() < 1000 || person.getZip() > 9999){
            
            throw new WebApplicationException("Zipcode must be 4 digits",400);
        }
       
        person.setId(id);
        return FACADE.editPerson(person);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new Person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Newly created Person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })

    public PersonDTO addPerson(String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().contains("[0-9]+") || person.getFirstname().length() < 2) {

            throw new WebApplicationException("Firstname must be 2 characters", 400);
        }

        if (person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().contains("[0-9]+") || person.getLastname().length() < 2) {

            throw new WebApplicationException("Lastname must be 2 characters", 400);
        }
        if (person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") && !person.getEmail().contains(".")) {

            throw new WebApplicationException("Please enter valid email", 400);
        }

        if (person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().contains("[0-9]+") || person.getStreet().length() < 3) {

            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters", 400);
        }

        if (person.getAddInfo() == null || person.getAddInfo().isEmpty()) {

            throw new WebApplicationException("Housenumber must be included", 400);
        }
        if (person.getCity() == null || person.getCity().isEmpty() || person.getCity().contains("[0-9]+") || person.getCity().length() < 3) {

            throw new WebApplicationException("City must be at least 3 characters", 400);
        }
        if (person.getZip() < 1000 || person.getZip() > 9999) {

            throw new WebApplicationException("Zipcode must be 4 digits", 400);
        }
         
        CityInfo cityByCity = FACADE.getCityInfo(person.getCity());
         CityInfo cityByZip = FACADE.getCityInfo(String.valueOf(person.getZip()));
         
          if (cityByCity != null || cityByZip != null) {
              
              if(cityByCity == null && cityByZip != null)
              {
                  throw new WebApplicationException("Zipcode matches another city", 400);
              }
              else if(cityByCity != null && cityByZip == null)
              {
                  throw new WebApplicationException("City matches another zipcode", 400);
              }
              else if(!cityByCity.getId().equals(cityByZip.getId())){
              
                  throw new WebApplicationException("Zipcode and city matches other cities ", 400);
          }
        }

        return FACADE.addPerson(person);

    }

    
    @Path("/addhobby/{id}")
      @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "adds a new hobby to the database and the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "a new hobby has been added to the person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })

    public Person addhobby(@PathParam("id") long id, String HobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(HobbyAsJSON, HobbyDTO.class);

        if (hobby.getHobby() == null ||  hobby.getHobby().length() < 2 ) {

            throw new WebApplicationException("hobby must be 2 characters", 400);
        }

        if ( hobby.getDescription() == null ||  hobby.getDescription().length() < 2) {

            throw new WebApplicationException("description must be 2 characters", 400);
        }

        return FACADE.addHobby(id, hobby.getHobby(), hobby.getDescription());

    }
    
}




//edit the entities in the underlying database
