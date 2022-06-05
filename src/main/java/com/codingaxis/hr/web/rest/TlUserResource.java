package com.codingaxis.hr.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.hr.domain.TlUser;
import com.codingaxis.hr.web.rest.errors.BadRequestAlertException;
import com.codingaxis.hr.web.util.HeaderUtil;
import com.codingaxis.hr.web.util.ResponseUtil;

import io.quarkus.security.Authenticated;

/**
 * REST controller for managing {@link com.codingaxis.hr.domain.TlUser}.
 */
@Path("/api/tl-users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TlUserResource {

  private final Logger log = LoggerFactory.getLogger(TlUserResource.class);

  private static final String ENTITY_NAME = "talencyTlUser";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /tl-users} : Create a new tlUser.
   *
   * @param tlUser the tlUser to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new tlUser, or with status
   *         {@code 400 (Bad Request)} if the tlUser has already an ID.
   */
  @POST
  @Transactional
  public Response createTlUser(@Valid TlUser tlUser, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save TlUser : {}", tlUser);
    if (tlUser.id != null) {
      throw new BadRequestAlertException("A new tlUser cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = TlUser.persistOrUpdate(tlUser);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /tl-users} : Updates an existing tlUser.
   *
   * @param tlUser the tlUser to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated tlUser, or with status
   *         {@code 400 (Bad Request)} if the tlUser is not valid, or with status {@code 500 (Internal Server Error)} if
   *         the tlUser couldn't be updated.
   */
  @PUT
  @Transactional
  @Authenticated
  public Response updateTlUser(@Valid TlUser tlUser) {

    this.log.debug("REST request to update TlUser : {}", tlUser);
    if (tlUser.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = TlUser.persistOrUpdate(tlUser);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, tlUser.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /tl-users/:id} : delete the "id" tlUser.
   *
   * @param id the id of the tlUser to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteTlUser(@PathParam("id") Long id) {

    this.log.debug("REST request to delete TlUser : {}", id);
    TlUser.findByIdOptional(id).ifPresent(tlUser -> {
      tlUser.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /tl-users} : get all the tlUsers. * @return the {@link Response} with status {@code 200 (OK)} and the
   * list of tlUsers in body.
   */
  @GET
  public List<TlUser> getAllTlUsers() {

    this.log.debug("REST request to get all TlUsers");
    return TlUser.findAll().list();
  }

  /**
   * {@code GET  /tl-users/:id} : get the "id" tlUser.
   *
   * @param id the id of the tlUser to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the tlUser, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getTlUser(@PathParam("id") Long id) {

    this.log.debug("REST request to get TlUser : {}", id);
    Optional<TlUser> tlUser = TlUser.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(tlUser);
  }
}
