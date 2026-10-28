package com.pakrt.pakrt.services;

import com.pakrt.pakrt.models.menu.Menu;
import com.pakrt.pakrt.models.menu.MenuDetail;
import com.pakrt.pakrt.repositories.menu.MenuDetailRepository;
import com.pakrt.pakrt.repositories.menu.MenuRepository;
import com.pakrt.pakrt.request.menu.MenuDetailRequest;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuDetailService {

    @Autowired
    private MenuDetailRepository menuDetailRepository;

    @Autowired
    private MenuRepository menuRepository;

    /**
     * Tambah Sub-Menu (MenuDetail) baru.
     */
    public MenuDetail create(MenuDetailRequest request) {
        if (request.getMenuId() == null || request.getMenuId().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Menu ID"));
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Name"));
        }
        if (request.getPath() == null || request.getPath().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Path"));
        }

        Menu parentMenu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Parent Menu")));

        MenuDetail detail = new MenuDetail();
        detail.setMenu(parentMenu);
        detail.setName(request.getName().trim());
        detail.setPath(request.getPath().trim());
        detail.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);

        if (request.getSequence() != null) {
            detail.setSequence(request.getSequence());
        } else {
            Integer maxSeq = menuDetailRepository.findMaxSequenceByMenuId(parentMenu.getId());
            detail.setSequence(maxSeq != null ? maxSeq + 1 : 1);
        }

        return menuDetailRepository.save(detail);
    }

    /**
     * Update Sub-Menu (MenuDetail).
     */
    public MenuDetail update(MenuDetailRequest request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Menu Detail ID"));
        }

        MenuDetail detail = menuDetailRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu Detail")));

        if (request.getMenuId() != null && !request.getMenuId().trim().isEmpty()) {
            Menu parentMenu = menuRepository.findById(request.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Parent Menu")));
            detail.setMenu(parentMenu);
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            detail.setName(request.getName().trim());
        }
        if (request.getPath() != null && !request.getPath().trim().isEmpty()) {
            detail.setPath(request.getPath().trim());
        }
        if (request.getSequence() != null) {
            detail.setSequence(request.getSequence());
        }
        if (request.getIsEnabled() != null) {
            detail.setIsEnabled(request.getIsEnabled());
        }

        return menuDetailRepository.save(detail);
    }

    /**
     * Mengambil daftar MenuDetail dengan filter menuId, keyword pencarian, atau status aktif.
     */
    @Transactional(readOnly = true)
    public List<MenuDetail> getAll(String menuId, String keyword, Boolean onlyEnabled) {
        if (menuId != null && !menuId.trim().isEmpty()) {
            if (Boolean.TRUE.equals(onlyEnabled)) {
                // Perbaikan: gunakan method repository dengan underscore (_)
                return menuDetailRepository.findByMenu_IdAndIsEnabledTrueOrderBySequenceAsc(menuId.trim());
            }
            // Perbaikan: gunakan method repository dengan underscore (_)
            return menuDetailRepository.findByMenu_IdOrderBySequenceAsc(menuId.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return menuDetailRepository.findByNameContainingIgnoreCaseOrderBySequenceAsc(keyword.trim());
        }

        return menuDetailRepository.findAllByOrderBySequenceAsc();
    }

    /**
     * Mengambil detail MenuDetail berdasarkan ID.
     */
    @Transactional(readOnly = true)
    public MenuDetail getById(String id) {
        return menuDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu Detail")));
    }

    /**
     * Mendapatkan nomor sequence berikutnya (auto-increment per menu).
     */
    @Transactional(readOnly = true)
    public Integer getNextSequence(String menuId) {
        if (menuId == null || menuId.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Menu ID"));
        }
        Integer maxSeq = menuDetailRepository.findMaxSequenceByMenuId(menuId.trim());
        return (maxSeq != null ? maxSeq : 0) + 1;
    }

    /**
     * Mendapatkan daftar sequence yang sudah terpakai di menu terkait.
     */
    @Transactional(readOnly = true)
    public List<Integer> getSequenceList(String menuId) {
        if (menuId == null || menuId.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Menu ID"));
        }
        return menuDetailRepository.findSequencesByMenuId(menuId.trim());
    }

    /**
     * Menghapus MenuDetail.
     */
    public void delete(String id) {
        MenuDetail detail = menuDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu Detail")));
        menuDetailRepository.delete(detail);
    }
}