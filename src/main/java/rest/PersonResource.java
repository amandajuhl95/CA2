package rest;

import dto.PersonDTO;
import utils.EMF_Creator;
import facades.FacadeExample;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
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
                contact = @Contact(name = "Choko Bananen", email = "cph-ah433@cphbusiness.dk")
        ),
        tags = {
            @Tag(name = "Person", description = "API related to Person Info")

        },
        servers = {
            @Server(
                    description = "For Local host testing",
                    url = "http://localhost:8080/CA2"
            ),
            @Server(
                    description = "Server API",
                    url = "http://mydroplet"
            )

        }
)

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final FacadeExample FACADE = FacadeExample.getFacadeExample(EMF);

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo()
    {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get Person info by ID",
            tags = {"Person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested Person"),
                @ApiResponse(responseCode = "400", description = "Person not found")})

    public PersonDTO getPerson(@PathParam("id") long id) {
        PersonDTO p = new PersonDTO("Rolf", "Trump", "trump-4-ever@hotmail.com", "I-hope-u-dont-agree-with-me");
        p.addHobby("Programming");
        p.addHobby("Dicatorship");
        p.addHobby("Antifa");
        p.addPhone("+61 4231528302");
        p.addPhone("+45 67854231");

        return p;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get Person info by ID",tags = {"Person"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "The Newly created Person"),                       
                    @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })
    
    public PersonDTO addPerson(PersonDTO person){
        if(person.getFirstname() == null || person.getLastname()== null || person.getEmail() == null || person.getAddress()== null){
            throw new WebApplicationException("Not all required arguments included",400);
        }
        person.setId(464);
        return person;
    }



}
