# STRUKTUR CODEBASE & ARSITEKTUR BAKU (PAK RT BACKEND)
# GROUND TRUTH & ARCHITECTURAL SPECIFICATION (`CODEBASE.md`)

Dokumen ini adalah **panduan struktur baku codebase** untuk backend sistem Pak RT berbasis Java 21, Spring Boot 3.3.2, PostgreSQL 16+, Spring Data JPA, dan Pola Nexus Multilateral.

---

## 1. INTEGRITAS DATABASE (GROUND TRUTH POSTGRESQL / NAVICAT)

Semua nama tabel dan kolom di codebase **WAJIB 100% SINKRON DENGAN DATABASE FISIK `pakrt`**:

| Nama Tabel Fisik | Tipe Primary Key (`id`) | Deskripsi Singkat |
| :--- | :--- | :--- |
| `warga` | `UUID` | Data kependudukan warga |
| `kartu_keluarga` | `UUID` | Data kepala keluarga & nomor KK |
| `accounts` | `UUID` | Autentikasi user & kredensial |
| `roles` | `SERIAL / INTEGER` | Role hak akses pengguna |
| `user_logs` | `UUID` | Audit trail & log aktivitas pengguna |
| `keuangan` | `UUID` | Catatan kas pemasukan & pengeluaran RT |
| `master_surat` | `SERIAL / INTEGER` | Template & jenis surat pengantar |
| `pengajuan_surat` | `UUID` | Transaksi pengajuan surat warga |
| `inventaris` | `SERIAL / INTEGER` | Master aset & barang RT |
| `peminjaman_inventaris` | `UUID` | Transaksi peminjaman barang inventaris |
| `pengumuman` | `SERIAL / INTEGER` | Broadcast pengumuman dan berita RT |

---

## 2. STRUKTUR MODULAR PACKAGE PER-TABEL DATABASE

Setiap tabel di database **WAJIB DIPISAHKAN KE DALAM FOLDER/PACKAGE MASING-MASING**. Dilarang menggabungkan model atau komponen dari tabel yang berbeda ke dalam satu folder.

```
pakrt/src/main/java/com/pakrt/pakrt/
├── controllers/                     # REST API Controllers
│   ├── WargaController.java
│   ├── KartuKeluargaController.java
│   ├── LogController.java
│   └── ...
├── services/                        # Business Logic Layer
│   ├── WargaService.java
│   ├── KartuKeluargaService.java
│   ├── LogService.java
│   └── ...
├── models/                          # Entity & Projection per Tabel
│   ├── audit/
│   │   └── BaseAuditEntity.java     # Base class created_at, created_by, updated_at, updated_by
│   ├── warga/
│   │   ├── Warga.java               # Entity utama (@Table(name = "warga"))
│   │   └── LWarga.java              # List Projection dengan encrypted externalCode
│   ├── kartukeluarga/
│   │   ├── KartuKeluarga.java       # Entity utama (@Table(name = "kartu_keluarga"))
│   │   └── LKartuKeluarga.java      # List Projection
│   ├── log/
│   │   └── UserLog.java             # Entity log aktivitas (@Table(name = "user_logs"))
│   ├── account/
│   ├── role/
│   ├── keuangan/
│   ├── mastersurat/
│   ├── pengajuansurat/
│   ├── inventaris/
│   ├── peminjamaninventaris/
│   └── pengumuman/
├── repositories/                    # Spring Data JPA Repository per Tabel
│   ├── warga/
│   │   ├── WargaRepository.java
│   │   └── LWargaRepository.java
│   ├── kartukeluarga/
│   │   ├── KartuKeluargaRepository.java
│   │   └── LKartuKeluargaRepository.java
│   ├── log/
│   │   └── UserLogRepository.java
│   └── ...
├── request/                         # Request DTO Payload per Tabel
│   ├── warga/
│   │   └── WargaRequest.java
│   ├── kartukeluarga/
│   │   └── KartuKeluargaRequest.java
│   ├── log/
│   └── ...
└── utils/                           # Core Utilities Nexus Multilateral
    ├── CryptoJs.java                # AES-256 GCM / CBC Encryption
    ├── Encrypt.java                 # JPA AttributeConverter
    ├── General.java                 # Context Helper (IP, Current Username)
    ├── Logging.java                 # Console Logger with Username tracking
    ├── ResponseBuilder.java         # Nexus Response Builder
    ├── ResultResponse.java          # Wrapper Result
    ├── BasicResponse.java           # Wrapper Basic Message
    └── MessageDictionary.java       # Response code mapping
```

---

## 3. STANDAR 5 LAYER PADA SETIAP TABEL

Setiap modul tabel `{feature}` wajib mengimplementasikan 7 komponen baku:

1. **Entity (`models/{feature}/{Feature}.java`)**:
   - Meng-`extends BaseAuditEntity`.
   - Menggunakan `@Convert(converter = Encrypt.class)` pada kolom data pribadi (`nik`, `no_whatsapp`, `no_kk`).
2. **List Model (`models/{feature}/L{Feature}.java`)**:
   - Khusus representasi data ringkas/tabel list di frontend.
   - Field `externalCode` terenkripsi untuk keamanan ID saat dikirim ke frontend.
3. **Repository CRUD (`repositories/{feature}/{Feature}Repository.java`)**:
   - `extends JpaRepository<Feature, ID_TYPE>, JpaSpecificationExecutor<Feature>`.
4. **Repository List (`repositories/{feature}/L{Feature}Repository.java`)**:
   - Query pagination & searching khusus.
5. **Request DTO (`request/{feature}/{Feature}Request.java`)**:
   - Validasi data masuk dari frontend.
6. **Service (`services/{Feature}Service.java`)**:
   - Logika bisnis, transaksi, enkripsi/dekripsi, audit trail otomatis.
   - Wajib memanggil `logService.log(...)` (log database) dan `Logging.LogInfo(...)` (log konsol).
7. **Controller (`controllers/{Feature}Controller.java`)**:
   - Mapping endpoint `/api/v1/{feature}/...`.
   - Response format Nexus: `ResponseBuilder.buildSuccessResponse()`, `DataMap.Mapped()`, `BasicResponse.Get()`.

---

## 4. AUDIT TRAIL OTOMATIS (`BaseAuditEntity`)

Semua entity operasional mewarisi field otomatis:
- `created_at` (`LocalDateTime`, `updatable = false`)
- `created_by` (`String`, `updatable = false`) &rarr; Otomatis dari `General.getUsername()`
- `updated_at` (`LocalDateTime`) &rarr; Otomatis ter-update saat record diedit
- `updated_by` (`String`) &rarr; Otomatis dari `General.getUsername()`

---

## 5. CATATAN RIWAYAT PENGEMBANGAN (CHANGELOG)

| Tanggal & Waktu | Modul / Fitur | Aksi & Perubahan yang Dilakukan | Status |
| :--- | :--- | :--- | :--- |
| **2026-10-08** | **Core Audit & Logging** | Dibuat `BaseAuditEntity`, `UserLog`, `UserLogRepository`, `LogService`, `LogController`, `General`, `Logging`. | ✅ Selesai |
| **2026-10-08** | **Modular Warga** | Pemisahan modul `warga` (`Warga.java`, `LWarga.java`, `WargaRepository.java`, `LWargaRepository.java`, `WargaRequest.java`, `WargaService.java`, `WargaController.java`). | ✅ Selesai |
| **2026-10-08** | **Modular Kartu Keluarga** | Pemisahan modul `kartukeluarga` ke folder terpisah (`KartuKeluarga.java`, `LKartuKeluarga.java`, `KartuKeluargaRepository.java`, `LKartuKeluargaRepository.java`, `KartuKeluargaRequest.java`, `KartuKeluargaService.java`, `KartuKeluargaController.java`). | ✅ Selesai |
| **2026-10-08** | **Arsitektur & Codebase Guide** | Pembuatan `CODEBASE.md` sebagai acuan struktur & pembersihan duplikasi file. | ✅ Selesai |
| **2026-10-09** | **JWT Bearer Auth & Security** | Implementasi `JwtService` (durasi 10 menit, Opsi B Key Derivation SHA-256), `JwtAuthenticationFilter`, `BCryptPasswordEncoder`, model `Role`, `Account`, `AuthService`, dan `AuthController` (`/api/v1/auth/login`). | ✅ Selesai |
| **2026-10-09** | **Modular Account (Registrasi & CRUD)** | Pembuatan modul lengkap `accounts` (`Account.java`, `LAccount.java`, `AccountRepository.java`, `LAccountRepository.java`, `AccountRequest.java`, `AccountService.java`, `AccountController.java` dengan BCrypt password hashing). | ✅ Selesai |


