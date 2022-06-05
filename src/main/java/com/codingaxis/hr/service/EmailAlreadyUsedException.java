package com.codingaxis.hr.service;

public class EmailAlreadyUsedException extends RuntimeException {

  public EmailAlreadyUsedException() {

    super("Email is already in use!");
  }
}
