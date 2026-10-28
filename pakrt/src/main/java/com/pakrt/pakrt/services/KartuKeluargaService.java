package com.pakrt.pakrt.services;

import com.pakrt.pakrt.crypto.CryptoJs;
import com.pakrt.pakrt.models.kartukeluarga.KartuKeluarga;
import com.pakrt.pakrt.models.kartukeluarga.LKartuKeluarga;
import com.pakrt.pakrt.repositories.kartukeluarga.KartuKeluargaRepository;
import com.pakrt.pakrt.repositories.kartukeluarga.LKartuKeluargaRepository;
import com.pakrt.pakrt.request.kartukeluarga.KartuKeluargaRequest;
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
public class KartuKeluargaService {

    @Autowired
    private KartuKeluargaRepository kartuKeluargaRepository;

    @Autowired
    private LKartuKeluargaRepository lKartuKeluargaRepository;

    @Autowired
    private LogService logService;

    public KartuKeluarga create(KartuKeluargaRequest request) {
        if (request.getNoKk() == null || request.getNoKk().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Nomor KK"));
        }
        if (request.getKepalaKeluarga() == null || request.getKepalaKeluarga().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Kepala Keluarga"));
        }
        if (request.getAlamatLengkap() == null || request.getAlamatLengkap().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Alamat Lengkap"));
        }
        if (kartuKeluargaRepository.findByNoKk(request.getNoKk().trim()).isPresent()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400204", "Nomor KK"));
        }

        KartuKeluarga entity = new KartuKeluarga();
        entity.setNoKk(request.getNoKk().trim());
        entity.setKepalaKeluarga(request.getKepalaKeluarga().trim());
        entity.setAlamatLengkap(request.getAlamatLengkap().trim());
        entity.setRt(request.getRt() != null ? request.getRt() : "011");
        entity.setRw(request.getRw() != null ? request.getRw() : "005");
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());

        KartuKeluarga saved = kartuKeluargaRepository.save(entity);

        // Audit Trail
        logService.log("Create", saved, "/api/v1/kartu-keluarga");
        Logging.LogInfo("Create Kartu Keluarga: " + General.stringToJson(request));

        return saved;
    }

    public KartuKeluarga update(KartuKeluargaRequest request) {
        UUID id = resolveId(request.getId(), request.getExternalCode());
        if (id == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Kartu Keluarga"));
        }

        KartuKeluarga entity = kartuKeluargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Kartu Keluarga")));

        if (request.getKepalaKeluarga() != null && !request.getKepalaKeluarga().trim().isEmpty()) {
            entity.setKepalaKeluarga(request.getKepalaKeluarga().trim());
        }
        if (request.getAlamatLengkap() != null && !request.getAlamatLengkap().trim().isEmpty()) {
            entity.setAlamatLengkap(request.getAlamatLengkap().trim());
        }
        if (request.getRt() != null) entity.setRt(request.getRt());
        if (request.getRw() != null) entity.setRw(request.getRw());
        if (request.getLatitude() != null) entity.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) entity.setLongitude(request.getLongitude());

        KartuKeluarga updated = kartuKeluargaRepository.save(entity);

        logService.log("Edit", updated, "/api/v1/kartu-keluarga");
        Logging.LogInfo("Update Kartu Keluarga: " + General.stringToJson(request));

        return updated;
    }

    public List<Map<String, Object>> getList(String keyword, Integer pageNo, Integer pageSize) {
        int page = (pageNo != null && pageNo >= 0) ? pageNo : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("kepalaKeluarga").ascending());

        Specification<LKartuKeluarga> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("kepalaKeluarga")), "%" + keyword.trim().toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("alamatLengkap")), "%" + keyword.trim().toLowerCase() + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<LKartuKeluarga> pageResult = lKartuKeluargaRepository.findAll(spec, pageable);
        return DataMap.Mapped(pageResult.getContent(), pageResult.getTotalElements());
    }

    public void delete(UUID id, String externalCode) {
        UUID targetId = resolveId(id, externalCode);
        if (targetId == null) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "ID Kartu Keluarga"));
        }

        KartuKeluarga entity = kartuKeluargaRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Kartu Keluarga")));

        kartuKeluargaRepository.delete(entity);
        logService.log("Delete", entity, "/api/v1/kartu-keluarga");
        Logging.LogInfo("Delete Kartu Keluarga ID: " + targetId);
    }

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
