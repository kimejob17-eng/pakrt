package com.pakrt.pakrt.services;

import com.pakrt.pakrt.models.statuskeluarga.StatusKeluarga;
import com.pakrt.pakrt.repositories.statuskeluarga.StatusKeluargaRepository;
import com.pakrt.pakrt.request.statuskeluarga.StatusKeluargaRequest;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class  StatusKeluargaService {

    @Autowired
    private StatusKeluargaRepository statusKeluargaRepository;

    /**
     * Menambahkan Master Status Keluarga baru.
     */
    public StatusKeluarga create(StatusKeluargaRequest request) {
        if (request.getKode() == null || request.getKode().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Kode Status"));
        }
        if (request.getNama() == null || request.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Nama Status"));
        }

        String normalizedKode = request.getKode().trim().toUpperCase().replaceAll("\\s+", "_");

        if (statusKeluargaRepository.existsByKode(normalizedKode)) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400204", "Kode: " + normalizedKode));
        }

        StatusKeluarga entity = new StatusKeluarga();
        entity.setKode(normalizedKode);
        entity.setNama(request.getNama().trim());
        entity.setDeskripsi(request.getDeskripsi());
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return statusKeluargaRepository.save(entity);
    }

    /**
     * Memperbarui Master Status Keluarga.
     */
    public StatusKeluarga update(StatusKeluargaRequest request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Status"));
        }

        StatusKeluarga entity = statusKeluargaRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Status Keluarga")));

        if (request.getKode() != null && !request.getKode().trim().isEmpty()) {
            String normalizedKode = request.getKode().trim().toUpperCase().replaceAll("\\s+", "_");
            if (!normalizedKode.equalsIgnoreCase(entity.getKode()) && statusKeluargaRepository.existsByKode(normalizedKode)) {
                throw new IllegalArgumentException(MessageDictionary.MessageEn("400204", "Kode: " + normalizedKode));
            }
            entity.setKode(normalizedKode);
        }

        if (request.getNama() != null && !request.getNama().trim().isEmpty()) {
            entity.setNama(request.getNama().trim());
        }

        if (request.getDeskripsi() != null) {
            entity.setDeskripsi(request.getDeskripsi());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        return statusKeluargaRepository.save(entity);
    }

    /**
     * Mengambil daftar seluruh Status Keluarga.
     */
    public List<StatusKeluarga> getAll(Boolean onlyActive) {
        if (Boolean.TRUE.equals(onlyActive)) {
            return statusKeluargaRepository.findByIsActiveTrue();
        }
        return statusKeluargaRepository.findAll();
    }

    /**
     * Mengambil detail Status Keluarga berdasarkan ID.
     */
    public StatusKeluarga getById(String id) {
        return statusKeluargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Status Keluarga")));
    }

    /**
     * Menghapus Status Keluarga.
     * Jika status sudah digunakan oleh data warga, tidak boleh dihapus fisik (menjaga integritas data relasional).
     */
    public void delete(String id) {
        StatusKeluarga entity = statusKeluargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Status Keluarga")));

        statusKeluargaRepository.delete(entity);
    }
}
