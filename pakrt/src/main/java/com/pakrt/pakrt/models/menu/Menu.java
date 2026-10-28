package com.pakrt.pakrt.models.menu;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"menu\"")
public class Menu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "menu_key")
    private String menuKey;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "sequence")
    private Integer sequence = 0;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;



    // Getter & Setter
    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }


    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequence ASC")
    @JsonManagedReference
    private List<MenuDetail> menuDetails = new ArrayList<>();
}
