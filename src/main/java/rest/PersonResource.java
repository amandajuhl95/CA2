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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new Person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The newly created Person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })

    public PersonDTO addPerson(String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().length() < 2) {

            throw new WebApplicationException("Firstname must be 2 characters", 400);
        }
        
        if (person.getFirstname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Firstname must not contain digits", 400);
        }

        if (person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().length() < 2) {

            throw new WebApplicationException("Lastname must be 2 characters", 400);
        }
        
        if (person.getLastname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Lastname must not contain digits", 400);
        }
        
        if (person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") || !person.getEmail().contains(".")) {

            throw new WebApplicationException("Please enter valid email", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonByEmail(person.getEmail());

        if (persons.size() > 0) {

            throw new WebApplicationException("Email is already in use", 400);
        }

        if (person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().matches(".*\\d+.*") || person.getStreet().length() < 3) {

            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters", 400);
        }

        if (person.getAddInfo() == null || person.getAddInfo().isEmpty() || !person.getAddInfo().matches(".*\\d+.*")) {

            throw new WebApplicationException("Housenumber must be included", 400);
        }
        if (person.getCity() == null || person.getCity().isEmpty() || person.getCity().matches(".*\\d+.*") || person.getCity().length() < 3) {

            throw new WebApplicationException("City must be at least 3 characters", 400);
        }
        if (person.getZip() < 1000 || person.getZip() > 9999) {

            throw new WebApplicationException("Zipcode must be 4 digits", 400);
        }

        List<CityInfo> cityByCity = FACADE.getCityInfo(person.getCity());
        List<CityInfo> cityByZip = FACADE.getCityInfo(String.valueOf(person.getZip()));

        if (cityByCity.size() > 0 || cityByZip.size() > 0) {

            if (cityByCity.isEmpty() && cityByZip.size() > 0) {

                throw new WebApplicationException("Zipcode matches another city", 400);

            } else if (cityByCity.size() > 0 && cityByZip.isEmpty()) {

                throw new WebApplicationException("City matches another zipcode", 400);

            } else if (!cityByCity.get(0).getId().equals(cityByZip.get(0).getId())) {

                throw new WebApplicationException("Zipcode and city matches other cities ", 400);
            }
        }

        return FACADE.addPerson(person);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Edit a person with a given id", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The person is edited"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided correctly with the body")})

    public PersonDTO editPerson(@PathParam("id") long id, String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }
        if (person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().length() < 2) {

            throw new WebApplicationException("Firstname must be 2 characters", 400);
        }
        
        if (person.getFirstname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Firstname must not contain digits", 400);
        }

        if (person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().length() < 2) {

            throw new WebApplicationException("Lastname must be 2 characters", 400);
        }
        
        if (person.getLastname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Lastname must not contain digits", 400);
        }
        
        if (person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") || !person.getEmail().contains(".")) {

            throw new WebApplicationException("Please enter valid email", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonByEmail(person.getEmail());

        if (persons.size() > 0) {

            throw new WebApplicationException("Email is already in use", 400);
        }

        if (person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().matches(".*\\d+.*") || person.getStreet().length() < 3) {

            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters", 400);
        }

        if (person.getAddInfo() == null || person.getAddInfo().isEmpty() || !person.getAddInfo().matches(".*\\d+.*")) {

            throw new WebApplicationException("Housenumber must be included", 400);
        }
        if (person.getCity() == null || person.getCity().isEmpty() || person.getCity().matches(".*\\d+.*") || person.getCity().length() < 3) {

            throw new WebApplicationException("City must be at least 3 characters", 400);
        }
        if (person.getZip() < 1000 || person.getZip() > 9999) {

            throw new WebApplicationException("Zipcode must be 4 digits", 400);
        }

        List<CityInfo> cityByCity = FACADE.getCityInfo(person.getCity());
        List<CityInfo> cityByZip = FACADE.getCityInfo(String.valueOf(person.getZip()));

        if (cityByCity.size() > 0 || cityByZip.size() > 0) {

            if (cityByCity.isEmpty() && cityByZip.size() > 0) {

                throw new WebApplicationException("Zipcode matches another city", 400);

            } else if (cityByCity.size() > 0 && cityByZip.isEmpty()) {

                throw new WebApplicationException("City matches another zipcode", 400);

            } else if (!cityByCity.get(0).getId().equals(cityByZip.get(0).getId())) {

                throw new WebApplicationException("Zipcode and city matches other cities ", 400);
            }
        }

        person.setId(id);
        return FACADE.editPerson(person);
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Delete a person with a given id", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The person is deleted"),
                @ApiResponse(responseCode = "400", description = "The person was not found and therefor not deleted")})

    public String deletePerson(@PathParam("id") long id) {

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }

        FACADE.deletePerson(id);
        
        return "{\"status\": \"Person has been deleted\"}";
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
        return p;
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

        PersonDTO p = FACADE.getPersonById(id);
        return p;
    }

    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve all persons", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested list of persons")})

    public List<PersonDTO> getAllPersons() {

        List<PersonDTO> persons = FACADE.getAllPersons();
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
    @Path("/count/{hobby}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve the amount of persons with a given hobby", tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The count of people with that hobby"),
                @ApiResponse(responseCode = "400", description = "The hobby doesn't excist, and the list of persons is not found")})

    public String getPersonCountByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {
            throw new WebApplicationException("Hobby must be defined", 400);
        }

        return "{\"count\":" + FACADE.getPersonCountByHobby(hobby) + "}";
    }

    @GET
    @Path("/zipcodes")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve a list of all zipcodes in Denmark", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The list of all zipcodes in denmark")})

    public String allZipCodesInDenmark() {

        return "{\"zipcodes\":" + FACADE.getZipcodes() + "}";
    }

    @POST
    @Path("/addhobby/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new hobby to the database and connect to the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A new hobby has been added to the person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided correctly with the body")
            })

    public PersonDTO addHobby(@PathParam("id") long person_id, String HobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(HobbyAsJSON, HobbyDTO.class);

        if (person_id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        if (hobby.getHobby() == null || hobby.getHobby().length() < 2) {

            throw new WebApplicationException("Hobby must be 2 at least characters", 400);
        }

        if (hobby.getDescription() == null || hobby.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addHobby(person_id, hobby);

    }

    @DELETE
    @Path("/deletehobby/{person_id}/" + "{hobby_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a hobby from a person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A hobby has been deleted from the person"),
                @ApiResponse(responseCode = "400", description = "A hobby or the person was not found and therefor not deleted")
            })

    public PersonDTO deleteHobby(@PathParam("person_id") long person_id, @PathParam("hobby_id") long hobby_id) {

        if (person_id == 0 || hobby_id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        return FACADE.deleteHobby(person_id, hobby_id);
    }

    @POST
    @Path("/addphone/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new phone to the database connected to the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A new phone has been added to the person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided correctly with the body")
            })

    public PersonDTO addPhone(@PathParam("id") long person_id, String PhoneAsJSON) {

        PhoneDTO phone = GSON.fromJson(PhoneAsJSON, PhoneDTO.class);

        if (person_id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        if (phone.getPhone() == 0 || String.valueOf(phone.getPhone()).length() != 8) {

            throw new WebApplicationException("Not a valid phone number, it must contain 8 digits", 400);
        }

        if (phone.getDescription() == null || phone.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addPhone(person_id, phone);

    }

    @DELETE
    @Path("/deletephone/{person_id}/{phone_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a phone from the database connected to the person", tags = {"Person"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "A phone has been deleted from the person"),
                @ApiResponse(responseCode = "400", description = "A phone or the person was not found and therefor not deleted")
            })

    public PersonDTO deletePhone(@PathParam("person_id") long person_id, @PathParam("phone_id") long phone_id) {

        if (person_id == 0 || phone_id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        return FACADE.deletePhone(person_id, phone_id);

    }
}
