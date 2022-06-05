package com.codingaxis.hr.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

/**
 * TODO indu This type ...
 *
 */
public class UserExtra implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  @Column(name = "phone")
  private String phone;

  @OneToOne
  @MapsId
  private User user;

}
