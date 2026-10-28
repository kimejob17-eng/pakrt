package com.pakrt.pakrt.request.role;

import lombok.Data;

@Data
public class RoleRequest {
    private Integer id;
    private String namaRole;
    private String deskripsi;
    private String permissions;
}