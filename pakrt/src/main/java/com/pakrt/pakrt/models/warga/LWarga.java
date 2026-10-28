package com.pakrt.pakrt.models.warga;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakrt.pakrt.utils.Encrypt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "warga")
public class LWarga implements Serializable {

    @JsonProperty("externalCode")
    @Column(name = "id")
    @Convert(converter = Encrypt.class)
    private String externalCode;

    @Id
    @Column(name = "nik", length = 16)
    @Convert(converter = Encrypt.class)
    private String nik;

    @Column(name = "nama_lengkap", length = 150)
    private String namaLengkap;

    @Column(name = "jenis_kelamin", length = 15)
    private String jenisKelamin;

    @Column(name = "no_whatsapp", length = 20)
    @Convert(converter = Encrypt.class)
    private String noWhatsapp;

    @Column(name = "status_domisili", length = 50)
    private String statusDomisili;

    @Column(name = "agama", length = 50)
    private String agama;

    @Column(name = "pekerjaan", length = 100)
    private String pekerjaan;

    @Column(name = "tempat_lahir", length = 100)
    private String tempatLahir;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;
}
