package com.pakrt.pakrt.request.warga;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class WargaRequest {
    private String externalCode; // ID terenkripsi untuk Update/Delete
    private UUID id;
    private String nik;
    private String namaLengkap;
    private UUID idKk;
    private UUID idAccount;
    private String tempatLahir;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;

    private String jenisKelamin;
    private String agama;
    private String pekerjaan;
    private String noWhatsapp;
    private String statusDomisili;
}
