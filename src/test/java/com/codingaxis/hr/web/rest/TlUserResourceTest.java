package com.codingaxis.hr.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.codingaxis.hr.TestUtil;
import com.codingaxis.hr.domain.TlUser;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;

@QuarkusTest
public class TlUserResourceTest {

  private static final TypeRef<TlUser> ENTITY_TYPE = new TypeRef<>() {
  };

  private static final TypeRef<List<TlUser>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
  };

  private static final String DEFAULT_EMAIL = "AAAAAAAAAA";

  private static final String UPDATED_EMAIL = "BBBBBBBBBB";

  private static final String DEFAULT_MOBILE_NUMBER = "AAAAAAAAAA";

  private static final String UPDATED_MOBILE_NUMBER = "BBBBBBBBBB";

  private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";

  private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

  private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;

  private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

  String adminToken;

  TlUser tlUser;

  @Inject
  LiquibaseFactory liquibaseFactory;

  @BeforeAll
  static void jsonMapper() {

    RestAssured.config = RestAssured.config()
        .objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
  }

  @BeforeEach
  public void authenticateAdmin() {

    this.adminToken = TestUtil.getAdminToken();
  }

  @BeforeEach
  public void databaseFixture() {

    try (Liquibase liquibase = this.liquibaseFactory.createLiquibase()) {
      liquibase.dropAll();
      liquibase.validate();
      liquibase.update(this.liquibaseFactory.createContexts(), this.liquibaseFactory.createLabels());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create an entity for this test.
   * <p>
   * This is a static method, as tests for other entities might also need it, if they test an entity which requires the
   * current entity.
   */
  public static TlUser createEntity() {

    var tlUser = new TlUser();
    tlUser.email = DEFAULT_EMAIL;
    tlUser.mobileNumber = DEFAULT_MOBILE_NUMBER;
    tlUser.password = DEFAULT_PASSWORD;
    tlUser.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
    return tlUser;
  }

  @BeforeEach
  public void initTest() {

    this.tlUser = createEntity();
  }

  @Test
  public void createTlUser() {

    var databaseSizeBeforeCreate = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // Create the TlUser
    this.tlUser = given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON).body(this.tlUser).when().post("/api/tl-users").then()
        .statusCode(CREATED.getStatusCode()).extract().as(ENTITY_TYPE);

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeCreate + 1);
    var testTlUser = tlUserList.stream().filter(it -> this.tlUser.id.equals(it.id)).findFirst().get();
    assertThat(testTlUser.email).isEqualTo(DEFAULT_EMAIL);
    assertThat(testTlUser.mobileNumber).isEqualTo(DEFAULT_MOBILE_NUMBER);
    assertThat(testTlUser.password).isEqualTo(DEFAULT_PASSWORD);
    assertThat(testTlUser.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
  }

  @Test
  public void createTlUserWithExistingId() {

    var databaseSizeBeforeCreate = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // Create the TlUser with an existing ID
    this.tlUser.id = 1L;

    // An entity with an existing ID cannot be created, so this API call must fail
    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(this.tlUser).when().post("/api/tl-users").then().statusCode(BAD_REQUEST.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  public void checkEmailIsRequired() throws Exception {

    var databaseSizeBeforeTest = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // set the field null
    this.tlUser.email = null;

    // Create the TlUser, which fails.
    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(this.tlUser).when().post("/api/tl-users").then().statusCode(BAD_REQUEST.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  public void checkMobileNumberIsRequired() throws Exception {

    var databaseSizeBeforeTest = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // set the field null
    this.tlUser.mobileNumber = null;

    // Create the TlUser, which fails.
    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(this.tlUser).when().post("/api/tl-users").then().statusCode(BAD_REQUEST.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  public void checkPasswordIsRequired() throws Exception {

    var databaseSizeBeforeTest = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // set the field null
    this.tlUser.password = null;

    // Create the TlUser, which fails.
    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(this.tlUser).when().post("/api/tl-users").then().statusCode(BAD_REQUEST.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  public void updateTlUser() {

    // Initialize the database
    this.tlUser = given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON).body(this.tlUser).when().post("/api/tl-users").then()
        .statusCode(CREATED.getStatusCode()).extract().as(ENTITY_TYPE);

    var databaseSizeBeforeUpdate = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // Get the tlUser
    var updatedTlUser = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users/{id}", this.tlUser.id).then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON)
        .extract().body().as(ENTITY_TYPE);

    // Update the tlUser
    updatedTlUser.email = UPDATED_EMAIL;
    updatedTlUser.mobileNumber = UPDATED_MOBILE_NUMBER;
    updatedTlUser.password = UPDATED_PASSWORD;
    updatedTlUser.modificationCounter = UPDATED_MODIFICATION_COUNTER;

    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(updatedTlUser).when().put("/api/tl-users").then().statusCode(OK.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeUpdate);
    var testTlUser = tlUserList.stream().filter(it -> updatedTlUser.id.equals(it.id)).findFirst().get();
    assertThat(testTlUser.email).isEqualTo(UPDATED_EMAIL);
    assertThat(testTlUser.mobileNumber).isEqualTo(UPDATED_MOBILE_NUMBER);
    assertThat(testTlUser.password).isEqualTo(UPDATED_PASSWORD);
    assertThat(testTlUser.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
  }

  @Test
  public void updateNonExistingTlUser() {

    var databaseSizeBeforeUpdate = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
        .body(this.tlUser).when().put("/api/tl-users").then().statusCode(BAD_REQUEST.getStatusCode());

    // Validate the TlUser in the database
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  public void deleteTlUser() {

    // Initialize the database
    this.tlUser = given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON).body(this.tlUser).when().post("/api/tl-users").then()
        .statusCode(CREATED.getStatusCode()).extract().as(ENTITY_TYPE);

    var databaseSizeBeforeDelete = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE).size();

    // Delete the tlUser
    given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .delete("/api/tl-users/{id}", this.tlUser.id).then().statusCode(NO_CONTENT.getStatusCode());

    // Validate the database contains one less item
    var tlUserList = given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON).extract()
        .as(LIST_OF_ENTITY_TYPE);

    assertThat(tlUserList).hasSize(databaseSizeBeforeDelete - 1);
  }

  @Test
  public void getAllTlUsers() {

    // Initialize the database
    this.tlUser = given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON).body(this.tlUser).when().post("/api/tl-users").then()
        .statusCode(CREATED.getStatusCode()).extract().as(ENTITY_TYPE);

    // Get all the tlUserList
    given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users?sort=id,desc").then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON)
        .body("id", hasItem(this.tlUser.id.intValue())).body("email", hasItem(DEFAULT_EMAIL))
        .body("mobileNumber", hasItem(DEFAULT_MOBILE_NUMBER)).body("password", hasItem(DEFAULT_PASSWORD))
        .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()));
  }

  @Test
  public void getTlUser() {

    // Initialize the database
    this.tlUser = given().auth().preemptive().oauth2(this.adminToken).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON).body(this.tlUser).when().post("/api/tl-users").then()
        .statusCode(CREATED.getStatusCode()).extract().as(ENTITY_TYPE);

    var response = // Get the tlUser
        given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
            .get("/api/tl-users/{id}", this.tlUser.id).then().statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON).extract().as(ENTITY_TYPE);

    // Get the tlUser
    given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users/{id}", this.tlUser.id).then().statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON)
        .body("id", is(this.tlUser.id.intValue()))

        .body("email", is(DEFAULT_EMAIL)).body("mobileNumber", is(DEFAULT_MOBILE_NUMBER))
        .body("password", is(DEFAULT_PASSWORD))
        .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()));
  }

  @Test
  public void getNonExistingTlUser() {

    // Get the tlUser
    given().auth().preemptive().oauth2(this.adminToken).accept(APPLICATION_JSON).when()
        .get("/api/tl-users/{id}", Long.MAX_VALUE).then().statusCode(NOT_FOUND.getStatusCode());
  }
}
