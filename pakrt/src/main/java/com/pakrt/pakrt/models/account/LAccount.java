package com.pakrt.pakrt.models.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakrt.pakrt.utils.Encrypt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class LAccount implements Serializable {

    @Id
    @JsonProperty("externalCode")
    @Column(name = "id")
    @Convert(converter = Encrypt.class)
    private String externalCode;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "nama", length = 100)
    private String nama;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "permissions")
    private String permissions;
}