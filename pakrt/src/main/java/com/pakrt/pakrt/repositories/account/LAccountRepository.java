package com.pakrt.pakrt.repositories.account;

import com.pakrt.pakrt.models.account.LAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LAccountRepository extends JpaRepository<LAccount, String>, JpaSpecificationExecutor<LAccount> {
}
