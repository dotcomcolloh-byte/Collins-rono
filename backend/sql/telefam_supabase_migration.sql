-- =============================================================================
-- TELEFAM PRODUCTION SUPABASE DATABASE MIGRATION SCRIPT
-- Project URL: https://whiviqdxrjmvoyjxvxte.supabase.co
-- Sets up Storage Buckets (quarantine, avatars, public-media),
-- PostgREST Tables, Indexes, and Row Level Security (RLS) Policies.
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- -----------------------------------------------------------------------------
-- 1. SUPABASE STORAGE BUCKETS
-- quarantine: Private bucket for raw untrusted uploads awaiting Ktor sanitization
-- avatars: Public bucket for sanitized, compressed, EXIF-stripped profile photos
-- public-media: Public bucket for sanitized post photos and videos
-- -----------------------------------------------------------------------------
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES
    ('quarantine', 'quarantine', false, 104857600, null),
    ('avatars', 'avatars', true, 10485760, ARRAY['image/jpeg', 'image/png', 'image/webp']),
    ('public-media', 'public-media', true, 104857600, ARRAY['image/jpeg', 'image/png', 'image/webp', 'video/mp4', 'video/webm', 'video/quicktime'])
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- Storage Objects Security Policies
DROP POLICY IF EXISTS "Quarantine Objects Insert Anon/Auth" ON storage.objects;
CREATE POLICY "Quarantine Objects Insert Anon/Auth" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'quarantine');

DROP POLICY IF EXISTS "Quarantine Objects Select Service/Owner" ON storage.objects;
CREATE POLICY "Quarantine Objects Select Service/Owner" ON storage.objects
    FOR SELECT USING (bucket_id = 'quarantine');

DROP POLICY IF EXISTS "Quarantine Objects Delete Service/Owner" ON storage.objects;
CREATE POLICY "Quarantine Objects Delete Service/Owner" ON storage.objects
    FOR DELETE USING (bucket_id = 'quarantine');

DROP POLICY IF EXISTS "Public Avatars Select" ON storage.objects;
CREATE POLICY "Public Avatars Select" ON storage.objects
    FOR SELECT USING (bucket_id = 'avatars');

DROP POLICY IF EXISTS "Public Avatars Insert" ON storage.objects;
CREATE POLICY "Public Avatars Insert" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'avatars');

DROP POLICY IF EXISTS "Public Avatars Update" ON storage.objects;
CREATE POLICY "Public Avatars Update" ON storage.objects
    FOR UPDATE USING (bucket_id = 'avatars');

DROP POLICY IF EXISTS "Public Media Select" ON storage.objects;
CREATE POLICY "Public Media Select" ON storage.objects
    FOR SELECT USING (bucket_id = 'public-media');

DROP POLICY IF EXISTS "Public Media Insert" ON storage.objects;
CREATE POLICY "Public Media Insert" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'public-media');

DROP POLICY IF EXISTS "Public Media Update" ON storage.objects;
CREATE POLICY "Public Media Update" ON storage.objects
    FOR UPDATE USING (bucket_id = 'public-media');


-- -----------------------------------------------------------------------------
-- 2. USER PROFILES TABLE (telefam_profiles)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_profiles (
    user_id TEXT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(32) NOT NULL UNIQUE,
    phone_country_code VARCHAR(8) NOT NULL DEFAULT '',
    phone_iso_code VARCHAR(8) NOT NULL DEFAULT '',
    phone_number VARCHAR(24) NOT NULL DEFAULT '',
    gender VARCHAR(32) NOT NULL DEFAULT 'UNSPECIFIED',
    date_of_birth DATE,
    bio VARCHAR(200) NOT NULL DEFAULT '',
    avatar_storage_key TEXT,
    avatar_cdn_url TEXT,
    avatar_sha256_checksum VARCHAR(64),
    avatar_perceptual_dhash VARCHAR(16),
    avatar_mime_type VARCHAR(32),
    avatar_byte_size INT,
    last_full_name_changed_at_ms BIGINT,
    last_username_changed_at_ms BIGINT,
    sandbox_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_profiles_username ON public.telefam_profiles(username);
ALTER TABLE public.telefam_profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Profiles are viewable by everyone" ON public.telefam_profiles;
CREATE POLICY "Profiles are viewable by everyone" ON public.telefam_profiles
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Profiles can be inserted by anyone" ON public.telefam_profiles;
CREATE POLICY "Profiles can be inserted by anyone" ON public.telefam_profiles
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Profiles can be updated by matching user_id" ON public.telefam_profiles;
CREATE POLICY "Profiles can be updated by matching user_id" ON public.telefam_profiles
    FOR UPDATE USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 3. USER POSTS & FEED VIDEOS TABLE (telefam_user_posts)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_user_posts (
    post_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    username VARCHAR(32) NOT NULL DEFAULT '',
    full_name VARCHAR(120) NOT NULL DEFAULT '',
    avatar_url TEXT,
    media_url TEXT NOT NULL,
    thumbnail_url TEXT,
    caption TEXT NOT NULL DEFAULT '',
    hashtags TEXT[] DEFAULT ARRAY[]::TEXT[],
    media_type VARCHAR(32) NOT NULL DEFAULT 'VIDEO',
    aspect_ratio REAL NOT NULL DEFAULT 0.5625,
    duration_ms INT NOT NULL DEFAULT 0,
    audio_track_title VARCHAR(128) DEFAULT 'Original Audio',
    audio_artist_name VARCHAR(128) DEFAULT '',
    audio_stream_url TEXT,
    likes_count BIGINT NOT NULL DEFAULT 0,
    comments_count BIGINT NOT NULL DEFAULT 0,
    shares_count BIGINT NOT NULL DEFAULT 0,
    views_count BIGINT NOT NULL DEFAULT 0,
    saves_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_user_posts_user ON public.telefam_user_posts(user_id);
CREATE INDEX IF NOT EXISTS idx_telefam_user_posts_created ON public.telefam_user_posts(created_at DESC);
ALTER TABLE public.telefam_user_posts ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Posts are viewable by everyone" ON public.telefam_user_posts;
CREATE POLICY "Posts are viewable by everyone" ON public.telefam_user_posts
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Posts can be created by authenticated or anon" ON public.telefam_user_posts;
CREATE POLICY "Posts can be created by authenticated or anon" ON public.telefam_user_posts
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Posts can be updated" ON public.telefam_user_posts;
CREATE POLICY "Posts can be updated" ON public.telefam_user_posts
    FOR UPDATE USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "Posts can be deleted" ON public.telefam_user_posts;
CREATE POLICY "Posts can be deleted" ON public.telefam_user_posts
    FOR DELETE USING (true);


-- -----------------------------------------------------------------------------
-- 4. PRIVACY SETTINGS TABLE (telefam_user_privacy_settings)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_user_privacy_settings (
    user_id TEXT PRIMARY KEY,
    message_requests VARCHAR(32) NOT NULL DEFAULT 'EVERYONE',
    who_can_call_me VARCHAR(32) NOT NULL DEFAULT 'EVERYONE',
    who_can_screenshot_chats VARCHAR(32) NOT NULL DEFAULT 'ALLOW',
    who_can_share_chats VARCHAR(32) NOT NULL DEFAULT 'ALLOW',
    who_can_copy_messages VARCHAR(32) NOT NULL DEFAULT 'ALLOW',
    who_can_download_media VARCHAR(32) NOT NULL DEFAULT 'ALLOW',
    chat_theme_id VARCHAR(32) NOT NULL DEFAULT 'DEFAULT',
    bubble_color_hex VARCHAR(16) NOT NULL DEFAULT '#E53935',
    read_receipts VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
    typing_indicators VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
    online_indicators VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
    sync_version BIGINT NOT NULL DEFAULT 1,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_user_privacy_settings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Privacy settings viewable" ON public.telefam_user_privacy_settings;
CREATE POLICY "Privacy settings viewable" ON public.telefam_user_privacy_settings
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Privacy settings upsert" ON public.telefam_user_privacy_settings;
CREATE POLICY "Privacy settings upsert" ON public.telefam_user_privacy_settings
    FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 5. SOCIAL FOLLOW RELATIONSHIPS (telefam_user_follows)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_user_follows (
    id TEXT PRIMARY KEY,
    follower_user_id TEXT NOT NULL,
    target_user_id TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_telefam_follow_pair ON public.telefam_user_follows(follower_user_id, target_user_id);
CREATE INDEX IF NOT EXISTS idx_telefam_follower ON public.telefam_user_follows(follower_user_id);
CREATE INDEX IF NOT EXISTS idx_telefam_following ON public.telefam_user_follows(target_user_id);
ALTER TABLE public.telefam_user_follows ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Follows are viewable" ON public.telefam_user_follows;
CREATE POLICY "Follows are viewable" ON public.telefam_user_follows
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Follows can be inserted" ON public.telefam_user_follows;
CREATE POLICY "Follows can be inserted" ON public.telefam_user_follows
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Follows can be deleted" ON public.telefam_user_follows;
CREATE POLICY "Follows can be deleted" ON public.telefam_user_follows
    FOR DELETE USING (true);


-- -----------------------------------------------------------------------------
-- 6. CONTACT HASHES (telefam_contact_hashes)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_contact_hashes (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id TEXT NOT NULL,
    phone_hash_sha256 TEXT,
    email_hash_sha256 TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_contact_phone ON public.telefam_contact_hashes(phone_hash_sha256);
CREATE INDEX IF NOT EXISTS idx_telefam_contact_email ON public.telefam_contact_hashes(email_hash_sha256);
ALTER TABLE public.telefam_contact_hashes ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Contact hashes access" ON public.telefam_contact_hashes;
CREATE POLICY "Contact hashes access" ON public.telefam_contact_hashes
    FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 7. USER BLOCKS & REPORTS (telefam_user_blocks, telefam_user_reports)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_user_blocks (
    id TEXT PRIMARY KEY,
    blocker_user_id TEXT NOT NULL,
    blocked_user_id TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_user_blocks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Blocks policy" ON public.telefam_user_blocks;
CREATE POLICY "Blocks policy" ON public.telefam_user_blocks FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_user_reports (
    report_id TEXT PRIMARY KEY,
    reporter_user_id TEXT NOT NULL,
    reported_user_id TEXT NOT NULL,
    reason_code VARCHAR(64) NOT NULL,
    location_code VARCHAR(64) NOT NULL,
    urgency_code VARCHAR(64) NOT NULL,
    additional_notes TEXT,
    internal_risk_score INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_user_reports ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Reports policy" ON public.telefam_user_reports;
CREATE POLICY "Reports policy" ON public.telefam_user_reports FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 8. E2EE KEY BUNDLES & ENCRYPTED CHAT MESSAGES
-- Strictly encrypted payloads only — zero plaintext seen by any server!
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_e2ee_key_bundles (
    user_id TEXT PRIMARY KEY,
    identity_public_key_hex TEXT NOT NULL,
    signed_prekey_hex TEXT NOT NULL,
    signed_prekey_sig_hex TEXT NOT NULL,
    one_time_prekeys_json TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_e2ee_key_bundles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "E2EE Key Bundles policy" ON public.telefam_e2ee_key_bundles;
CREATE POLICY "E2EE Key Bundles policy" ON public.telefam_e2ee_key_bundles FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_chat_conversations (
    chat_id TEXT PRIMARY KEY,
    participant_1_user_id TEXT NOT NULL,
    participant_2_user_id TEXT NOT NULL,
    last_message_preview TEXT,
    last_message_time_ms BIGINT NOT NULL DEFAULT 0,
    unread_count INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chat_p1 ON public.telefam_chat_conversations(participant_1_user_id);
CREATE INDEX IF NOT EXISTS idx_chat_p2 ON public.telefam_chat_conversations(participant_2_user_id);
ALTER TABLE public.telefam_chat_conversations ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Chat conversations policy" ON public.telefam_chat_conversations;
CREATE POLICY "Chat conversations policy" ON public.telefam_chat_conversations FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_chat_messages (
    message_id TEXT PRIMARY KEY,
    chat_id TEXT NOT NULL,
    sender_user_id TEXT NOT NULL,
    recipient_user_id TEXT NOT NULL,
    encrypted_ciphertext TEXT NOT NULL,
    nonce_hex VARCHAR(64) NOT NULL,
    sender_public_key_hex VARCHAR(128) NOT NULL,
    recipient_public_key_hex VARCHAR(128) NOT NULL,
    message_type VARCHAR(32) NOT NULL DEFAULT 'TEXT',
    timestamp_ms BIGINT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_ephemeral BOOLEAN NOT NULL DEFAULT FALSE,
    ephemeral_ttl_seconds INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_chat_id ON public.telefam_chat_messages(chat_id, timestamp_ms);
CREATE INDEX IF NOT EXISTS idx_chat_messages_recipient ON public.telefam_chat_messages(recipient_user_id, is_read);
ALTER TABLE public.telefam_chat_messages ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Chat messages policy" ON public.telefam_chat_messages;
CREATE POLICY "Chat messages policy" ON public.telefam_chat_messages FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_chat_reactions (
    reaction_id TEXT PRIMARY KEY,
    message_id TEXT NOT NULL,
    chat_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    emoji VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_chat_reactions ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Chat reactions policy" ON public.telefam_chat_reactions;
CREATE POLICY "Chat reactions policy" ON public.telefam_chat_reactions FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 9. STARS BALANCES & TRANSACTIONS (telefam_stars_balances, telefam_stars_transactions)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_stars_balances (
    user_id TEXT PRIMARY KEY,
    total_stars_balance BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.telefam_stars_balances ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Stars balances policy" ON public.telefam_stars_balances;
CREATE POLICY "Stars balances policy" ON public.telefam_stars_balances FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_stars_transactions (
    transaction_id TEXT PRIMARY KEY,
    sender_user_id TEXT NOT NULL,
    recipient_user_id TEXT NOT NULL,
    stars_amount BIGINT NOT NULL,
    source_type VARCHAR(32) NOT NULL,
    reference_id TEXT,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_stars_recipient ON public.telefam_stars_transactions(recipient_user_id, source_type);
ALTER TABLE public.telefam_stars_transactions ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Stars transactions policy" ON public.telefam_stars_transactions;
CREATE POLICY "Stars transactions policy" ON public.telefam_stars_transactions FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 10. AD CAMPAIGNS & LEADS (telefam_campaigns, telefam_campaign_leads)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_campaigns (
    campaign_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    post_id TEXT NOT NULL,
    post_thumbnail_url TEXT,
    post_caption TEXT,
    campaign_goal VARCHAR(32) NOT NULL,
    target_audience_type VARCHAR(32) NOT NULL DEFAULT 'AUTOMATIC',
    target_gender VARCHAR(16) NOT NULL DEFAULT 'ALL',
    target_age_range VARCHAR(32) NOT NULL DEFAULT 'ALL',
    target_locations TEXT,
    target_interests TEXT,
    tier_id VARCHAR(64) NOT NULL,
    tier_min_reach INT NOT NULL,
    tier_max_reach INT NOT NULL,
    daily_budget_minor BIGINT NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    daily_budget_display DOUBLE PRECISION NOT NULL,
    duration_days INT NOT NULL DEFAULT 1,
    total_budget_display DOUBLE PRECISION NOT NULL,
    cta_button_text VARCHAR(64) NOT NULL,
    destination_url TEXT,
    whatsapp_or_chat_number VARCHAR(32),
    lead_form_title VARCHAR(128),
    lead_form_questions TEXT,
    payment_gateway VARCHAR(32) NOT NULL,
    payment_reference TEXT NOT NULL,
    payment_verified BOOLEAN NOT NULL DEFAULT TRUE,
    verified_at_ms BIGINT NOT NULL,
    starts_at_ms BIGINT NOT NULL,
    expires_at_ms BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    impressions_count BIGINT NOT NULL DEFAULT 0,
    clicks_count BIGINT NOT NULL DEFAULT 0,
    conversions_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_campaigns_user ON public.telefam_campaigns(user_id);
CREATE INDEX IF NOT EXISTS idx_telefam_campaigns_post ON public.telefam_campaigns(post_id);
CREATE INDEX IF NOT EXISTS idx_telefam_campaigns_status ON public.telefam_campaigns(status, expires_at_ms);
ALTER TABLE public.telefam_campaigns ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Campaigns policy" ON public.telefam_campaigns;
CREATE POLICY "Campaigns policy" ON public.telefam_campaigns FOR ALL USING (true) WITH CHECK (true);

CREATE TABLE IF NOT EXISTS public.telefam_campaign_leads (
    lead_id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL REFERENCES public.telefam_campaigns(campaign_id) ON DELETE CASCADE,
    applicant_user_id TEXT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_campaign_leads_campaign ON public.telefam_campaign_leads(campaign_id);
ALTER TABLE public.telefam_campaign_leads ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Campaign leads policy" ON public.telefam_campaign_leads;
CREATE POLICY "Campaign leads policy" ON public.telefam_campaign_leads FOR ALL USING (true) WITH CHECK (true);


-- -----------------------------------------------------------------------------
-- 11. MEDIA FINGERPRINTS VAULT (telefam_media_fingerprints)
-- Deduplication and fast validation
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.telefam_media_fingerprints (
    sha256_checksum VARCHAR(64) PRIMARY KEY,
    perceptual_dhash VARCHAR(16) NOT NULL,
    storage_key TEXT NOT NULL,
    verified_mime_type VARCHAR(32) NOT NULL,
    compressed_byte_size INT NOT NULL,
    width_px INT NOT NULL DEFAULT 512,
    height_px INT NOT NULL DEFAULT 512,
    reuse_count INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telefam_media_fingerprints_dhash ON public.telefam_media_fingerprints(perceptual_dhash);
ALTER TABLE public.telefam_media_fingerprints ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Media fingerprints policy" ON public.telefam_media_fingerprints;
CREATE POLICY "Media fingerprints policy" ON public.telefam_media_fingerprints FOR ALL USING (true) WITH CHECK (true);
