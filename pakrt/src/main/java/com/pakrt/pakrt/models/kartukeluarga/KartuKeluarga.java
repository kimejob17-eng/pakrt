package com.pakrt.pakrt.models.kartukeluarga;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pakrt.pakrt.models.audit.BaseAuditEntity;
import com.pakrt.pakrt.models.warga.Warga;
import com.pakrt.pakrt.utils.Encrypt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kartu_keluarga")
public class KartuKeluarga extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private java.util.UUID id;

    @Convert(converter = Encrypt.class)
    @Column(name = "no_kk", unique = true, nullable = false, length = 16)
    private String noKk;

    @Column(name = "kepala_keluarga", nullable = false, length = 150)
    private String kepalaKeluarga;

    @Column(name = "alamat_lengkap", nullable = false, columnDefinition = "text")
    private String alamatLengkap;

    @Column(name = "rt", length = 3)
    private String rt;

    @Column(name = "rw", length = 3)
    private String rw;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @OneToMany(mappedBy = "kartuKeluarga", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Warga> anggotaKeluarga = new ArrayList<>();
}
