package com.pakrt.pakrt.services;

import com.pakrt.pakrt.crypto.CryptoJs;
import com.pakrt.pakrt.models.kartukeluarga.KartuKeluarga;
import com.pakrt.pakrt.models.warga.LWarga;
import com.pakrt.pakrt.models.warga.Warga;
import com.pakrt.pakrt.repositories.kartukeluarga.KartuKeluargaRepository;
import com.pakrt.pakrt.repositories.warga.LWargaRepository;
import com.pakrt.pakrt.repositories.warga.WargaRepository;
import com.pakrt.pakrt.request.warga.WargaRequest;
import com.pakrt.pakrt.utils.DataMap;
import com.pakrt.pakrt.utils.General;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WargaService {

    @Autowired
    private WargaRepository wargaRepository;

    @Autowired
    private LWargaRepository lWargaRepository;

    @Autowired
    private KartuKeluargaRepository kartuKeluargaRepository;

    @Autowired
    private LogService logService;

    /**
     * Tambah Warga baru.
     */
    public Warga create(WargaRequest request) {
        if (request.getNik() == null || request.getNik().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "NIK"));
        }
        if (request.getNamaLengkap() == null || request.getNamaLengkap().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Nama Lengkap"));
        }
        if (wargaRepository.findByNik(request.getNik().trim()).isPresent()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400204", "NIK"));
        }

        Warga entity = new Warga();
        entity.setNik(request.getNik().trim());
        entity.setNamaLengkap(request.getNamaLengkap().trim());
        entity.setJenisKelamin(request.getJenisKelamin());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setAgama(request.getAgama());
        entity.setPekerjaan(request.getPekerjaan());
        entity.setNoWhatsapp(request.getNoWhatsapp());
        entity.setStatusDomisili(request.getStatusDomisili() != null ? request.getStatusDomisili() : "WARGA_TETAP");
        entity.setIdAccount(request.getIdAccount());

        if (request.getIdKk() != null) {
            KartuKeluarga kk = kartuKeluargaRepository.findById(request.getIdKk()).orElse(null);
            entity.setKartuKeluarga(kk);
        }

        Warga saved = wargaRepository.save(entity);

        // Audit Trail Log ke Database & Konsol
        logService.log("Create", saved, "/api/v1/warga");
        Logging.LogInfo("Create Warga: " + General.stringToJson(request));

        return saved;
    }

    /**
     * Update data Warga.
     */
    public Warga update(WargaRequest request) {
        UUID id = resolveId(request.getId(), request.getExternalCode());
        if (id == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Warga"));
        }

        Warga entity = wargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Warga")));

        if (request.getNamaLengkap() != null && !request.getNamaLengkap().trim().isEmpty()) {
            entity.setNamaLengkap(request.getNamaLengkap().trim());
        }
        if (request.getJenisKelamin() != null) {
            entity.setJenisKelamin(request.getJenisKelamin());
        }
        if (request.getTempatLahir() != null) {
            entity.setTempatLahir(request.getTempatLahir());
        }
        if (request.getTanggalLahir() != null) {
            entity.setTanggalLahir(request.getTanggalLahir());
        }
        if (request.getAgama() != null) {
            entity.setAgama(request.getAgama());
        }
        if (request.getPekerjaan() != null) {
            entity.setPekerjaan(request.getPekerjaan());
        }
        if (request.getNoWhatsapp() != null) {
            entity.setNoWhatsapp(request.getNoWhatsapp());
        }
        if (request.getStatusDomisili() != null) {
            entity.setStatusDomisili(request.getStatusDomisili());
        }
        if (request.getIdAccount() != null) {
            entity.setIdAccount(request.getIdAccount());
        }
        if (request.getIdKk() != null) {
            KartuKeluarga kk = kartuKeluargaRepository.findById(request.getIdKk()).orElse(null);
            entity.setKartuKeluarga(kk);
        }

        Warga updated = wargaRepository.save(entity);

        // Audit Trail Log
        logService.log("Edit", updated, "/api/v1/warga");
        Logging.LogInfo("Update Warga: " + General.stringToJson(request));

        return updated;
    }

    /**
     * Ambil List Warga terenkripsi dengan pagination dan filter (Nexus Standard).
     */
    public List<Map<String, Object>> getList(String keyword, String statusDomisili, Integer pageNo, Integer pageSize) {
        int page = (pageNo != null && pageNo >= 0) ? pageNo : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("namaLengkap").ascending());

        Specification<LWarga> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("namaLengkap")), "%" + keyword.trim().toLowerCase() + "%"));
            }
            if (statusDomisili != null && !statusDomisili.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("statusDomisili"), statusDomisili.trim()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<LWarga> pageResult = lWargaRepository.findAll(spec, pageable);
        return DataMap.Mapped(pageResult.getContent(), pageResult.getTotalElements());
    }

    /**
     * Hapus Warga.
     */
    public void delete(UUID id, String externalCode) {
        UUID targetId = resolveId(id, externalCode);
        if (targetId == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Warga"));
        }

        Warga entity = wargaRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Warga")));

        wargaRepository.delete(entity);
        logService.log("Delete", entity, "/api/v1/warga");
        Logging.LogInfo("Delete Warga ID: " + targetId);
    }

    /**
     * Helper resolusi ID (bisa UUID mentah atau String externalCode terenkripsi).
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
