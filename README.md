# Template Compose

Starter Android ini menyediakan bagian-bagian yang sering dipakai saat membuat aplikasi. Gunakan yang diperlukan; fitur contoh tidak wajib dipakai pada proyek baru.

## Konfigurasi environment

Modul `app` menyediakan flavor `dev`, `staging`, dan `production`. Untuk sementara ketiganya memakai DummyJSON agar contoh API dapat dijalankan. Pilih build variant di Android Studio atau jalankan, misalnya:

```powershell
.\gradlew.bat :app:assembleDevDebug
```

URL API setiap flavor berada di `app/build.gradle.kts` dan dibaca kode Kotlin melalui `AppConfig`. Cari `TODO(template)` saat menyesuaikan starter untuk aplikasi baru. Setiap TODO ditempatkan pada konfigurasi atau contoh kode yang perlu diganti.

Irisan pertama menyiapkan environment. Irisan kedua menyiapkan dependensi untuk navigasi, ViewModel, API/JSON, DataStore, Room, Paging, biometrik, dan gambar jaringan. Belum ada implementasi fitur atau perubahan alur aplikasi pada irisan kedua. Penyimpanan token aman nantinya memakai Android Keystore sehingga tidak membutuhkan library tambahan. Login, penyimpanan sesi, contoh layar API, dan komponen UI reusable akan ditambahkan bertahap. Generator layar opsional dikerjakan setelah starter selesai.
