package com.pakrt.pakrt.services;

import com.pakrt.pakrt.crypto.CryptoJs;
import com.pakrt.pakrt.models.account.Account;
import com.pakrt.pakrt.models.account.LAccount;
import com.pakrt.pakrt.models.role.Role;
import com.pakrt.pakrt.repositories.account.AccountRepository;
import com.pakrt.pakrt.repositories.account.LAccountRepository;
import com.pakrt.pakrt.repositories.role.RoleRepository;
import com.pakrt.pakrt.request.account.AccountRequest;
import com.pakrt.pakrt.utils.DataMap;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LAccountRepository lAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogService logService;

    /**
     * Registrasi / Buat Akun Baru.
     */
    public Account create(AccountRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Username"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Password"));
        }

        String cleanUsername = request.getUsername().trim();

        if (accountRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400204", "Username"));
        }

        String plainPassword = request.getPassword().trim();
        try {
            plainPassword = CryptoJs.DecryptorAES(plainPassword);
        } catch (Exception e) {
            plainPassword = request.getPassword().trim();
        }

        Account account = new Account();
        account.setUsername(cleanUsername);
        account.setNama(request.getNama() != null ? request.getNama().trim() : cleanUsername);
        account.setPassword(passwordEncoder.encode(plainPassword));
        account.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        account.setPermissions(request.getPermissions() != null ? request.getPermissions().trim() : null);

        Integer targetRoleId = request.getRoleId() != null ? request.getRoleId() : 4;
        Role role = roleRepository.findById(targetRoleId).orElse(null);
        account.setRoleId(targetRoleId);
        account.setRole(role);

        Account saved = accountRepository.save(account);

        logService.log("Create", "Registrasi Akun: " + cleanUsername, "/api/v1/account");
        Logging.LogInfo("Create Account berhasil untuk user: " + cleanUsername);

        return saved;
    }

    /**
     * Update Akun (Nama, Password, Role, Status Aktif, Permissions).
     */
    public Account update(AccountRequest request) {
        UUID id = resolveId(request.getId(), request.getExternalCode());
        if (id == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Account"));
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Account")));

        if (request.getNama() != null && !request.getNama().trim().isEmpty()) {
            account.setNama(request.getNama().trim());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String plainPassword = request.getPassword().trim();
            try {
                plainPassword = CryptoJs.DecryptorAES(plainPassword);
            } catch (Exception e) {
                plainPassword = request.getPassword().trim();
            }
            account.setPassword(passwordEncoder.encode(plainPassword));
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId()).orElse(null);
            account.setRoleId(request.getRoleId());
            account.setRole(role);
        }

        if (request.getIsActive() != null) {
            account.setIsActive(request.getIsActive());
        }

        if (request.getPermissions() != null) {
            account.setPermissions(request.getPermissions().trim());
        }

        Account updated = accountRepository.save(account);

        logService.log("Edit", "Update Akun: " + account.getUsername(), "/api/v1/account");
        Logging.LogInfo("Update Account ID: " + id);

        return updated;
    }

    /**
     * Toggle status aktif/non-aktif akun.
     */
    public Account toggleStatus(UUID id, String externalCode) {
        UUID targetId = resolveId(id, externalCode);
        if (targetId == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Account"));
        }

        Account account = accountRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Account")));

        boolean newStatus = !Boolean.TRUE.equals(account.getIsActive());
        account.setIsActive(newStatus);
        Account saved = accountRepository.save(account);

        String act = newStatus ? "Aktivasi Akun: " : "Non-aktivasi Akun: ";
        logService.log("Edit", act + account.getUsername(), "/api/v1/account/toggle-status");
        return saved;
    }

    /**
     * Ambil List Akun terenkripsi dengan pagination dan filter (Nexus Standard).
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getList(String keyword, Integer roleId, Integer pageNo, Integer pageSize) {
        int page = (pageNo != null && pageNo >= 0) ? pageNo : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        Specification<LAccount> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), kw),
                        cb.like(cb.lower(root.get("nama")), kw)
                ));
            }
            if (roleId != null) {
                predicates.add(cb.equal(root.get("roleId"), roleId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<LAccount> pageResult = lAccountRepository.findAll(spec, pageable);
        return DataMap.Mapped(pageResult.getContent(), pageResult.getTotalElements());
    }

    /**
     * Ambil detail akun berdasarkan ID.
     */
    @Transactional(readOnly = true)
    public Account getById(UUID id, String externalCode) {
        UUID targetId = resolveId(id, externalCode);
        if (targetId == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Account"));
        }

        return accountRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Account")));
    }

    /**
     * Hapus Akun.
     */
    public void delete(UUID id, String externalCode) {
        UUID targetId = resolveId(id, externalCode);
        if (targetId == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Account"));
        }

        Account account = accountRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Account")));

        accountRepository.delete(account);
        logService.log("Delete", "Hapus Akun: " + account.getUsername(), "/api/v1/account");
        Logging.LogInfo("Delete Account ID: " + targetId);
    }

    /**
     * Helper resolusi ID.
     */
    private UUID resolveId(UUID id, String externalCode) {
        if (id != null) return id;
        if (externalCode != null && !externalCode.trim().isEmpty()) {
            try {
                String decrypted = CryptoJs.DecryptorAES(externalCode.trim());
                return UUID.fromString(decrypted != null ? decrypted : externalCode.trim());
            } catch (Exception e) {
                try {
                    return UUID.fromString(externalCode.trim());
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}