package com.pakrt.pakrt.request.statuskeluarga;

import lombok.Data;

@Data
public class StatusKeluargaRequest {
    private String id;
    private String kode;
    private String nama;
    private String deskripsi;
    private Boolean isActive;
}
