package com.codingaxis.hr.web.rest.vm;

import javax.validation.constraints.Size;

import com.codingaxis.hr.service.dto.UserDTO;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
@RegisterForReflection
public class ManagedUserVM extends UserDTO {
  public static final int PASSWORD_MIN_LENGTH = 4;

  public static final int PASSWORD_MAX_LENGTH = 100;

  public ManagedUserVM() {

    // Empty constructor needed for Jackson.
  }

  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  public String password;

  public String phone;

  @Override
  public String toString() {

    return "ManagedUserVM{" + super.toString() + "} ";
  }
}
