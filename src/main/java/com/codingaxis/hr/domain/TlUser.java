package com.codingaxis.hr.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A TlUser.
 */
@Entity
@Table(name = "tl_user")
@Cacheable
@RegisterForReflection
public class TlUser extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "email", nullable = false)
    public String email;

    @NotNull
    @Column(name = "mobile_number", nullable = false)
    public String mobileNumber;

    @NotNull
    @Column(name = "password", nullable = false)
    public String password;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TlUser)) {
            return false;
        }
        return id != null && id.equals(((TlUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TlUser{" +
            "id=" + id +
            ", email='" + email + "'" +
            ", mobileNumber='" + mobileNumber + "'" +
            ", password='" + password + "'" +
            ", modificationCounter=" + modificationCounter +
            "}";
    }

    public TlUser update() {
        return update(this);
    }

    public TlUser persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static TlUser update(TlUser tlUser) {
        if (tlUser == null) {
            throw new IllegalArgumentException("tlUser can't be null");
        }
        var entity = TlUser.<TlUser>findById(tlUser.id);
        if (entity != null) {
            entity.email = tlUser.email;
            entity.mobileNumber = tlUser.mobileNumber;
            entity.password = tlUser.password;
            entity.modificationCounter = tlUser.modificationCounter;
        }
        return entity;
    }

    public static TlUser persistOrUpdate(TlUser tlUser) {
        if (tlUser == null) {
            throw new IllegalArgumentException("tlUser can't be null");
        }
        if (tlUser.id == null) {
            persist(tlUser);
            return tlUser;
        } else {
            return update(tlUser);
        }
    }


}
