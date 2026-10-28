package com.pakrt.pakrt.repositories.statuskeluarga;

import com.pakrt.pakrt.models.statuskeluarga.StatusKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusKeluargaRepository extends JpaRepository<StatusKeluarga, String> {
    Optional<StatusKeluarga> findByKode(String kode);
    boolean existsByKode(String kode);
    List<StatusKeluarga> findByIsActiveTrue();
}
