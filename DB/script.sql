-- =========================================================
-- EXTENSIONES Y TIPOS
-- =========================================================

-- Permite generar UUIDs seguros (gen_random_uuid)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Estados posibles de un email
CREATE TYPE email_status AS ENUM ('DRAFT', 'SENT', 'FAILED', 'QUEUED');

-- Tipo de destinatario
CREATE TYPE recipient_type AS ENUM ('TO', 'CC', 'BCC', 'SELF');

-- Tipo de contenido del email
CREATE TYPE body_type_enum AS ENUM ('HTML', 'PLAIN');


-- =========================================================
-- USERS
-- =========================================================
-- Representa los usuarios del sistema
CREATE TABLE users (
    id_user UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- ID único
    email VARCHAR(255) NOT NULL UNIQUE,                -- Email único
    full_name VARCHAR(150) NOT NULL,                   -- Nombre completo
    password VARCHAR(255) NOT NULL,                    -- Hash (bcrypt)
    avatar_url VARCHAR(500),                           -- Imagen de perfil
    is_active BOOLEAN NOT NULL DEFAULT TRUE,           -- Estado del usuario

    -- Contador denormalizado para performance (no calcular siempre COUNT)
    unread_count INT DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Trigger para mantener updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_updated_at();


-- =========================================================
-- LABELS (equivalente a carpetas tipo Gmail)
-- =========================================================
-- Cada usuario tiene sus propias etiquetas
CREATE TABLE labels (
    id_label UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL, -- dueño de la etiqueta

    name VARCHAR(100) NOT NULL, -- nombre (INBOX, SENT, etc.)
    color VARCHAR(7),           -- color hex (#FFFFFF)

    is_system BOOLEAN NOT NULL DEFAULT FALSE, -- labels del sistema

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Un usuario no puede tener labels duplicadas
    UNIQUE(user_id, name),

    FOREIGN KEY (user_id) REFERENCES users(id_user) ON DELETE CASCADE
);


-- =========================================================
-- THREADS (conversaciones)
-- =========================================================
-- Agrupa múltiples emails en una conversación
CREATE TABLE threads (
    id_thread UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Se usa para ordenar inbox por actividad reciente
    last_email_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- =========================================================
-- EMAILS (mensaje real)
-- =========================================================
-- Representa un correo único (no se duplica por usuario)
CREATE TABLE emails (
    id_email UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    thread_id UUID NOT NULL, -- pertenece a una conversación
    sender_id UUID NOT NULL, -- usuario que envía

    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,

    body_type body_type_enum NOT NULL DEFAULT 'HTML',

    -- Estado del envío
    status email_status NOT NULL DEFAULT 'DRAFT',

    sent_at TIMESTAMPTZ,     -- cuándo fue enviado
    deleted_at TIMESTAMPTZ,  -- soft delete global (opcional)

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    FOREIGN KEY (thread_id) REFERENCES threads(id_thread) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id_user)
);


-- =========================================================
-- EMAIL_RECIPIENTS (núcleo del sistema)
-- =========================================================
-- Define qué usuarios ven el email y en qué estado
CREATE TABLE email_recipients (
    id_recipient UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    email_id UUID NOT NULL,
    user_id UUID NOT NULL,

    -- Tipo de participación en el correo
    type recipient_type NOT NULL,

    -- Estados por usuario (cada usuario tiene su propia vista)
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_starred BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    is_trashed BOOLEAN NOT NULL DEFAULT FALSE,

    read_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Evita duplicados
    UNIQUE(email_id, user_id, type),

    FOREIGN KEY (email_id) REFERENCES emails(id_email) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id_user) ON DELETE CASCADE
);


-- =========================================================
-- ATTACHMENTS (adjuntos)
-- =========================================================
-- Solo guarda metadata (los archivos viven en storage externo)
CREATE TABLE attachments (
    id_attachment UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    email_id UUID NOT NULL,

    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL, -- tamaño en bytes
    mime_type VARCHAR(100) NOT NULL,

    -- URL hacia S3 / Cloudinary / etc.
    storage_url VARCHAR(500) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    FOREIGN KEY (email_id) REFERENCES emails(id_email) ON DELETE CASCADE
);


-- =========================================================
-- EMAIL_LABELS (relación muchos a muchos)
-- =========================================================
-- Permite que un email tenga múltiples etiquetas por usuario
CREATE TABLE email_labels (
    email_id UUID NOT NULL,
    label_id UUID NOT NULL,
    user_id UUID NOT NULL,

    PRIMARY KEY (email_id, label_id, user_id),

    FOREIGN KEY (email_id) REFERENCES emails(id_email) ON DELETE CASCADE,

    -- Asegura que el label pertenezca al usuario correcto
    FOREIGN KEY (label_id, user_id)
        REFERENCES labels(id_label, user_id) ON DELETE CASCADE
);


-- =========================================================
-- ÍNDICES (performance)
-- =========================================================

-- Inbox: búsqueda principal (optimizada)
CREATE INDEX idx_inbox
ON email_recipients(user_id, is_trashed, is_archived, created_at DESC);

-- Emails no leídos
CREATE INDEX idx_unread
ON email_recipients(user_id)
WHERE is_read = false;

-- Ordenar conversaciones por actividad
CREATE INDEX idx_threads_activity
ON threads(last_email_at DESC);

-- Emails dentro de un thread
CREATE INDEX idx_emails_thread
ON emails(thread_id, sent_at DESC);

-- Búsqueda por sender y estado
CREATE INDEX idx_sender_status
ON emails(sender_id, status);

-- Full-text search (búsqueda tipo Gmail)
CREATE INDEX idx_email_search
ON emails USING gin(to_tsvector('spanish', subject || ' ' || body));


-- =========================================================
-- TRIGGERS DE NEGOCIO
-- =========================================================

-- Actualiza automáticamente la actividad del thread
CREATE OR REPLACE FUNCTION update_thread_activity()
RETURNS TRIGGER AS $$
BEGIN
   UPDATE threads
   SET last_email_at = NOW()
   WHERE id_thread = NEW.thread_id;

   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_thread_activity
AFTER INSERT ON emails
FOR EACH ROW
EXECUTE FUNCTION update_thread_activity();