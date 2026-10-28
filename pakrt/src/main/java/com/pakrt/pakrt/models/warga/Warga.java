package com.pakrt.pakrt.models.warga;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pakrt.pakrt.models.audit.BaseAuditEntity;
import com.pakrt.pakrt.models.kartukeluarga.KartuKeluarga;
import com.pakrt.pakrt.utils.Encrypt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warga")
public class Warga extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private java.util.UUID id;

    @Convert(converter = Encrypt.class)
    @Column(name = "nik", unique = true, nullable = false, length = 16)
    private String nik;

    @Column(name = "nama_lengkap", nullable = false, length = 150)
    private String namaLengkap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kk")
    @JsonIgnoreProperties({"anggotaKeluarga", "hibernateLazyInitializer", "handler"})
    private KartuKeluarga kartuKeluarga;

    @Column(name = "id_account")
    private java.util.UUID idAccount;

    @Column(name = "tempat_lahir", length = 100)
    private String tempatLahir;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    @Column(name = "jenis_kelamin", length = 15)
    private String jenisKelamin;

    @Column(name = "agama", length = 50)
    private String agama;

    @Column(name = "pekerjaan", length = 100)
    private String pekerjaan;

    @Convert(converter = Encrypt.class)
    @Column(name = "no_whatsapp", length = 20)
    private String noWhatsapp;

    @Column(name = "status_domisili", length = 50)
    private String statusDomisili;
}
