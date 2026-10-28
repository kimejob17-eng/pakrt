package com.pakrt.pakrt.models.menu;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_detail") // Memaksa Hibernate membaca camelCase sesuai PostgreSQL
public class MenuDetail extends com.pakrt.pakrt.models.audit.BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Strategi UUID untuk kolom varchar(36)
    @Column(name = "id_menu_detail", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu", nullable = false)
    @JsonBackReference
    private Menu menu;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "sequence")
    private Integer sequence = 0;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    // Helper method untuk mempermudah response JSON mendapatkan id_menu induk
    @Transient
    public String getMenuId() {
        return menu != null ? menu.getId() : null;
    }

    @Transient
    public String getMenuName() {
        return menu != null ? menu.getName() : null;
    }
}