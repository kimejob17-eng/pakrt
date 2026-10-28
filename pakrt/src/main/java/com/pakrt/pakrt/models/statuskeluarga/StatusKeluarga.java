package com.pakrt.pakrt.models.statuskeluarga;

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
@Table(name = "mst_status_keluarga")
public class StatusKeluarga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_status_keluarga", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(name = "kode", unique = true, nullable = false, length = 50)
    private String kode;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "deskripsi", length = 255)
    private String deskripsi;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
