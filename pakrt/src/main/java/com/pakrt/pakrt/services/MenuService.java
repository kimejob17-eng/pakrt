package com.pakrt.pakrt.services;

import com.pakrt.pakrt.models.menu.Menu;
import com.pakrt.pakrt.models.menu.MenuDetail;
import com.pakrt.pakrt.repositories.menu.MenuRepository;
import com.pakrt.pakrt.request.menu.MenuRequest;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    /**
     * Tambah Menu induk baru.
     */
    public Menu create(MenuRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Name"));
        }

        Menu menu = new Menu();
        menu.setName(request.getName().trim());
        menu.setMenuKey(request.getMenuKey());
        menu.setPath(request.getPath());
        menu.setIcon(request.getIcon());
        menu.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);

        if (request.getSequence() != null) {
            menu.setSequence(request.getSequence());
        } else {
            Integer maxSeq = menuRepository.findMaxSequence();
            menu.setSequence(maxSeq != null ? maxSeq + 1 : 1);
        }

        return menuRepository.save(menu);
    }

    /**
     * Update Menu induk.
     */
    public Menu update(MenuRequest request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageDictionary.MessageEn("400202", "Menu ID"));
        }

        Menu menu = menuRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu")));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            menu.setName(request.getName().trim());
        }
        if (request.getMenuKey() != null) {
            menu.setMenuKey(request.getMenuKey().trim());
        }
        if (request.getPath() != null) {
            menu.setPath(request.getPath());
        }
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        if (request.getSequence() != null) {
            menu.setSequence(request.getSequence());
        }
        if (request.getIsEnabled() != null) {
            menu.setIsEnabled(request.getIsEnabled());
        }

        return menuRepository.save(menu);
    }

    /**
     * Mengambil daftar Menu induk (dengan filter pencarian atau hanya yang aktif).
     */
    @Transactional(readOnly = true)
    public List<Menu> getAll(String keyword, Boolean onlyEnabled) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return menuRepository.searchByName(keyword.trim());
        }
        if (Boolean.TRUE.equals(onlyEnabled)) {
            return menuRepository.findByIsEnabledTrueOrderBySequenceAsc();
        }
        return menuRepository.findAllByOrderBySequenceAsc();
    }

    /**
     * Mengambil struktur hierarki Menu beserta MenuDetail (Sub-Menu) aktif untuk navigasi frontend.
     */
    @Transactional(readOnly = true)
    public List<Menu> getMenuTree() {
        List<Menu> activeMenus = menuRepository.findByIsEnabledTrueOrderBySequenceAsc();
        for (Menu menu : activeMenus) {
            if (menu.getMenuDetails() != null) {
                List<MenuDetail> activeDetails = menu.getMenuDetails().stream()
                        .filter(detail -> Boolean.TRUE.equals(detail.getIsEnabled()))
                        .collect(Collectors.toList());
                menu.setMenuDetails(activeDetails);
            }
        }
        return activeMenus;
    }

    /**
     * Mengambil detail Menu berdasarkan ID.
     */
    @Transactional(readOnly = true)
    public Menu getById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu")));
    }

    /**
     * Menghapus Menu beserta seluruh MenuDetail anak di dalamnya.
     */
    public void delete(String id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageDictionary.MessageEn("400404", "Menu")));
        menuRepository.delete(menu);
    }
}