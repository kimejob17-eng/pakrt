package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.kartukeluarga.KartuKeluarga;
import com.pakrt.pakrt.request.kartukeluarga.KartuKeluargaRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.KartuKeluargaService;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kartu-keluarga")
public class KartuKeluargaController {

    @Autowired
    private KartuKeluargaService kartuKeluargaService;

    /**
     * Endpoint Tambah Kartu Keluarga.
     * POST /api/v1/kartu-keluarga/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody KartuKeluargaRequest request) {
        try {
            KartuKeluarga created = kartuKeluargaService.create(request);
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi create KK gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error create KK: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Update Kartu Keluarga.
     * PUT /api/v1/kartu-keluarga/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody KartuKeluargaRequest request) {
        try {
            KartuKeluarga updated = kartuKeluargaService.update(request);
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Validasi update KK gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Error update KK: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint List Kartu Keluarga (Nexus Standard Search & Pagination).
     * GET /api/v1/kartu-keluarga/list?keyword=...&pageNo=0&pageSize=10
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<Map<String, Object>> result = kartuKeluargaService.getList(keyword, pageNo, pageSize);
            return ResponseEntity.ok(ResultResponse.Get("200100", result));
        } catch (Exception e) {
            Logging.LogError("Error fetching KK list: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Kartu Keluarga via DELETE.
     * DELETE /api/v1/kartu-keluarga/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        try {
            kartuKeluargaService.delete(id, null);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Kartu Keluarga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Hapus Kartu Keluarga via POST (Nexus Standard Format).
     * POST /api/v1/kartu-keluarga/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody KartuKeluargaRequest request) {
        try {
            kartuKeluargaService.delete(request.getId(), request.getExternalCode());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Data Kartu Keluarga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}
