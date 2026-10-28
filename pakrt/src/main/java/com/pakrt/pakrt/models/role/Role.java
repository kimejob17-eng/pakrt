package com.pakrt.pakrt.models.role;

import com.pakrt.pakrt.models.audit.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseAuditEntity    {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nama_role", nullable = false, length = 50)
    private String namaRole;

    @Column(name = "deskripsi", length = 255)
    private String deskripsi;

    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions;
}