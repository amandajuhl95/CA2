package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.HobbyDTO;
import dto.PersonDTO;
import dto.PhoneDTO;
import entities.CityInfo;
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
import java.util.List;
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
    @Operation(summary = "Retrieve person information by id", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested Person"),
                @ApiResponse(responseCode = "400", description = "Person not found")})
    
    public PersonDTO getPerson(@PathParam("id") long id) {

        return FACADE.getPersonById(id);
    }

    @GET
    @Path("/{number}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve person information by phonenumber", tags = {"Person"},
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
    @Operation(summary = "Retrieve all persons by a specific hobby", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested list of persons"),
                @ApiResponse(responseCode = "400", description = "The list of persons is not found")})

    public List<PersonDTO> getPersonsByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {

            throw new WebApplicationException("Hobby must be defined", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonsByHobby(hobby);
        return persons;
    }

    @GET
    @Path("/city/{city}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve all persons by a specific city", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested list of persons"),
                @ApiResponse(responseCode = "400", description = "The list of persons is not found")})

    public List<PersonDTO> getPersonsByCity(@PathParam("city") String city) {

        if (city == null || "".equals(city)) {

            throw new WebApplicationException("City must be defined", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonsByCity(city);
        return persons;
    }

    @GET
    @Path("/count/{hobby}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve the count of persons with a given hobby", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The count of people with that hobby"),
                @ApiResponse(responseCode = "400", description = "The hobby doesn't excist, and the list of persons is not found")})

    public int getPersonCountByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {
            throw new WebApplicationException("Hobby must be defined", 400);
        }

        return FACADE.getPersonCountByHobby(hobby);
    }

    @GET
    @Path("/zipcodes")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve a list of all zipcodes in Denmark", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The list of all zipcodes in denmark"),
                @ApiResponse(responseCode = "400", description = "The list of zipcodes not is found")})

    public List<Integer> AllZipCodesInDenmark() {

        return FACADE.getZipcodes();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Delete a person with a given id", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The person is deleted"),
                @ApiResponse(responseCode = "400", description = "The person was not found and therefor not deleted")})

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
    @Operation(summary = "Edit a person with a given id", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The person is edited"),
                @ApiResponse(responseCode = "400", description = "The person is NOT edited")})

    public PersonDTO EditUser(@PathParam("id") long id, String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (person.getId() == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }
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

        if (person.getAddInfo() == null || person.getAddInfo().isEmpty() || !person.getAddInfo().contains("[0-9]+")) {

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

            if (cityByCity == null && cityByZip != null) {
                throw new WebApplicationException("Zipcode matches another city", 400);

            } else if (cityByCity != null && cityByZip == null) {
                throw new WebApplicationException("City matches another zipcode", 400);

            } else if (!cityByCity.getId().equals(cityByZip.getId())) {

                throw new WebApplicationException("Zipcode and city matches other cities ", 400);
            }
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

        List<PersonDTO> persons = FACADE.getAllPersons();
        for (PersonDTO pDTO : persons) {
            
            if (pDTO.getEmail().equals(person.getEmail())) {
                throw new WebApplicationException("Email is already in use", 400);
            }
        }

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

            if (cityByCity == null && cityByZip != null) {
                
                throw new WebApplicationException("Zipcode matches another city", 400);

            } else if (cityByCity != null && cityByZip == null) {
                
                throw new WebApplicationException("City matches another zipcode", 400);

            } else if (!cityByCity.getId().equals(cityByZip.getId())) {

                throw new WebApplicationException("Zipcode and city matches other cities ", 400);
            }
        }

        return FACADE.addPerson(person);

    }

    @Path("/addhobby/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new hobby to the database and connect to the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A new hobby has been added to the person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })

    public PersonDTO addHobby(@PathParam("id") long id, String HobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(HobbyAsJSON, HobbyDTO.class);

        if (hobby.getHobby() == null || hobby.getHobby().length() < 2) {

            throw new WebApplicationException("Hobby must be 2 at least characters", 400);
        }

        if (hobby.getDescription() == null || hobby.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addHobby(id, hobby);

    }
    
    @Path("/deletehobby/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a hobby from a person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The hobby has been deleted from the person"),
                @ApiResponse(responseCode = "400", description = "The hobby was not found and therefor not deleted")
            })

    public String deleteHobby(@PathParam("id") long hobby_id, long person_id) {
        
        if(person_id == 0 || hobby_id == 0)
        {
            throw new WebApplicationException("Id not passed correctly", 400);
        }
        
        FACADE.deleteHobby(person_id, hobby_id);
        
        return "{\"status\": \"Hobby has been deleted\"}";

    }
    
    @Path("/addphone/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new phone to the database connected to the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A new phone has been added to the person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })

    public PersonDTO addPhone(@PathParam("id") long id, String PhoneAsJSON) {

        PhoneDTO phone = GSON.fromJson(PhoneAsJSON, PhoneDTO.class);

        if (String.valueOf(phone.getPhone()).length() != 8) {

            throw new WebApplicationException("Not a valid phone number, it must contain 8 digits", 400);
        }

        if (phone.getDescription() == null || phone.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addPhone(id, phone);

    }
    
    

}




//edit the entities in the underlying database
