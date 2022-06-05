package com.codingaxis.hr.web.rest;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.hr.security.jwt.TokenProvider;
import com.codingaxis.hr.service.AuthenticationService;
import com.codingaxis.hr.web.rest.vm.LoginVM;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;

/**
 * Controller to authenticate users.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserJWTController {
  private final Logger log = LoggerFactory.getLogger(UserJWTController.class);

  final AuthenticationService authenticationService;

  final TokenProvider tokenProvider;

  @Inject
  public UserJWTController(AuthenticationService authenticationService, TokenProvider tokenProvider) {

    this.authenticationService = authenticationService;
    this.tokenProvider = tokenProvider;
  }

  @POST
  @Path("/authenticate")
  @PermitAll
  public Response authorize(@Valid LoginVM loginVM) {

    System.out.println("LOGINVM:" + loginVM);
    try {
      QuarkusSecurityIdentity identity = this.authenticationService.authenticate(loginVM.username, loginVM.password);
      boolean rememberMe = (loginVM.rememberMe == null) ? false : loginVM.rememberMe;
      String jwt = this.tokenProvider.createToken(identity, rememberMe);
      return Response.ok().entity(new JWTToken(jwt)).header("Authorization", "Bearer " + jwt).build();
    } catch (SecurityException e) {
      return Response.status(401).build();
    }
  }

  /**
   * Object to return as body in JWT Authentication.
   */
  @RegisterForReflection
  public static class JWTToken {
    @JsonbProperty("id_token")
    public String idToken;

    JWTToken(String idToken) {

      this.idToken = idToken;
    }
  }
}
