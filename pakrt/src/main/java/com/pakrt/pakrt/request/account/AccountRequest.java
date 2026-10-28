package com.pakrt.pakrt.request.account;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountRequest {
    private String externalCode; // ID terenkripsi untuk Update/Delete
    private UUID id;
    private String username;
    private String nama;
    private String password;
    private Integer roleId;
    private Boolean isActive;
    private String permissions;
}