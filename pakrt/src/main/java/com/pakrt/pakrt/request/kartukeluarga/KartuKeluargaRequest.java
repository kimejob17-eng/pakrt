package com.pakrt.pakrt.request.kartukeluarga;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class KartuKeluargaRequest {
    private String externalCode; // ID terenkripsi saat update/delete
    private UUID id;
    private String noKk;
    private String kepalaKeluarga;
    private String alamatLengkap;
    private String rt;
    private String rw;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
