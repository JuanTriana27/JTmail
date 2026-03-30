-- Se crean usuarios
INSERT INTO users (email, full_name, password)
VALUES
('juan@test.com', 'Juan Triana', 'hash1'),
('ana@test.com', 'Ana Gomez', 'hash2')
RETURNING id_user;

-- Se crean labels
INSERT INTO labels (user_id, name, is_system)
VALUES
-- Juan
('485cd680-b977-435d-bdd3-b10e26958c3b', 'INBOX', TRUE),
('485cd680-b977-435d-bdd3-b10e26958c3b', 'SENT', TRUE),

-- Ana
('a5c055f3-6f28-4709-bcbb-fd837aef5370', 'INBOX', TRUE),
('a5c055f3-6f28-4709-bcbb-fd837aef5370', 'SENT', TRUE);

-- Se genera un threads que digamos es la llave para poder enviar el mail
INSERT INTO threads DEFAULT VALUES
RETURNING id_thread;

-- Se envia el mail
INSERT INTO emails (
    thread_id,
    sender_id,
    subject,
    body,
    status,
    sent_at
)
VALUES (
    '77c55265-270b-4320-8079-9fd3dfcf92ce',
    '485cd680-b977-435d-bdd3-b10e26958c3b',
    'Hola Ana',
    'Este es un correo de prueba',
    'SENT',
    NOW()
)
RETURNING id_email;

-- Ana recibe el correo con el mismo email_id y juan tambien lo tiene
INSERT INTO email_recipients (email_id, user_id, type)
VALUES ('89686393-86a0-4959-ab2e-685e1a9f9614', 'a5c055f3-6f28-4709-bcbb-fd837aef5370', 'TO');

-- Juan (sender) también lo tiene
INSERT INTO email_recipients (email_id, user_id, type, is_read)
VALUES ('89686393-86a0-4959-ab2e-685e1a9f9614', '485cd680-b977-435d-bdd3-b10e26958c3b', 'SELF', TRUE);

-- Se asignan los labels
-- INBOX de Ana
INSERT INTO email_labels (email_id, label_id, user_id)
SELECT '89686393-86a0-4959-ab2e-685e1a9f9614', id_label, user_id
FROM labels
WHERE user_id = 'a5c055f3-6f28-4709-bcbb-fd837aef5370' AND name = 'INBOX';

-- SENT de Juan
INSERT INTO email_labels (email_id, label_id, user_id)
SELECT '89686393-86a0-4959-ab2e-685e1a9f9614', id_label, user_id
FROM labels
WHERE user_id = '485cd680-b977-435d-bdd3-b10e26958c3b' AND name = 'SENT';

-- Probar el inbox:
SELECT e.subject, e.body, er.is_read, er.created_at
FROM email_recipients er
JOIN emails e ON e.id_email = er.email_id
WHERE er.user_id = 'a5c055f3-6f28-4709-bcbb-fd837aef5370'
  AND er.is_trashed = false
  AND er.is_archived = false
ORDER BY er.created_at DESC;

-- Marcar como leido
UPDATE email_recipients
SET is_read = true, read_at = NOW()
WHERE user_id = 'a5c055f3-6f28-4709-bcbb-fd837aef5370'
  AND email_id = '89686393-86a0-4959-ab2e-685e1a9f9614';

-- Mandar a la papelera
UPDATE email_recipients
SET is_trashed = true
WHERE user_id = 'a5c055f3-6f28-4709-bcbb-fd837aef5370'
  AND email_id = '89686393-86a0-4959-ab2e-685e1a9f9614';

-- Ver los no leidos:
SELECT COUNT(*)
FROM email_recipients
WHERE user_id = 'a5c055f3-6f28-4709-bcbb-fd837aef5370'
  AND is_read = false;