package com.pakrt.pakrt.config;

import com.pakrt.pakrt.models.menu.Menu;
import com.pakrt.pakrt.models.menu.MenuDetail;
import com.pakrt.pakrt.models.statuskeluarga.StatusKeluarga;
import com.pakrt.pakrt.repositories.menu.MenuDetailRepository;
import com.pakrt.pakrt.repositories.menu.MenuRepository;
import com.pakrt.pakrt.repositories.statuskeluarga.StatusKeluargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MenuDataInitializer implements CommandLineRunner {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuDetailRepository menuDetailRepository;

    @Autowired
    private StatusKeluargaRepository statusKeluargaRepository;

    @Override
    public void run(String... args) {
        try {
            seedStatusKeluarga();
        } catch (Exception e) {
            System.out.println("[INFO] Skipping seedStatusKeluarga: " + e.getMessage());
        }
        try {
            seedMenuAndMenuDetails();
        } catch (Exception e) {
            System.out.println("[INFO] Skipping seedMenuAndMenuDetails: " + e.getMessage());
        }
    }

    private void seedStatusKeluarga() {
        if (statusKeluargaRepository.count() == 0) {
            List<StatusKeluarga> initialStatuses = Arrays.asList(
                    new StatusKeluarga(null, "KEPALA_KELUARGA", "Kepala Keluarga", "Kepala Keluarga utama dalam Kartu Keluarga", true),
                    new StatusKeluarga(null, "SUAMI", "Suami", "Suami", true),
                    new StatusKeluarga(null, "ISTRI", "Istri", "Istri", true),
                    new StatusKeluarga(null, "ANAK", "Anak", "Anak kandung / anak angkat", true),
                    new StatusKeluarga(null, "MENANTU", "Menantu", "Menantu", true),
                    new StatusKeluarga(null, "CUCU", "Cucu", "Cucu", true),
                    new StatusKeluarga(null, "ORANG_TUA", "Orang Tua", "Orang Tua kandung", true),
                    new StatusKeluarga(null, "MERTUA", "Mertua", "Orang tua dari pasangan", true),
                    new StatusKeluarga(null, "FAMILI_LAIN", "Famili Lain", "Keluarga atau kerabat lain", true),
                    new StatusKeluarga(null, "PEMBANTU", "Pembantu", "Asisten Rumah Tangga", true),
                    new StatusKeluarga(null, "LAINNYA", "Lainnya", "Status keluarga lainnya", true)
            );
            statusKeluargaRepository.saveAll(initialStatuses);
        }
    }

    private void seedMenuAndMenuDetails() {
        if (menuRepository.count() == 0) {
            // 1. Dasbor
            createMenuWithDetails("Dasbor", "/dashboard", "LayoutDashboard", 1, Arrays.asList(
                    new SubMenuData("Ringkasan Eksekutif", "/dashboard/overview", 1),
                    new SubMenuData("Peta GIS Wilayah", "/dashboard/gis", 2)
            ));

            // 2. Kependudukan
            createMenuWithDetails("Kependudukan", "/kependudukan", "Users", 2, Arrays.asList(
                    new SubMenuData("Data Warga", "/kependudukan/warga", 1),
                    new SubMenuData("Kartu Keluarga", "/kependudukan/kartu-keluarga", 2),
                    new SubMenuData("Status Keluarga", "/kependudukan/status-keluarga", 3),
                    new SubMenuData("Mutasi Warga", "/kependudukan/mutasi", 4)
            ));

            // 3. Layanan Surat
            createMenuWithDetails("Layanan Surat", "/surat", "Mail", 3, Arrays.asList(
                    new SubMenuData("Permohonan Surat", "/surat/permohonan", 1),
                    new SubMenuData("Validasi & Riwayat", "/surat/validasi", 2),
                    new SubMenuData("Template Surat", "/surat/template", 3)
            ));

            // 4. Keuangan & Kas
            createMenuWithDetails("Keuangan & Kas", "/keuangan", "Wallet", 4, Arrays.asList(
                    new SubMenuData("Buku Kas RT", "/keuangan/buku-kas", 1),
                    new SubMenuData("Laporan Transparansi", "/keuangan/transparansi", 2)
            ));

            // 5. Inventaris
            createMenuWithDetails("Inventaris", "/inventaris", "Package", 5, Arrays.asList(
                    new SubMenuData("Daftar Aset RT", "/inventaris/aset", 1),
                    new SubMenuData("Peminjaman Barang", "/inventaris/peminjaman", 2)
            ));

            // 6. Pengaturan Sistem
            createMenuWithDetails("Pengaturan Sistem", "/pengaturan", "Settings", 6, Arrays.asList(
                    new SubMenuData("Manajemen Menu", "/pengaturan/menu", 1),
                    new SubMenuData("Manajemen Sub-Menu", "/pengaturan/menu-detail", 2),
                    new SubMenuData("Hak Akses & Role", "/pengaturan/role", 3),
                    new SubMenuData("WhatsApp Gateway", "/pengaturan/whatsapp", 4)
            ));
        }
    }

    private void createMenuWithDetails(String name, String path, String icon, int sequence, List<SubMenuData> subMenus) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setSequence(sequence);
        menu.setIsEnabled(true);
        Menu savedMenu = menuRepository.save(menu);

        for (SubMenuData sub : subMenus) {
            MenuDetail detail = new MenuDetail();
            detail.setMenu(savedMenu);
            detail.setName(sub.name);
            detail.setPath(sub.path);
            detail.setSequence(sub.sequence);
            detail.setIsEnabled(true);
            menuDetailRepository.save(detail);
        }
    }

    private static class SubMenuData {
        String name;
        String path;
        int sequence;

        SubMenuData(String name, String path, int sequence) {
            this.name = name;
            this.path = path;
            this.sequence = sequence;
        }

        SubMenuData(String name, String path, String icon, int sequence) {
            this.name = name;
            this.path = path;
            this.sequence = sequence;
        }
    }
}