package com.pakrt.pakrt.repositories.kartukeluarga;

import com.pakrt.pakrt.models.kartukeluarga.LKartuKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LKartuKeluargaRepository extends JpaRepository<LKartuKeluarga, String>, JpaSpecificationExecutor<LKartuKeluarga> {
}
