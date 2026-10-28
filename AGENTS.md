# ATURAN & BATASAN PENGEMBANGAN CODEBASE (PAK RT BACKEND)
# STRICT AGENT CONSTRAINTS & DEVELOPMENT GUIDELINES

Dokumen ini adalah **panduan batasan mutlak (Hard Constraints)** untuk agen AI. Agen AI **WAJIB** membaca dan mematuhi panduan struktur baku di [CODEBASE.md](file:///d:/pakrt/CODEBASE.md).

---

## 🛑 PRINSIP UTAMA: ANTI-HALUSINASI & STRICT INSTRUCTION ONLY

1. **DILARANG BERHALUSINASI**:
   - Dilarang membuat nama tabel, kolom, tipe data, enum, atau relasi yang tidak terdaftar secara fisik di database PostgreSQL `pakrt` (Navicat). Lihat daftar tabel di [CODEBASE.md](file:///d:/pakrt/CODEBASE.md).
   - Dilarang mengasumsikan method/library eksternal yang tidak terpasang di `pom.xml`.
   - Dilarang mengarang format respons JSON di luar standar Nexus Multilateral.

2. **DILARANG MEMODIFIKASI TANPA PERINTAH**:
   - Modifikasi file, penambahan class, penghapusan class, atau refactoring **HANYA** boleh dilakukan sesuai instruksi eksplisit pengguna.
   - Dilarang mengubah konfigurasi database, port, credentials, atau arsitektur tanpa persetujuan langsung.
   - Jangan menyentuh modul atau file yang tidak bersangkutan dengan task yang sedang ditugaskan.

3. **STRUKTUR MODULAR PER-TABEL WAJIB DIPATUHI**:
   - Setiap tabel database **WAJIB** memiliki folder sendiri: `models/{feature}/`, `repositories/{feature}/`, `request/{feature}/`, `services/`, dan `controllers/`.
   - Dilarang mencampur model dari tabel yang berbeda ke dalam satu folder.

4. **VERIFIKASI SETIAP LANGKAH**:
   - Setiap kali selesai membuat/mengubah kode, wajib lakukan build check (`./mvnw test` atau `./mvnw clean compile -DskipTests`) untuk memastikan zero-error.

5. **PENCATATAN RIWAYAT LANGKAH KERJA**:
   - Setiap pekerjaan yang selesai wajib dicatat di tabel changelog pada [CODEBASE.md](file:///d:/pakrt/CODEBASE.md).
