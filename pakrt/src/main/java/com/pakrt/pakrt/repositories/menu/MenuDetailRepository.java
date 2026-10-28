package com.pakrt.pakrt.repositories.menu;

import com.pakrt.pakrt.models.menu.MenuDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuDetailRepository extends JpaRepository<MenuDetail, String> {

    List<MenuDetail> findAllByOrderBySequenceAsc();

    // Gunakan underscore (_) untuk secara eksplisit merujuk ke properti id di dalam objek menu (menu.id).
    // Hal ini wajib dilakukan untuk menghindari bentrok dengan method @Transient getMenuId() di entity MenuDetail.
    List<MenuDetail> findByMenu_IdOrderBySequenceAsc(String menuId);

    List<MenuDetail> findByMenu_IdAndIsEnabledTrueOrderBySequenceAsc(String menuId);

    // Pengganti @Query searchByName. Spring Data JPA otomatis men-generate query LIKE dengan case-insensitive
    List<MenuDetail> findByNameContainingIgnoreCaseOrderBySequenceAsc(String name);

    // Untuk fungsi agregat seperti MAX, tetap gunakan @Query
    @Query("SELECT COALESCE(MAX(md.sequence), 0) FROM MenuDetail md WHERE md.menu.id = :menuId")
    Integer findMaxSequenceByMenuId(@Param("menuId") String menuId);

    // Mengambil list tipe data spesifik (hanya sequence) tetap lebih efisien menggunakan @Query
    @Query("SELECT md.sequence FROM MenuDetail md WHERE md.menu.id = :menuId ORDER BY md.sequence ASC")
    List<Integer> findSequencesByMenuId(@Param("menuId") String menuId);
}