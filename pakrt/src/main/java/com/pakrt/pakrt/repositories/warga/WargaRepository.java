package com.pakrt.pakrt.repositories.warga;

import com.pakrt.pakrt.models.warga.Warga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WargaRepository extends JpaRepository<Warga, java.util.UUID> {
    Optional<Warga> findByNik(String nik);
    List<Warga> findByKartuKeluarga_Id(java.util.UUID idKk);
    List<Warga> findByStatusDomisili(String statusDomisili);
}
