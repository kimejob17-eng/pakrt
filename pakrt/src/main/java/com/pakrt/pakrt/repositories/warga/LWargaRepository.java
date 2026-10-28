package com.pakrt.pakrt.repositories.warga;

import com.pakrt.pakrt.models.warga.LWarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LWargaRepository extends JpaRepository<LWarga, String>, JpaSpecificationExecutor<LWarga> {
}
