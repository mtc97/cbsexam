package com.cbsexam;

import cache.UserCache;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Hashing;
import utils.Log;

@Path("user")
public class UserEndpoints {

  private static UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON fix
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? fix
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    }else{
      return Response.status(400).entity("Could not return user").build();
    }

  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(true);

    // TODO: Add Encryption to JSON fix
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. fix
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    User userLogin = new Gson().fromJson(body, User.class);

    String token = UserController.loginUsers(userLogin);

    if(token != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Logged in" + token).build();
    }else {
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("Could not login").build();
    }
  }

  // TODO: Make the system able to delete users fix
  @DELETE
  @Path("/delete/{user_id}")
  public Response deleteUser(@PathParam("user_id")int id, String body) {

    DecodedJWT token = UserController.verifier(body);

    Boolean delete = UserController.deleteUser(token.getClaim("test").asInt());

    if(delete) {
      userCache.getUsers(true);
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("User " + id + "has been removed from the webshop").build();
    }else{
      return Response.status(400).entity("User has not been found").build();
    }
  }

  // TODO: Make the system able to update users fix
  @POST
  @Path("/update/{user_id}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("user_id") int userId, @PathParam("token") String token, String body) {

    User user = new Gson().fromJson(body, User.class);

    DecodedJWT jwt = UserController.verifier(token);

    Boolean update = UserController.updateUser(user, jwt.getClaim("test").asInt());

    userCache.getUsers(true);
    if(update){
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("User " + userId + "has been updated").build();
    }else{
      return Response.status(400).entity("User has not been found").build();
    }
  }



}
