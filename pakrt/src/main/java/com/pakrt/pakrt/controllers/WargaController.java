package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.warga.Warga;
import com.pakrt.pakrt.request.warga.WargaRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.WargaService;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warga")
public class WargaController {

    @Autowired
    private WargaService wargaService;

    /**
     * Endpoint Tambah Warga.
     * POST /api/v1/warga/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WargaRequest request) {
        try {
            Warga created = wargaService.create(request);
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi create warga gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error create warga: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Update Warga.
     * PUT /api/v1/warga/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody WargaRequest request) {
        try {
            Warga updated = wargaService.update(request);
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi update warga gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error update warga: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint List Warga (Nexus Standard Search & Pagination).
     * GET /api/v1/warga/list?keyword=...&statusDomisili=...&pageNo=0&pageSize=10
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String statusDomisili,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<Map<String, Object>> result = wargaService.getList(keyword, statusDomisili, pageNo, pageSize);
            return ResponseEntity.ok(ResultResponse.Get("200100", result));
        } catch (Exception e) {
            Logging.LogError("Error fetching warga list: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Warga via DELETE method.
     * DELETE /api/v1/warga/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        try {
            wargaService.delete(id, null);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Warga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Warga via POST (Nexus Standard Format).
     * POST /api/v1/warga/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody WargaRequest request) {
        try {
            wargaService.delete(request.getId(), request.getExternalCode());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Warga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}
