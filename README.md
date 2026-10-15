# TP 5 DPBO — PeopleData (Data Musisi Xdinary Heroes)

Tugas Praktikum 5 Mata Kuliah Desain dan Pemrograman Berorientasi Objek (DPBO).


Program ini mengelola data musisi Xdinary Heroes (`People`) berbasis GUI Java Swing yang terintegrasi penuh dengan database **MySQL** melalui **JDBC**, menggantikan sistem penyimpanan sementara `ArrayList` dengan persistensi data permanen serta dilengkapi validasi input dan konfirmasi keamanan data.

## Janji

Saya Sheva Desprianty Hermawan dengan NIM [2511645] mengerjakan Tugas Praktikum 5 pada Mata Kuliah Desain dan Pemrograman Berorientasi Objek (DPBO) untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

## Struktur Folder

```text
TP5DPBO2526C1/
├── README.md
├── db_people.sql
├── TP5DPBO2526C1.iml
├── lib/
│   └── mysql-connector-j-26.7.0/
│       └── mysql-connector-j-26.7.0.jar
├── src/
│   ├── Database.java
│   ├── Person.java
│   ├── PeopleMenu.java
│   └── PeopleMenu.form
└── Dokumen/
    └── demo.mp4
```

## Desain / Fungsi Program & Database

**Tema:** Data Musisi — Xdinary Heroes (`XDH001` - `XDH006`)

### Atribut Tabel MySQL (`db_people.sql` / `Person.java`):

| # | Atribut | Tipe Data | Komponen GUI | Keterangan |
|---|---|---|---|---|
| 1 | **ID** | `VARCHAR(50)` | `JTextField` (`idField`) | Primary Key unik, contoh: `XDH001` |
| 2 | **Nama** | `VARCHAR(100)` | `JTextField` (`namaField`) | Nama lengkap musisi / anggota Xdinary Heroes |
| 3 | **Tahun Lahir** | `INT` | `JTextField` (`tahunLahirField`) | Angka tahun lahir (misal: `1998`) |
| 4 | **Kategori / Peran** | `VARCHAR(50)` | `JComboBox` (`kategoriComboBox`) | Opsi: `Vocalist`, `Guitarist`, `Bassist`, `Drummer`, `Keyboardist`, `Synthesizer` |
| 5 | **Jenis Kelamin** | `VARCHAR(50)` | `JComboBox` (`genderComboBox`) | Opsi: `Laki-laki`, `Perempuan` **[Fitur Bonus]** |

### File Program:
* **`db_people.sql`** — Script SQL DDL & DML untuk membuat database `db_people` dan tabel `person` beserta 6 data awal anggota Xdinary Heroes.
* **`src/Database.java`** — Class manajemen koneksi JDBC MySQL (`jdbc:mysql://localhost:3306/db_people`), menyediakan method pengungkit query `selectQuery()` dan `insertUpdateDeleteQuery()`.
* **`src/Person.java`** — Model data entitas person beserta konstruktor dan getter/setter.
* **`src/PeopleMenu.java`** — Window `JFrame` utama yang menangani pengolahan GUI, penanganan query CRUD ke MySQL, event listener, serta validasi form.
* **`src/PeopleMenu.form`** — Binding layout GUI Designer IntelliJ IDEA.

### Fungsi Tiap Komponen:
* **Form (`JTextField` / `JComboBox`)** untuk menginput dan memperbarui data musisi.
* **`addUpdateButton`** berlabel `Add` saat `selectedIndex == -1`, berubah menjadi `Update` saat baris tabel diklik.
* **`deleteButton`** disembunyikan (`setVisible(false)`) pada mode Add, dan dimunculkan otomatis saat baris tabel diklik.
* **`cancelButton`** memanggil `clearForm()` untuk mereset seluruh form input dan membatalkan mode edit.
* **`personTable` (`JTable` + `DefaultTableModel`)** untuk menampilkan data dari MySQL. Klik baris akan mengambil nilai kolom langsung dari model tabel untuk diisikan ke form via `mousePressed`.

## Method Program

| Method | Fungsi |
|---|---|
| `main` | Titik masuk utama aplikasi: inisialisasi `PeopleMenu`, mengatur ukuran window ($750 \times 650$), peletakan di tengah layar, dan konfigurasi `EXIT_ON_CLOSE`. |
| `PeopleMenu()` | Constructor: membuat instansiasi `Database`, memasang data opsi pada `JComboBox`, memanggil `setTable()`, menyembunyikan `deleteButton`, serta mendaftarkan listener tombol dan tabel. |
| `setTable()` | Membaca seluruh baris dari MySQL via `SELECT * FROM person`, menyusun `DefaultTableModel` berisikan kolom (`No`, `ID`, `Nama`, `Tahun Lahir`, `Peran/Kategori`, `Jenis Kelamin`), dan mengembalikannya ke `personTable`. |
| `insertData()` | **Create**: Mengambil data form, memvalidasi kelengkapan input, mengecek ID duplikat via query `SELECT`, lalu menyimpan data baru via query `INSERT INTO person VALUES(...)`, merefresh tabel, dan mereset form. |
| `updateData()` | **Update**: Mengambil data dari form (`id` dikunci *read-only*), memvalidasi input, mengeksekusi query `UPDATE person SET ... WHERE id = '...'`, merefresh tabel, dan mereset form. |
| `deleteData()` | **Delete**: Membaca Primary Key `id` dari baris yang dipilih, mengeksekusi query `DELETE FROM person WHERE id = '...'` setelah dikonfirmasi `YES_OPTION`, lalu merefresh tabel dan mereset form. |
| `clearForm()` | **Reset Form**: Mengosongkan text field, mengembalikan `idField.setEditable(true)`, mereset `JComboBox` ke indeks 0, mereset tombol ke `Add`, menyembunyikan `deleteButton`, dan mengatur `selectedIndex = -1`. |

## Penjelasan Alur

1. **Program Jalan**: Constructor `PeopleMenu()` memanggil `Database()` untuk terkoneksi ke server MySQL, `setTable()` mengambil data dari database, `JComboBox` diinisialisasi, dan `clearForm()` mengatur mode awal aplikasi ke mode Add.
2. **Read (Tampilan Awal)**: Tabel menampilkan seluruh data awal anggota Xdinary Heroes (`XDH001` - `XDH006`) langsung dari tabel `person` di MySQL.
3. **Create**: Pengguna mengisikan data di form input $\rightarrow$ Klik tombol **Add** (`selectedIndex == -1`) $\rightarrow$ Method `insertData()` memvalidasi input kosong $\rightarrow$ Cek ketersediaan ID di database $\rightarrow$ Jika ID unik, query `INSERT INTO` dieksekusi ke MySQL $\rightarrow$ Tabel direfresh via `setTable()` $\rightarrow$ Form di-reset via `clearForm()` $\rightarrow$ Dialog pop-up sukses ditampilkan.
4. **Select (Klik Baris Tabel)**: Pengguna mengklik salah satu baris pada `personTable` $\rightarrow$ Event `mousePressed` menyimpan `selectedIndex` $\rightarrow$ Nilai kolom (`id`, `nama`, `thn_lahir`, `kategori`, `jenis_kelamin`) dibaca langsung dari model tabel $\rightarrow$ Form input terisi otomatis, `idField` dikunci (`setEditable(false)`), tombol **Add** berubah menjadi **Update**, dan tombol **Delete** muncul di layar.
5. **Update**: Pengguna mengubah data pada form $\rightarrow$ Klik tombol **Update** $\rightarrow$ Method `updateData()` memvalidasi data $\rightarrow$ Query `UPDATE person SET ...` dieksekusi ke MySQL $\rightarrow$ Tabel direfresh $\rightarrow$ Form di-reset kembali ke mode Add $\rightarrow$ Dialog sukses ditampilkan.
6. **Delete**: Pengguna memilih baris data $\rightarrow$ Klik tombol **Delete** $\rightarrow$ Muncul pop-up konfirmasi `JOptionPane.showConfirmDialog` *"Apakah Anda yakin ingin menghapus data musisi [Nama]?"* $\rightarrow$ Jika memilih **YES**, method `deleteData()` mengeksekusi query `DELETE` ke MySQL $\rightarrow$ Data terhapus dari database, tabel direfresh, dan form di-reset. Jika memilih **NO**, proses penghapusan dibatalkan.
7. **Cancel**: Kapan saja tombol **Cancel** diklik $\rightarrow$ Method `clearForm()` dijalankan untuk mengosongkan seluruh field input, mengembalikan tombol ke mode Add, dan menyembunyikan tombol Delete.
8. **Penyimpanan Data Permanen**: Seluruh perubahan data (Create, Update, Delete) tersimpan secara permanen pada database MySQL `db_people`.

## Validasi / Error Handling

| Bagian | Kondisi Tidak Valid | Penanganan / Pesan Dialog Prompt |
|---|---|---|
| **Field Kosong** | `ID`, `Nama`, atau `Tahun Lahir` kosong saat Insert/Update | `WARNING_MESSAGE`: *"Semua kolom input wajib diisi!"* |
| **Combo Box** | `Kategori` atau `Jenis Kelamin` belum dipilih (`selectedIndex == 0`) | `WARNING_MESSAGE`: *"Silakan pilih Peran dan Jenis Kelamin yang valid!"* |
| **Duplikat ID** | Primary Key `ID` sudah ada di database saat Insert | `ERROR_MESSAGE`: *"ID '[id]' sudah terdaftar! Gunakan ID lain."* |
| **Tahun Lahir** | Input tahun lahir mengandung huruf/karakter non-angka (`NumberFormatException`) | `ERROR_MESSAGE`: *"Tahun Lahir harus berupa angka!"* |
| **Konfirmasi Hapus** | Menekan tombol **Delete** | `showConfirmDialog` (`YES_NO_OPTION`): *"Apakah Anda yakin ingin menghapus data musisi [Nama]?"* |
| **Koneksi Database** | Server MySQL mati atau query SQL bermasalah (`SQLException`) | `ERROR_MESSAGE`: *"Gagal mengambil/menyimpan data dari database: [pesan_error]"* |

## Dokumentasi Demonstrasi Operasi CRUD

<video src="Dokumen/demo.mp4" controls width="100%"></video>

