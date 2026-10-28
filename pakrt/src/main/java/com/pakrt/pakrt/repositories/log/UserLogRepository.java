package com.pakrt.pakrt.repositories.log;

import com.pakrt.pakrt.models.log.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, java.util.UUID>, JpaSpecificationExecutor<UserLog> {

    List<UserLog> findByUsernameOrderByCreatedAtDesc(String username);

    List<UserLog> findByActionOrderByCreatedAtDesc(String action);
}

