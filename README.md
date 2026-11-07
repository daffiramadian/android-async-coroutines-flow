
# Navigation Compose, Layered Architecture, REST API, Kotlin Coroutines & Flow

Repository ini berisi proyek praktikum mata kuliah **Pemrograman Aplikasi Perangkat Bergerak (CIF62041)**, Fakultas Ilmu Komputer, Universitas Brawijaya.

NavigationLab dikembangkan melalui Modul Praktikum 05–08 dengan fokus pada implementasi navigasi, arsitektur aplikasi, integrasi REST API, serta pengelolaan asynchronous menggunakan Kotlin Coroutines dan Flow.

## Modul Praktikum

### Modul 05 - Navigation Compose
Mengimplementasikan navigasi antarhalaman menggunakan Navigation Compose dengan empat destination:
- Home Screen
- Detail Screen
- Profile Screen
- About Screen

Navigasi dikelola menggunakan `NavController` dan `popBackStack()`.

### Modul 06 - Layered Architecture dan MVVM
Memisahkan kode aplikasi menjadi beberapa layer agar UI tidak bergantung langsung pada sumber data.

```text
UI Layer
   ↓
ViewModel
   ↓
Repository
   ↓
StudentLocalDataSource
```

Data mahasiswa pada modul ini masih menggunakan penyimpanan in-memory.

### Modul 07 - REST API dan Retrofit
Mengganti sumber data in-memory dengan REST API DummyJSON menggunakan Retrofit dan Gson. Komunikasi asynchronous masih menggunakan mekanisme callback melalui `Call.enqueue()`.

### Modul 08 - Kotlin Coroutines dan Flow

Modul 08 berfokus pada refactoring komunikasi REST API dari pola callback menjadi berbasis Kotlin Coroutines dan Flow.

Perubahan utama:
- Mengubah API service, remote data source, dan repository menjadi menggunakan `suspend function`.
- Mengelola operasi asynchronous melalui `viewModelScope.launch`.
- Menggunakan `MutableStateFlow` dan `StateFlow` untuk pengelolaan UI state.
- Mengonsumsi state menggunakan `collectAsStateWithLifecycle()`.
- Mengimplementasikan pencarian profil reaktif menggunakan operator Flow.
- Menambahkan fitur Refresh dan Retry berdasarkan query yang sedang aktif.
- Menangani `CancellationException` agar pembatalan coroutine tidak dianggap sebagai error.

**Alur arsitektur Modul 08:**

```text
Compose UI
    │ collectAsStateWithLifecycle()
    ▼
ViewModel
    │ viewModelScope.launch
    │ MutableStateFlow
    ▼
StudentRepository
    │ suspend function
    ▼
StudentRemoteDataSource
    ▼
StudentApiService (Retrofit Suspend)
    │ HTTP Request
    ▼
DummyJSON REST API
```

## Fitur Aplikasi

- Menampilkan daftar mahasiswa dari REST API.
- Menampilkan detail mahasiswa berdasarkan ID.
- Halaman Profile dan About.
- Loading indicator ketika request berlangsung.
- Error state dengan tombol Coba Lagi.
- Refresh untuk memuat ulang data.
- Pencarian profil secara otomatis tanpa tombol manual.
- Navigasi antarhalaman dengan back stack.

## Struktur Proyek

```text
data/
├── model/
│   └── Student.kt
├── repository/
│   ├── StudentRepository.kt
│   └── DefaultStudentRepository.kt
└── remote/
    ├── StudentApiService.kt
    ├── RetrofitClient.kt
    ├── StudentRemoteDataSource.kt
    └── dto/
        ├── UserDto.kt
        └── UserResponse.kt

ui/
├── home/
│   ├── HomeUiState.kt
│   └── HomeViewModel.kt
├── detail/
│   ├── DetailUiState.kt
│   └── DetailViewModel.kt
└── screen/
    ├── HomeScreen.kt
    ├── DetailScreen.kt
    ├── ProfileScreen.kt
    └── AboutScreen.kt
```

## Implementasi Modul 08

### 1. Retrofit Suspend Function

`StudentApiService` menggunakan suspend function untuk mengambil daftar mahasiswa, detail mahasiswa, dan melakukan pencarian.

```kotlin
interface StudentApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 10
    ): UserResponse

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): UserDto

    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String
    ): UserResponse
}
```

### 2. StateFlow

ViewModel menggunakan `MutableStateFlow` secara privat dan mengekspos `StateFlow` agar UI hanya dapat membaca state.

```kotlin
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

private val _searchQuery = MutableStateFlow("")
val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
```

Pada sisi Compose, state dikonsumsi menggunakan:

```kotlin
val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
```

### 3. Reactive Search dengan Flow

Pencarian otomatis dilakukan ketika pengguna mengetik pada TextField. Proses pencarian menggunakan tiga operator utama:

| Operator | Fungsi |
|---|---|
| `debounce(500)` | Menunggu 500 ms setelah pengguna berhenti mengetik. |
| `distinctUntilChanged()` | Mencegah request dengan query yang sama. |
| `collectLatest()` | Membatalkan request sebelumnya ketika query baru masuk. |

Jika query kosong, aplikasi akan memuat kembali daftar mahasiswa awal.

### 4. Error Handling, Retry, dan Refresh

- **Loading:** Menampilkan indikator ketika request berlangsung.
- **Error:** Menampilkan pesan kesalahan ketika request gagal.
- **Cancellation:** Melempar kembali `CancellationException` agar pembatalan coroutine tidak dianggap sebagai error.
- **Retry:** Mengulang request sesuai query yang sedang aktif.
- **Refresh:** Memuat ulang data menggunakan `viewModelScope.launch` sesuai kondisi pencarian saat ini.

## REST API

API yang digunakan adalah [DummyJSON](https://dummyjson.com/).

**Base URL:**

```text
https://dummyjson.com/
```

| Kebutuhan | Endpoint |
|---|---|
| Daftar mahasiswa | `GET /users?limit=10` |
| Detail mahasiswa | `GET /users/{id}` |
| Pencarian profil | `GET /users/search?q={query}` |

Permission internet pada `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Lingkungan Pengembangan dan Dependensi

| Komponen | Teknologi |
|---|---|
| IDE | Android Studio |
| Bahasa | Kotlin |
| UI Toolkit | Jetpack Compose |
| Navigation | Navigation Compose |
| Architecture | Layered Architecture / MVVM |
| Asynchronous | Kotlin Coroutines |
| Reactive Stream | Kotlin Flow / StateFlow |
| HTTP Client | Retrofit |
| JSON Converter | Gson |
| API | DummyJSON |
| Target SDK | Android 14 / API Level 35+ |

**Dependensi utama:**

- `kotlinx-coroutines-android:1.11.0`
- `lifecycle-runtime-compose:2.9.4`
- `lifecycle-viewmodel-compose:2.9.4`
- Retrofit (suspend support)
- Gson Converter
