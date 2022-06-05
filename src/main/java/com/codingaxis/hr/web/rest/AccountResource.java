package com.codingaxis.hr.web.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.hr.service.MailService;
import com.codingaxis.hr.service.UserService;
import com.codingaxis.hr.service.dto.UserDTO;

/**
 * REST controller for managing the current user's account.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AccountResource {
  private final Logger log = LoggerFactory.getLogger(AccountResource.class);

  private static class AccountResourceException extends RuntimeException {

    private AccountResourceException(String message) {

      super(message);
    }
  }

  final MailService mailService;

  final UserService userService;

  @Inject
  public AccountResource(MailService mailService, UserService userService) {

    this.mailService = mailService;
    this.userService = userService;
  }

  /**
   * {@code GET /account} : get the current user.
   *
   * @return the current user.
   * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
   */
  @GET
  @Path("/account")
  public UserDTO getAccount(@Context SecurityContext ctx) {

    return this.userService.getUserWithAuthoritiesByLogin(ctx.getUserPrincipal().getName()).map(UserDTO::new)
        .orElseThrow(() -> new AccountResourceException("User could not be found"));
  }

}
