# Product Requirements Document (PRD) 

Nama Produk: Pak RT 

Fase Rilis: MVP (Versi 1.0) 

## 1. VISI & TUJUAN PRODUK 

Pak RT (sebelumnya SI-RT) adalah infrastruktur digital terpusat yang dikembangkan untuk mendigitalisasi tata kelola operasional Rukun Tetangga secara holistik. Sistem ini dirancang untuk mencapai empat tujuan utama: 

(1) Memangkas birokrasi administratif melalui otomasi template surat dinamis, (2) Menjamin transparansi arus kas lingkungan kepada seluruh warga, (3) Memetakan demografi warga secara presisi menggunakan teknologi Geographic Information System (GIS), dan (4) Mengamankan privasi data warga sesuai dengan standar kepatuhan UU Pelindungan Data Pribadi (UU PDP). Aplikasi didistribusikan melalui Web Dashboard (Admin) dan Mobile Web-App (Warga). 

## 2. STRUKTUR PENGGUNA & HAK AKSES (RBAC) 

Sistem menggunakan pendekatan Role-Based Access Control untuk membatasi wewenang dan memberlakukan Data Scoping secara ketat. Berikut adalah empat profil akses utama: 

### 1. Superadmin 

Aktor: Ketua RT / Sekretaris Memiliki kontrol mutlak. Mengelola Role Mapping pengguna, memvalidasi dan memberikan persetujuan surat keluar, mengatur master data, serta mengeksekusi mutasi perpindahan kependudukan warga. 

### 2. Admin Keuangan 

Aktor: Bendahara Lingkungan Memiliki akses eksklusif ke modul kas. Bertanggung jawab mencatat uang kas masuk/keluar, memvalidasi resi, dan mengelola penerbitan Laporan Transparansi Keuangan berkala. 

### 3. Admin Lapangan 

### 4. User 

Aktor: Kader Dasawisma (Dawis) Bertugas memvalidasi data demografi fisik di lapangan. Sangat dibatasi oleh aturan Data Scoping, hanya dapat mengelola data kependudukan & titik peta di wilayah tugas spesifiknya. 

Aktor: Warga Terdaftar Akses level dasar untuk membuat permohonan surat, melakukan booking barang inventaris, menerima notifikasi WA otomatis, serta melihat (Read-Only) Laporan Transparansi Keuangan. 

Halaman 1 dari 5 

Dokumen Internal - Proyek Pak RT 

## 3. RINCIAN MODUL & FITUR SISTEM 

### 3.1 Dasbor Utama & Analitik Demografi 

Dasbor adalah pusat informasi bagi pengguna saat pertama kali masuk ke aplikasi. Secara umum menampilkan ringkasan saldo kas, panel notifikasi, dan papan pengumuman. Khusus untuk Superadmin, dasbor diperluas dengan menampilkan metrik demografi real-time untuk memantau status fisik kependudukan. 



<!-- Start of picture text -->
145 32 85<br>WARGA = RUMAH WARGA TIDAK = RUMAH MENGONTRAK / KOST<br>(Warga Tetap) Memiliki KTP RT 11 (Non-Domisili Fisik) KTP terdaftar di Warga pendatang yang menetap<br>dan secara fisik menempati rumah RT 11, namun fisik rumah sudah dengan status menyewa<br>tersebut. berpindah/kosong. (mengontrak) atau indekos.<br><!-- End of picture text -->

Catatan Sistem: Kalkulasi jumlah warga pada panel Dasbor bergantung langsung pada keakuratan validasi lapangan yang dilakukan oleh Admin Lapangan (Dawis) melalui modul Kependudukan. 

### 3.2 Modul Kependudukan 

Modul operasional untuk manajemen Master Data kependudukan. Termasuk pengelolaan entitas Kartu Keluarga (KK), manajemen data pribadi/fisik warga secara detail, serta pelacakan siklus mutasi (warga yang pindah wilayah atau warga pendatang baru). 

### 3.3 Modul Peta Digital (Sistem Informasi Geografis / GIS) 

Peta interaktif terintegrasi OpenStreetMap (OSM). Fitur ini memvisualisasikan data demografis warga ke dalam bentuk tata letak visual wilayah. Menggunakan pin marker untuk menandai koordinat aktual setiap rumah warga, dilengkapi dengan fitur pencarian pintar untuk membantu identifikasi visual dengan cepat. Modul ini penting untuk memvalidasi apakah "Warga = Rumah" secara geografis. 

Halaman 2 dari 5 

Dokumen Internal - Proyek Pak RT 

## LANJUTAN FITUR & MODUL SISTEM 

|Modul Aplikasi|Hak Akses|Deskripsi Fungsionalitas Operasional|
|---|---|---|
|3.4 Administrasi<br>Surat Dinamis|Warga,<br>Superadmin|Fasilitas pengajuan permohonan surat melaluiformisian yang<br>berubah menyesuaikan jenis surat. Warga dapat melihat riwayat<br>pengajuan dan mengunduh berkas akhir dalam format PDF.<br>Sistem mengotomatisasi injeksi Tanda Tangan berupaQR Code<br>validasi digital segera setelah Superadmin menyetujui pengajuan.|
|3.5 Keuangan &<br>Laporan Transparansi|Bendahara,<br>Warga|Modul pencatatan buku kas (pemasukan dan pengeluaran).<br>Pencatatan pemasukan akan terintegrasi dengan validasi<br>pembacaan resi yang dikirim warga. Untuk menghindari<br>kecurigaan warga, sistem memuat fiturLaporan Transparansi<br>yang bersifatRead-Onlybagi seluruh warga untuk melihat riwayat<br>arus kas lingkungan.|
|3.6 Manajemen<br>Inventaris|Warga,<br>Superadmin|Katalog digital yang menampilkan daftar aset/barang milik RT<br>(contoh: tenda, kursi lipat, perangkat sound system). Warga dapat<br>melakukanbookingpeminjaman melalui aplikasi. Superadmin<br>mengelola jadwal ketersediaan barang dan wajib menginput<br>kondisi fisik barang saat dipinjam maupun dikembalikan.|
|3.7 Pengaturan &<br>Integrasi|Superadmin|Panel kendali utama untuk mengonfigurasi pengaturan<br>fundamental aplikasi. Meliputi manajemen pemetaan jabatan<br>(Role Mapping) dan manajemen parameter integrasi API<br>(misalnya: manajemen kunci token untuk koneksiWhatsApp<br>Gateway).|



Halaman 3 dari 5 

Dokumen Internal - Proyek Pak RT 

## 4. DAFTAR LAYANAN SURAT (DUKUNGAN MVP) 

Untuk rilis Versi 1.0, aplikasi Pak RT dilengkapi dengan mesin formulir dinamis (JSON-based) yang mampu melayani 11 jenis surat dengan parameter yang berbeda-beda, terbagi dalam tiga kategori dokumen: 

|No|Jenis Dokumen / Layanan|Keterangan & Fungsi Utama|
|---|---|---|
|A. ADM|INISTRASI KEPENDUDUKAN (DASAR)||
|1|Surat Pengantar Pembuatan KTP/KK|Diperlukan untuk memperbarui data e-KTP,<br>pencetakan ulang dokumen rusak, atau penerbitan<br>KTP awal.|
|2|Surat Pengantar Pindah Domisili|Rekomendasi administratif bagi warga yang ingin<br>pindah keluar dari wilayah, atau pendatang baru.|
|3|Surat Keterangan Kelahiran|Syarat pengantar di tingkat lingkungan untuk<br>mengurus Akta Kelahiran di Disdukcapil.|
|4|Surat Keterangan Kematian|Pengantar legalitas proses pemakaman dan<br>pembuatan Akta Kematian anggota keluarga.|
|B. KEP<br>|ERLUAN SOSIAL & EKONOMI TERAPAN||
|5|Surat Keterangan Tidak Mampu (SKTM)|Prasyarat utama warga untuk mengajukan bantuan<br>sosial, keringanan biaya RS, atau beasiswa.|
|6|Surat Keterangan Domisili|Bukti administratif tempat tinggal untuk lampiran<br>melamar pekerjaan atau pembukaan rekening bank.|
|7|Surat Keterangan Usaha (SKU)|Menyatakan bahwa warga memiliki usaha mikro<br>sebagai syarat kelengkapan pinjaman KUR di bank.|
|8|Surat Keterangan Berkelakuan Baik|Dokumen prasyarat rujukan sebelum warga<br>mengurus Surat Keterangan Catatan Kepolisian<br>(SKCK).|
|C. INTE|RNAL KELEMBAGAAN RUKUN TETANGGA||
|9|Surat Undangan Rapat|Sirkulasi undangan resmi untuk musyawarah wilayah,<br>acara 17-an, atau kerja bakti masal.|
|10|Surat Edaran|Pengumuman iuran insidental, jadwal Siskamling<br>bulanan, atau himbauan keamanan hari raya.|
|11|Surat Tugas|Mandat formal bagi warga atau pengurus untuk<br>mewakili acara/forum di tingkat RW atau Kelurahan.|



Dokumen Internal - Proyek Pak RT 

Halaman 4 dari 5 

## 5. ARSITEKTUR DATA & SPESIFIKASI TEKNIS 

### 5.1 Desain Basis Data (Database Design) 

- Pemisahan Entitas Fundamental: Untuk menjaga fleksibilitas, arsitektur basis data memisahkan struktur menjadi: Tabel Account (menangani manajemen login credential), Tabel KartuKeluarga (bertindak sebagai pengikat relasi nomor KK dan penyimpan titik koordinat rumah pusat), serta Tabel Warga (menyimpan data demografi individu dengan enkripsi pada field sensitif). 

- Status Domisili Dinamis: Identifikasi "Warga=Rumah", "Pindah", atau "Mengontrak" akan disematkan sebagai kolom relasional pada tabel kependudukan, sehingga statistik dasbor dapat menghitung query ini secara cepat dan tepat (O(1) aggregasi). 

- Skema Form JSONB: Fitur unggulan di mana parameter seluruh form surat (untuk ke-11 jenis surat) disimpan ke dalam satu kolom tipe data JSONB di PostgreSQL. Sistem ini menghindarkan aplikasi dari desain anti-pattern di mana setiap surat membutuhkan tabel yang berbeda (EAV Model). 

### 5.2 Stack Teknologi & Integrasi 

- Arsitektur Backend: Java 21 (LTS) dikombinasikan dengan Spring Boot 3.x. Menjalankan model asinkron memanfaatkan Virtual Threads (Project Loom) agar notifikasi sistem ke WhatsApp Gateway dapat dijalankan paralel tanpa membebani (blocking) server utama. 

- Lapisan Data (ORM & Database): PostgreSQL difungsikan dengan modul PostGIS untuk pemrosesan geolokasi. Lapisan komunikasi ditangani via Spring Data JPA, Hibernate, dan Hypersistence Utils. Keamanan API dibalut menggunakan token JWT (JSON Web Token) dan Spring Security. 

- Frontend (UI/UX) & Mobile: Aplikasi antarmuka dibangun dari nol (from scratch) menggunakan pustaka React JS dengan bahasa Typescript, di-*bundle* menggunakan Vite. Styling memanfaatkan kerangka Tailwind CSS, dibantu react-hook-form untuk performa input form ringan. Untuk kompilasi menjadi Mobile App (APK), sistem menjembataninya melalui Capacitor untuk integrasi fungsional GPS dan direktori lokal (*Filesystem* PDF). 

### 5.3 Skema Infrastruktur Deployment 

Aplikasi disiapkan untuk peluncuran (deployment) dalam *Virtual Private Server* (VPS) berbasis operasi Ubuntu Linux. Spesifikasi minimum server adalah: Prosesor 2 Core, RAM 8GB, dengan 100GB NVMe Storage. Ekosistem akan dijalankan dalam manajemen klaster lokal via Docker Desktop / Docker Compose dengan pengarahan lalu-lintas jaringan memanfaatkan Reverse Proxy Nginx. 

Dokumen Internal - Proyek Pak RT 

Halaman 5 dari 5 

