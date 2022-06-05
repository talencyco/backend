package com.codingaxis.hr.service;

public class UsernameAlreadyUsedException extends RuntimeException {

  public UsernameAlreadyUsedException() {

    super("Login name already used!");
  }
}
