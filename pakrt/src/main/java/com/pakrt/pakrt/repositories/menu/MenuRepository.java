package com.pakrt.pakrt.repositories.menu;

import com.pakrt.pakrt.models.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    List<Menu> findAllByOrderBySequenceAsc();

    List<Menu> findByIsEnabledTrueOrderBySequenceAsc();

    @Query("SELECT m FROM Menu m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY m.sequence ASC")
    List<Menu> searchByName(@Param("name") String name);

    @Query("SELECT COALESCE(MAX(m.sequence), 0) FROM Menu m")
    Integer findMaxSequence();

    boolean existsByName(String name);
}
