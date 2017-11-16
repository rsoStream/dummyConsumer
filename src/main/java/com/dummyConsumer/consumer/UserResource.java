package com.dummyConsumer.consumer;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.crypto.Data;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/users")
@ApplicationScoped
public class UserResource {

    @Inject
    private ConfigProperties properties;

    @GET
    @Path("/config")
    public Response test() {
        String response =
                "{" +
                        "\"stringProperty\": \"%s\"," +
                        "\"booleanProperty\": %b," +
                        "\"integerProperty\": %d" +
                        "}";

        response = String.format(
                response,
                properties.getStringProperty(),
                properties.getBooleanProperty(),
                properties.getIntegerProperty());

        return Response.ok(response).build();
    }

    @GET
    @Path("/get")
    public Response get() {
        return Response.ok(ConfigurationUtil.getInstance().get("rest-config.string-property").orElse("nope")).build();
    }

    @GET
    public Response getAllUsers() {
        List<User> users = DataBase.getUsers();
        return Response.ok(users).build();
    }

    @POST
    public Response createNewUser(User user) {
        DataBase.addUser(user);
        return Response.ok(true).build();
    }

    @GET
    @Path("{id}")
    public Response getUserById(
            @PathParam("id") int id
    ) {
        System.out.println(id);
        User user = DataBase.getUser(id);
        return Response.ok(user).build();
    }

    @POST
    @Path("create")
    public Response createDummyUsers() {
        if (DataBase.isEmpty()) {
            System.out.println("Creating dummy users.");

            User m = new User();
            m.setId(0);
            m.setFirstName("Marko");
            m.setLastName("P");
            m.setMeta("[b]New feature 1");

            User m1 = new User();
            m1.setId(1);
            m1.setFirstName("Luka");
            m1.setLastName("P");
            m1.setMeta("[c]New feature 2");

            DataBase.addUser(m);
            DataBase.addUser(m1);
        }

        return Response.ok(DataBase.getUsers()).build();
    }

    @GET
    @Path("movies")
    public Response getUserByIdTest() {
        System.out.println("Getting user movies");
        Client client = ClientBuilder.newClient();
        Response resp = client
                .target("http://localhost:8081/v1/producer/") // TODO: add service discovery
//                .path(String.valueOf(id)) // sets a parameter in the url -> .../users/{id}
             // .queryParam("id", "1") // sets a query parameter as in ?id=<someID>
                .request(MediaType.APPLICATION_JSON)
                .get();
        return Response.ok(resp.getEntity()).build();
    }
}
