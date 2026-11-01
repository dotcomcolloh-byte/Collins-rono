# TELEFAM PRODUCTION PLATFORM (telefam.company)

Telefam is a production-grade, secure, multi-platform social and messaging application designed for Android (smartphones, foldables, tablets, desktop/DeX) and backed by a high-performance Ktor + PostgreSQL microservice engine.

---

## 1. Architectural Highlights

### Mobile Application (Jetpack Compose + Material 3)
- **Zero-Trust E2EE Messaging:** Built-in Signal Protocol engine supporting X3DH (Extended Triple Diffie-Hellman) prekey exchange, Double Ratchet cryptographic progression, and AES-256-GCM AEAD encryption. Users can independently inspect SHA-256 Identity Key Fingerprints, Ratchet state chains, and scan 60-digit safety numbers with deterministic QR code matrices.
- **Dynamic Theming & Wallpapers:** Real-time persistence of chat themes (`crimson_silk`, `savannah_sunset`, `aurora_cosmos`, `emerald_botanical`, `sahara_twilight`, `alpine_mist`) and custom outgoing message bubble colors (`#C80815`, `#1E88E5`, `#43A047`, `#FB8C00`, `#8E24AA`, `#00ACC1`). Applied in real time to both settings previews and active 1:1 chat backgrounds.
- **KLIPY Media Integration:** Stickers, animated GIFs, and Video Clips served via paginated proxy with responsive Lucide SVG icons, zero mock data, and smooth previews.
- **Identity & Authentication:** Dual authentication with PBKDF2-SHA256 password hashing and Google Sign-In via Jetpack `CredentialManager` (`GetSignInWithGoogleOption`).
- **Responsive & Adaptive:** Supports phones, foldables, tablets, and desktop displays with dynamic window size classes, `BoxWithConstraints`, and responsive grid densities.
- **Policy & Security Compliance:** No hardcoded secrets, zero-permission Android Photo Picker, zero synthetic mock data, and strict biometric/passkey support.

---

## 2. Backend Architecture (Ktor + PostgreSQL)

The backend service is located in `/backend` and provides complete database persistence via PostgreSQL with strict Row-Level Security (RLS) and parameterized SQL queries.

### PostgreSQL Tables & Schema (`backend/sql/telefam_postgres_schema.sql`)
1. `telefam_users` — Primary account table (UUID, email, password hash/salt, Google sub ID, verification status).
2. `telefam_profiles` — Profile information (full name, username, phone, gender, DOB, bio, avatar checksums, cooldowns).
3. `telefam_media_fingerprints` — SHA-256 and perceptual dHash vault for media deduplication and forensic isolation.
4. `telefam_devices` — Device telemetry records with server-derived composite hashes (anti-spoofing).
5. `telefam_otp_challenges` — OTP requests with HMAC digest, attempt counters, and strict expiry cooldowns.
6. `telefam_encrypted_sessions` — AES-256-GCM encrypted session states with SHA-256 token lookup.
7. `telefam_rate_limits` — Sliding-window IP and user rate-limiting to prevent brute force.
8. `telefam_forensic_audit_logs` — Immutable audit records tracking auth events, security risk scores, and telemetry.
9. `telefam_user_privacy_settings` — Privacy rules (calls, screenshots, copy, downloads, theme ID, bubble color).
10. `telefam_user_follows` — Social graph with idempotency keys and internal security scoring.
11. `telefam_contact_hashes` — Zero-knowledge contact discovery matching phone and email hashes.
12. `telefam_user_blocks` & `telefam_user_reports` — Safety, blocking, and multi-step reporting mechanisms.
13. `telefam_user_posts` — Post studio publications with views, likes, and category tags.
14. `telefam_user_subscriptions` — Creator subscriber graph with RLS isolation.
15. `telefam_e2ee_key_bundles` — X3DH prekey registry (Identity keys, signed prekeys, one-time prekeys).
16. `telefam_chat_conversations` & `telefam_chat_messages` — 1:1 ciphertext-only chat store (zero-knowledge server).

---

## 3. Endpoints & API Specifications

All endpoints are hosted under `https://api.telefam.company` and mirror modern REST/HTTP-2 standards:

### Authentication (`/v1/auth`)
| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/auth/signup` | Register email & password. Sends 6-digit OTP verification. |
| `POST` | `/v1/auth/login` | Authenticate user credentials, check device trust, return session & profile. |
| `POST` | `/v1/auth/google` | Synchronize and authenticate Google OAuth credentials via `googleSubId`. |
| `POST` | `/v1/auth/otp/send` | Dispatches OTP for sign up, new device authorization, or step-up verification. |
| `POST` | `/v1/auth/otp/verify` | Validates OTP challenge, establishes session, marks email verified. |
| `POST` | `/v1/auth/password/reset` | Resets password after OTP challenge validation and revokes stale sessions. |
| `POST` | `/v1/auth/telemetry-sync` | Synchronizes device telemetry and network fingerprinting. |

### User Profile (`/v1/profile`)
| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/profile/me` | Fetch active profile, avatar storage keys, and subscription metrics. |
| `GET` | `/v1/profile/{id}` | Retrieve public profile for any user by UUID. |
| `GET` | `/v1/profile/username/check` | Verify username availability and provide AI/heuristic suggestions. |
| `POST` | `/v1/profile/setup` | Complete first-time profile registration. |
| `PUT` | `/v1/profile/update` | Update existing profile details with cooldown validations. |
| `POST` | `/v1/profile/avatar/upload` | Sandbox quarantine media processing and thumbnail generation. |

### Privacy & Settings (`/v1/privacy`, `/v1/settings`)
| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/privacy/settings` | Retrieve user privacy settings, chat background theme, and bubble color. |
| `PUT` | `/v1/privacy/settings` | Update user privacy rules with RLS verification. |
| `PUT` | `/v1/settings/account-security` | Configure 2-step verification, passkeys, and account visibility. |

### Contacts & Discovery (`/v1/contacts`)
| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/contacts/discover` | Search registered users by query, country, or mutuals (no demo/mock data). |
| `POST` | `/v1/contacts/upload-hashes` | Zero-knowledge address book matching via SHA-256 hashes. |

### 1:1 Chat & E2EE (`/v1/chat`)
| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/chat/prekeys/upload` | Upload local X3DH prekey bundle (identity key, signed prekey, one-time keys). |
| `GET` | `/v1/chat/prekeys/bundle` | Retrieve peer X3DH prekey bundle to initiate E2EE Double Ratchet session. |
| `GET` | `/v1/chat/contacts` | Retrieve list of eligible 1:1 messaging contacts. |
| `POST` | `/v1/chat/send` | Submit ciphertext-only message payload. |
| `POST` | `/v1/chat/react` | Submit encrypted message reaction. |
| `POST` | `/v1/chat/disappearing` | Update disappearing message timer (Off, 24h, 7d, 30d). |

### Stickers & Rich Media (`/v1/klipy`)
| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/klipy/media` | Curated stickers, animated GIFs, and short clips with pagination and search. |

---

## 4. Setup & Deployment Instructions

### Prerequisites
- JDK 17 or JDK 21
- PostgreSQL 14+ instance
- Android SDK 34+ / Gradle 8.10+

### Database Initialization
```bash
psql -U <your_postgres_user> -d <your_db_name> -f backend/sql/telefam_postgres_schema.sql
```

### Backend Configuration (`backend/.env`)
Copy `backend/.env.example` to `backend/.env` and update environment variables:
```properties
SERVER_HOST=0.0.0.0
SERVER_PORT=8080
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/telefam_production
POSTGRES_USER=telefam_rw
POSTGRES_PASSWORD=your_strong_password
POSTGRES_MAX_POOL_SIZE=24
SERVER_FINGERPRINT_PEPPER=your_pepper_hex
SESSION_AES_GCM_MASTER_KEY=your_32_byte_hex_key
```

### Running the Backend
```bash
cd backend
gradle run
```

### Running the Android Client
Open the project in Android Studio or build via command line:
```bash
gradle :app:assembleDebug
```
Ensure secrets are configured in AI Studio Secrets panel or `.env`:
- `TELEFAM_API_BASE_URL` — Base API URL (e.g. `https://api.telefam.company` or local endpoint).
- `TELEFAM_GOOGLE_WEB_CLIENT_ID` — Google OAuth Client ID for Credential Manager authentication.

---

## 5. Security & Verification Guarantees
- **No Mock or Seeded Data:** Client directory and messaging operate against verified user database records.
- **Zero-Knowledge Architecture:** Message contents and attachments are encrypted client-side using Double Ratchet before reaching the server. The server stores ciphertext exclusively.
- **Row-Level Security (RLS):** All user settings, profile updates, and messages are protected by PostgreSQL RLS policies tied to authenticated user IDs.
- **Anti-Brute Force Protection:** Exponential cooldowns and rate limits prevent password and OTP hammering.
- **Adaptive Screen Sizing:** Tested across phone (compact), foldable (medium), and tablet/desktop (expanded) viewports.
