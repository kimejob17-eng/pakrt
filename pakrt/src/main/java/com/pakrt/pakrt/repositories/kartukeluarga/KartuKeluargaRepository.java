package com.pakrt.pakrt.repositories.kartukeluarga;

import com.pakrt.pakrt.models.kartukeluarga.KartuKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KartuKeluargaRepository extends JpaRepository<KartuKeluarga, UUID>, JpaSpecificationExecutor<KartuKeluarga> {
    Optional<KartuKeluarga> findByNoKk(String noKk);
    boolean existsByNoKk(String noKk);
}
