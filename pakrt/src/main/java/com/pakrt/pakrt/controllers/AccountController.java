package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.account.Account;
import com.pakrt.pakrt.models.role.Role;
import com.pakrt.pakrt.repositories.role.RoleRepository;
import com.pakrt.pakrt.request.account.AccountRequest;
import com.pakrt.pakrt.request.role.RoleRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.AccountService;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Endpoint Registrasi / Tambah Akun Baru.
     * POST /api/v1/account/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AccountRequest request) {
        try {
            Account created = accountService.create(request);
            created.setPassword("[PROTECTED]");
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi create account gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error create account: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Update Akun.
     * PUT /api/v1/account/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody AccountRequest request) {
        try {
            Account updated = accountService.update(request);
            updated.setPassword("[PROTECTED]");
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi update account gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error update account: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Cepat Toggle Status Aktif / Non-aktif Akun.
     * PUT /api/v1/account/toggle-status/{id}
     */
    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID id) {
        try {
            Account updated = accountService.toggleStatus(id, null);
            updated.setPassword("[PROTECTED]");
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint List Akun (Nexus Standard Pagination & Search).
     * GET /api/v1/account/list?keyword=...&roleId=...&pageNo=0&pageSize=10
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<Map<String, Object>> result = accountService.getList(keyword, roleId, pageNo, pageSize);
            return ResponseEntity.ok(ResultResponse.Get("200100", result));
        } catch (Exception e) {
            Logging.LogError("Error fetching account list: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Mengambil Daftar Role.
     * GET /api/v1/account/roles
     */
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            return ResponseEntity.ok(ResultResponse.Get("200100", roles));
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Update Hak Akses / Permissions untuk Role.
     * PUT /api/v1/account/roles/permissions
     */
    @PutMapping("/roles/permissions")
    public ResponseEntity<?> updateRolePermissions(@RequestBody RoleRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseBuilder.buildErrorResponse("400202", "ID Role wajib diisi");
            }
            Role role = roleRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Role tidak ditemukan"));

            if (request.getPermissions() != null) {
                role.setPermissions(request.getPermissions().trim());
            }
            if (request.getDeskripsi() != null) {
                role.setDeskripsi(request.getDeskripsi().trim());
            }

            Role saved = roleRepository.save(role);
            return ResponseBuilder.buildSuccessResponse(saved);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Detail Akun.
     * GET /api/v1/account/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            Account account = accountService.getById(id, null);
            account.setPassword("[PROTECTED]");
            return ResponseBuilder.buildSuccessResponse(account);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Akun via DELETE method.
     * DELETE /api/v1/account/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        try {
            accountService.delete(id, null);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Akun berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Akun via POST (Nexus Standard Format).
     * POST /api/v1/account/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody AccountRequest request) {
        try {
            accountService.delete(request.getId(), request.getExternalCode());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Akun berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}