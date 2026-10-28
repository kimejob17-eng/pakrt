package com.pakrt.pakrt.models.kartukeluarga;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pakrt.pakrt.utils.Encrypt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "kartu_keluarga")
public class LKartuKeluarga implements Serializable {

    @JsonProperty("externalCode")
    @Column(name = "id")
    @Convert(converter = Encrypt.class)
    private String externalCode;

    @Id
    @Column(name = "no_kk", length = 16)
    @Convert(converter = Encrypt.class)
    private String noKk;

    @Column(name = "kepala_keluarga", length = 150)
    private String kepalaKeluarga;

    @Column(name = "alamat_lengkap", columnDefinition = "text")
    private String alamatLengkap;

    @Column(name = "rt", length = 3)
    private String rt;

    @Column(name = "rw", length = 3)
    private String rw;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;
}
