USE llajtazo;

-- =========================================================
-- LIMPIEZA (orden por FK)
-- =========================================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM tickets;
DELETE FROM carrito_items;
DELETE FROM carritos;
DELETE FROM zonas;

-- si no quieres tocar events/organizadores/usuarios, comenta estos 3:
DELETE FROM events;
DELETE FROM organizadores;
DELETE FROM usuarios;

-- opcional: resetear autoincrement (si tu tabla usa AI)
ALTER TABLE tickets AUTO_INCREMENT = 1;
ALTER TABLE carrito_items AUTO_INCREMENT = 1;
ALTER TABLE carritos AUTO_INCREMENT = 1;
ALTER TABLE zonas AUTO_INCREMENT = 1;
ALTER TABLE events AUTO_INCREMENT = 1;
ALTER TABLE usuarios AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- SEED MÍNIMO: usuarios (asistente + organizador)
-- =========================================================
-- OJO: tu tabla usuarios tiene columnas duplicadas/extra:
-- email, nombre_completo, password_hash, avatar_url, created_at, correo, password, username
-- Para que no falle, insertamos TODAS las NOT NULL.

INSERT INTO usuarios (
  id,
  email,
  nombre_completo,
  password_hash,
  avatar_url,
  created_at,
  correo,
  password,
  username
) VALUES
-- Asistente (id=1)
(
  1,
  'asistente@test.com',
  'Asistente Prueba',
  'hash_dummy',
  NULL,
  NOW(),
  'asistente@test.com',
  'pass_dummy',
  'asistente1'
),
-- Organizador (id=2) (también es usuario en tu modelo)
(
  2,
  'organizador@test.com',
  'Organizador Prueba',
  'hash_dummy',
  NULL,
  NOW(),
  'organizador@test.com',
  'pass_dummy',
  'organizador1'
);

-- =========================================================
-- organizadores (id = usuario.id)
-- =========================================================
-- Tu tabla organizadores tiene columnas extra NOT NULL: created_at
INSERT INTO organizadores (
  id,
  about,
  followers,
  suscribed,
  created_at,
  correo,
  password,
  profile_pic,
  username
) VALUES (
  2,
  'Organizador de prueba para tests',
  0,
  b'1',
  NOW(),
  'organizador@test.com',
  'pass_dummy',
  NULL,
  'organizador1'
);

-- =========================================================
-- events: creamos 2 eventos
-- =========================================================
-- events requiere organizadores_id NOT NULL
-- Nota: en tu tabla hay organizadores_id y también organizador_id (nullable)
INSERT INTO events (
  id,
  lugar_id,
  categoria_id,
  organizadores_id,
  titulo,
  descripcion,
  start_time,
  end_time,
  cover_url,
  estado,
  fecha_creacion,
  tipo_evento,
  organizador_id
) VALUES
(
  1,
  NULL,
  NULL,
  2,
  'Evento Test 1',
  'Evento para pruebas de carrito/checkout',
  NOW(),
  DATE_ADD(NOW(), INTERVAL 2 HOUR),
  NULL,
  'PUBLISHED',
  NOW(),
  'PR',
  2
),
(
  2,
  NULL,
  NULL,
  2,
  'Evento Test 2',
  'Segundo evento para validar zona de otro evento',
  NOW(),
  DATE_ADD(NOW(), INTERVAL 3 HOUR),
  NULL,
  'PUBLISHED',
  NOW(),
  'PR',
  2
);

-- =========================================================
-- zonas: creamos 2 zonas (una por evento)
-- =========================================================
-- Tu tabla zonas tiene moneda y currency (duplicado), ambas NOT NULL.
-- Insertamos ambas iguales.
INSERT INTO zonas (
  id,
  evento_id,
  nombre,
  precio,
  moneda,
  cantidad_tickets,
  activo,
  currency
) VALUES
(
  1,
  1,
  'VIP',
  100.0,
  'BOB',
  50,
  1,
  'BOB'
),
(
  2,
  2,
  'GENERAL',
  60.0,
  'BOB',
  30,
  1,
  'BOB'
);

-- =========================================================
-- carritos: 1 carrito ABIERTO para el asistente en evento 1
-- =========================================================
-- Tu tabla carritos tiene usuario_id y asistente_id (ambos FK a usuarios.id)
-- Para evitar problemas, ponemos ambos = 1
INSERT INTO carritos (
  id,
  usuario_id,
  evento_id,
  estado,
  creado_en,
  actualizado_en,
  created_at,
  updated_at,
  asistente_id
) VALUES (
  1,
  1,
  1,
  'ABIERTO',
  NOW(),
  NOW(),
  NOW(6),
  NOW(6),
  1
);

-- =========================================================
-- carrito_items: dejalo VACÍO por defecto
-- (los tests lo llenan vía service)
-- =========================================================

-- =========================================================
-- tickets: dejamos VACÍO por defecto
-- (checkout creará tickets)
-- =========================================================
