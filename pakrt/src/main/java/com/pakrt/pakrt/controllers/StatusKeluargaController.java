package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.statuskeluarga.StatusKeluarga;
import com.pakrt.pakrt.request.statuskeluarga.StatusKeluargaRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.StatusKeluargaService;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status-keluarga")
public class StatusKeluargaController {

    @Autowired
    private StatusKeluargaService statusKeluargaService;

    /**
     * Endpoint untuk menambah Status Keluarga baru via Web Admin.
     * POST /api/v1/status-keluarga/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody StatusKeluargaRequest request) {
        try {
            StatusKeluarga created = statusKeluargaService.create(request);
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint untuk memperbarui Status Keluarga via Web Admin.
     * PUT /api/v1/status-keluarga/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody StatusKeluargaRequest request) {
        try {
            StatusKeluarga updated = statusKeluargaService.update(request);
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint untuk mengambil list Status Keluarga (misal untuk dropdown form Warga di Web).
     * GET /api/v1/status-keluarga/list?onlyActive=true
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "false") Boolean onlyActive) {
        try {
            List<StatusKeluarga> list = statusKeluargaService.getAll(onlyActive);
            return ResponseEntity.ok(ResultResponse.Get("200100", list));
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint untuk mengambil detail Status Keluarga berdasarkan ID.
     * GET /api/v1/status-keluarga/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            StatusKeluarga detail = statusKeluargaService.getById(id);
            return ResponseBuilder.buildSuccessResponse(detail);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint untuk menghapus Status Keluarga.
     * DELETE /api/v1/status-keluarga/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            statusKeluargaService.delete(id);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Status Keluarga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint alternatif delete via POST untuk kompatibilitas form/Nexus:
     * POST /api/v1/status-keluarga/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody StatusKeluargaRequest request) {
        try {
            statusKeluargaService.delete(request.getId());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Status Keluarga berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}
