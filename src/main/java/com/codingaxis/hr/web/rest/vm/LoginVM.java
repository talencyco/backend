package com.codingaxis.hr.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * View Model object for storing a user's credentials.
 */
@RegisterForReflection
public class LoginVM {
  @NotNull
  @Size(min = 1, max = 50)
  public String username;

  @NotNull
  @Size(min = 4, max = 100)
  public String password;

  public Boolean rememberMe;

  @Override
  public String toString() {

    return "LoginVM{password" + this.password + " username='" + this.username + '\'' + ", rememberMe=" + this.rememberMe
        + '}';
  }
}
