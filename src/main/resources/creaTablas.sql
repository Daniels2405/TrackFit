/*
  Script de creación de base de datos para TrackFit
  Sistema web transaccional para la gestión de gimnasios.
  SC-403 Desarrollo de Aplicaciones Web y Patrones - Grupo 6

  Este script crea el esquema, las tablas, los usuarios de base de datos
  y carga datos ficticios de demostración.
  Requiere MySQL 8.0.16 o superior (para que se apliquen los CHECK).

  USUARIOS DE PRUEBA DE LA APLICACIÓN (ficticios)
  ------------------------------------------------
  Rol            username        contraseña
  ADMINISTRADOR  admin           Admin2026*
  RECEPCION      recepcion       Recepcion2026*
  ENTRENADOR     maria.solano    Entrenador2026*
  ENTRENADOR     andres.campos   Entrenador2026*
  CLIENTE        carlos.rodriguez (y los demás clientes)  Cliente2026*
*/

-- Sección de administración (ejecutar una vez en un entorno de desarrollo)
drop database if exists trackfit;
drop user if exists trackfit_app;
drop user if exists trackfit_reportes;

-- Creación del esquema
CREATE database trackfit
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación de usuarios de base de datos (contraseñas de desarrollo; en otros entornos usar variables de entorno)
create user 'trackfit_app'@'%' identified by 'Tr4ckF1t_App.';
create user 'trackfit_reportes'@'%' identified by 'Tr4ckF1t_Rep.';

-- Asignación de permisos
grant select, insert, update, delete on trackfit.* to 'trackfit_app'@'%';
grant select on trackfit.* to 'trackfit_reportes'@'%';
flush privileges;

use trackfit;

-- --- Sección de Creación de Tablas ---

-- ========== MÓDULO DE SEGURIDAD ==========

-- Tabla de roles
create table rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(25) NOT NULL unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol))
  ENGINE = InnoDB;

-- Tabla de usuarios (administradores, recepción, entrenadores y clientes).
-- El tipo de usuario se define por su rol en usuario_rol.
-- especialidad solo se llena cuando el usuario tiene el rol ENTRENADOR (se valida en el service).
CREATE TABLE usuario (
  cedula VARCHAR(20) NOT NULL,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(30) NOT NULL,
  apellido1 VARCHAR(30) NOT NULL,
  apellido2 VARCHAR(30) NULL,
  correo VARCHAR(75) NOT NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  especialidad VARCHAR(60) NULL,
  ruta_imagen_perfil VARCHAR(1024) NULL,
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (cedula),
  index ndx_username (username))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
create table usuario_rol (
  cedula VARCHAR(20) NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (cedula, id_rol),
  foreign key fk_usuarioRol_usuario (cedula) references usuario(cedula),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol))
  ENGINE = InnoDB;

-- ========== MÓDULO DE MEMBRESÍAS Y PAGOS ==========

-- Tabla de planes de membresía (Standard, Premium, VIP)
create table plan_membresia (
  id_plan INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL,
  precio DECIMAL(12,2) NOT NULL CHECK (precio >= 0),
  duracion_dias INT UNSIGNED NOT NULL CHECK (duracion_dias > 0),
  anticipacion_reserva_dias INT UNSIGNED NOT NULL CHECK (anticipacion_reserva_dias >= 0),
  ruta_imagen VARCHAR(1024) NULL,
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_plan),
  unique (nombre))
  ENGINE = InnoDB;

-- Tabla de beneficios de cada plan (un beneficio por fila)
create table plan_beneficio (
  id_beneficio INT NOT NULL AUTO_INCREMENT,
  id_plan INT NOT NULL,
  descripcion VARCHAR(100) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_beneficio),
  index ndx_beneficio_plan (id_plan),
  UNIQUE (id_plan, descripcion),
  foreign key fk_beneficio_plan (id_plan) references plan_membresia(id_plan))
  ENGINE = InnoDB;

-- Tabla de membresías asignadas a clientes.
-- El estado Vigente/Vencida NO se guarda: se calcula comparando fecha_vencimiento con la fecha actual.
-- fecha_vencimiento se guarda para conservar el histórico aunque el plan cambie su duración.
create table membresia (
  id_membresia INT NOT NULL AUTO_INCREMENT,
  cedula_cliente VARCHAR(20) NOT NULL,
  id_plan INT NOT NULL,
  fecha_inicio DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  estado ENUM('Activa', 'Cancelada') NOT NULL DEFAULT 'Activa',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_membresia),
  CHECK (fecha_vencimiento >= fecha_inicio),
  index ndx_membresia_cliente (cedula_cliente),
  index ndx_membresia_plan (id_plan),
  foreign key fk_membresia_cliente (cedula_cliente) references usuario(cedula),
  foreign key fk_membresia_plan (id_plan) references plan_membresia(id_plan))
  ENGINE = InnoDB;

-- Tabla de pagos de membresías (tabla transaccional)
create table pago (
  id_pago INT NOT NULL AUTO_INCREMENT,
  id_membresia INT NOT NULL,
  monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
  fecha DATE NOT NULL,
  metodo_pago ENUM('Efectivo', 'Tarjeta', 'SINPE', 'Transferencia') NOT NULL,
  cedula_registra VARCHAR(20) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pago),
  index ndx_pago_membresia (id_membresia),
  index ndx_pago_registra (cedula_registra),
  foreign key fk_pago_membresia (id_membresia) references membresia(id_membresia),
  foreign key fk_pago_registra (cedula_registra) references usuario(cedula))
  ENGINE = InnoDB;

-- ========== MÓDULO DE ACTIVIDADES Y RESERVAS ==========

-- Tabla de actividades (catálogo: Yoga, Spinning, etc.)
create table actividad (
  id_actividad INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(255) NULL,
  ruta_imagen VARCHAR(1024) NULL,
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_actividad),
  unique (nombre))
  ENGINE = InnoDB;

-- Tabla que indica qué planes pueden tomar cada actividad
create table actividad_plan (
  id_actividad INT NOT NULL,
  id_plan INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_actividad, id_plan),
  foreign key fk_actividadPlan_actividad (id_actividad) references actividad(id_actividad),
  foreign key fk_actividadPlan_plan (id_plan) references plan_membresia(id_plan))
  ENGINE = InnoDB;

-- Tabla de horarios (sesiones concretas de una actividad con su cupo máximo).
-- El cupo disponible NO se guarda: cupo_maximo menos las reservas Confirmadas.
create table horario (
  id_horario INT NOT NULL AUTO_INCREMENT,
  id_actividad INT NOT NULL,
  fecha DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  cupo_maximo INT UNSIGNED NOT NULL CHECK (cupo_maximo > 0),
  cedula_entrenador VARCHAR(20) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_horario),
  CHECK (hora_fin > hora_inicio),
  index ndx_horario_actividad (id_actividad),
  index ndx_horario_entrenador (cedula_entrenador),
  index ndx_horario_fecha (fecha),
  foreign key fk_horario_actividad (id_actividad) references actividad(id_actividad),
  foreign key fk_horario_entrenador (cedula_entrenador) references usuario(cedula))
  ENGINE = InnoDB;

-- Tabla de reservas (tabla transaccional)
create table reserva (
  id_reserva INT NOT NULL AUTO_INCREMENT,
  cedula_cliente VARCHAR(20) NOT NULL,
  id_horario INT NOT NULL,
  estado ENUM('Confirmada', 'Cancelada') NOT NULL DEFAULT 'Confirmada',
  fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_reserva),
  index ndx_reserva_cliente (cedula_cliente),
  index ndx_reserva_horario (id_horario),
  UNIQUE (cedula_cliente, id_horario),
  foreign key fk_reserva_cliente (cedula_cliente) references usuario(cedula),
  foreign key fk_reserva_horario (id_horario) references horario(id_horario))
  ENGINE = InnoDB;

-- ========== MÓDULO DE ENTRENAMIENTO (se implementa en el avance final) ==========

-- Tabla de ejercicios
create table ejercicio (
  id_ejercicio INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  descripcion VARCHAR(255) NULL,
  ruta_imagen VARCHAR(1024) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_ejercicio),
  unique (nombre))
  ENGINE = InnoDB;

-- Tabla de rutinas asignadas a clientes
create table rutina (
  id_rutina INT NOT NULL AUTO_INCREMENT,
  cedula_cliente VARCHAR(20) NOT NULL,
  cedula_entrenador VARCHAR(20) NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  observaciones VARCHAR(500) NULL,
  ruta_imagen VARCHAR(1024) NULL,
  activa boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rutina),
  index ndx_rutina_cliente (cedula_cliente),
  index ndx_rutina_entrenador (cedula_entrenador),
  foreign key fk_rutina_cliente (cedula_cliente) references usuario(cedula),
  foreign key fk_rutina_entrenador (cedula_entrenador) references usuario(cedula))
  ENGINE = InnoDB;

-- Tabla de ejercicios de cada rutina
create table rutina_ejercicio (
  id_rutina_ejercicio INT NOT NULL AUTO_INCREMENT,
  id_rutina INT NOT NULL,
  id_ejercicio INT NOT NULL,
  series INT UNSIGNED NOT NULL CHECK (series > 0),
  repeticiones INT UNSIGNED NOT NULL CHECK (repeticiones > 0),
  peso DECIMAL(6,2) NULL CHECK (peso IS NULL OR peso >= 0),
  observaciones VARCHAR(255) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rutina_ejercicio),
  UNIQUE (id_rutina, id_ejercicio),
  index ndx_rutinaEjercicio_ejercicio (id_ejercicio),
  foreign key fk_rutinaEjercicio_rutina (id_rutina) references rutina(id_rutina),
  foreign key fk_rutinaEjercicio_ejercicio (id_ejercicio) references ejercicio(id_ejercicio))
  ENGINE = InnoDB;

-- Tabla de progreso físico de los clientes
create table progreso (
  id_progreso INT NOT NULL AUTO_INCREMENT,
  cedula_cliente VARCHAR(20) NOT NULL,
  cedula_entrenador VARCHAR(20) NOT NULL,
  fecha DATE NOT NULL,
  peso DECIMAL(5,2) NOT NULL CHECK (peso > 0),
  porcentaje_grasa DECIMAL(4,2) NULL CHECK (porcentaje_grasa IS NULL OR (porcentaje_grasa >= 0 AND porcentaje_grasa <= 100)),
  observaciones VARCHAR(500) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_progreso),
  index ndx_progreso_cliente (cedula_cliente),
  index ndx_progreso_entrenador (cedula_entrenador),
  foreign key fk_progreso_cliente (cedula_cliente) references usuario(cedula),
  foreign key fk_progreso_entrenador (cedula_entrenador) references usuario(cedula))
  ENGINE = InnoDB;

-- ========== MÓDULO DE MARKETING Y DISEÑO WEB ==========
-- [IDEA EN DESARROLLO PARA DISEÑO DE LA PÁGINA]
-- Tabla opcional para la gestión dinámica de banners y promociones que se muestran
-- en el index o secciones comerciales (carruseles de ofertas, avisos de temporada, etc.).
create table promocion (
  id_promocion INT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  ruta_imagen VARCHAR(1024) NOT NULL,
  enlace_destino VARCHAR(255) NULL,
  fecha_inicio DATE NULL,
  fecha_fin DATE NULL,
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_promocion)
) ENGINE = InnoDB;

-- --- Sección de Inserción de Datos (todos ficticios) ---

-- Inserción de roles
insert into rol (rol) values ('ADMINISTRADOR'), ('RECEPCION'), ('ENTRENADOR'), ('CLIENTE');

-- Inserción de usuarios: personal del gimnasio
-- Contraseñas (BCrypt): admin = Admin2026*, recepcion = Recepcion2026*, entrenadores = Entrenador2026*
INSERT INTO usuario (cedula, username, password, nombre, apellido1, apellido2, correo, telefono, especialidad, activo) VALUES
('100000001','admin','$2b$12$OG5pTMfVrINAnTAldRl/2ewa6j655Rt6tC8J2BsRs07a6LQwvd4/.','Sofía','Navarro','Blanco','admin@trackfit.test','2222-0001',NULL,true),
('100000002','recepcion','$2b$12$1vGqitme/J9.Fki9IiSpm.cDsif/ok7nT4fln.gNA59suKgtQDSKa','Laura','Méndez','Ulate','recepcion@trackfit.test','2222-0002',NULL,true),
('100000003','maria.solano','$2b$12$JXtUiVlA2LSX.yySjxh0W.uopYGYap9BPO6MHK6TBi9bQl6.CAFbi','María','Solano','Vindas','maria.solano@trackfit.test','2222-0003','Yoga y movilidad',true),
('100000004','andres.campos','$2b$12$JXtUiVlA2LSX.yySjxh0W.uopYGYap9BPO6MHK6TBi9bQl6.CAFbi','Andrés','Campos','Rivera','andres.campos@trackfit.test','2222-0004','Fuerza y acondicionamiento',true);

-- Inserción de usuarios: clientes (contraseña: Cliente2026*)
INSERT INTO usuario (cedula, username, password, nombre, apellido1, apellido2, correo, telefono, activo) VALUES
('200000001','carlos.rodriguez','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Carlos','Rodríguez','Mora','carlos.rodriguez@trackfit.test','8800-0001',true),
('200000002','maria.gonzalez','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','María','González','Soto','maria.gonzalez@trackfit.test','8800-0002',true),
('200000003','jose.vargas','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','José','Vargas','Araya','jose.vargas@trackfit.test','8800-0003',true),
('200000004','daniela.jimenez','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Daniela','Jiménez','Cordero','daniela.jimenez@trackfit.test','8800-0004',true),
('200000005','luis.chaves','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Luis Fernando','Chaves','Solís','luis.chaves@trackfit.test','8800-0005',true),
('200000006','andrea.salazar','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Andrea','Salazar','Quesada','andrea.salazar@trackfit.test','8800-0006',true),
('200000007','kevin.ramirez','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Kevin','Ramírez','Brenes','kevin.ramirez@trackfit.test','8800-0007',true),
('200000008','paola.hernandez','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Paola','Hernández','Vega','paola.hernandez@trackfit.test','8800-0008',true),
('200000009','esteban.mora','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Esteban','Mora','Castro','esteban.mora@trackfit.test','8800-0009',true),
('200000010','sofia.acuna','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Sofía','Acuña','Rojas','sofia.acuna@trackfit.test','8800-0010',true),
('200000011','mauricio.alfaro','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Mauricio','Alfaro','Ureña','mauricio.alfaro@trackfit.test','8800-0011',true),
('200000012','valeria.barrantes','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Valeria','Barrantes','Núñez','valeria.barrantes@trackfit.test','8800-0012',true),
('200000013','diego.zuniga','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Diego','Zúñiga','Porras','diego.zuniga@trackfit.test','8800-0013',true),
('200000014','camila.leiva','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Camila','Leiva','Picado','camila.leiva@trackfit.test','8800-0014',true),
('200000015','gabriel.sandi','$2b$12$4Q7KNT0.L5x3VAcl5Y4tDeW0fr0AYDGudfWs79KKMQTVL/yMPPZDm','Gabriel','Sandí','Fonseca','gabriel.sandi@trackfit.test','8800-0015',true);

-- Asignación de roles a usuarios
insert into usuario_rol (cedula, id_rol) values
('100000001', 1),
('100000002', 2),
('100000003', 3),
('100000004', 3);
insert into usuario_rol (cedula, id_rol)
  select cedula, 4 from usuario where cedula like '2%';

-- Inserción de planes de membresía
INSERT INTO plan_membresia (nombre, precio, duracion_dias, anticipacion_reserva_dias, activo) VALUES
('Standard', 25000, 30, 2, true),
('Premium',  35000, 30, 5, true),
('VIP',      50000, 30, 7, true);

-- Inserción de beneficios de los planes
INSERT INTO plan_beneficio (id_plan, descripcion) VALUES
(1, 'Acceso completo al gimnasio'),
(1, 'Clases grupales limitadas'),
(1, 'Reservas con 2 días de anticipación'),
(2, 'Acceso completo al gimnasio'),
(2, 'Clases grupales ampliadas (Spinning y Pilates)'),
(2, 'Rutinas personalizadas'),
(2, 'Reservas con 5 días de anticipación'),
(3, 'Acceso completo al gimnasio'),
(3, 'Todas las clases grupales'),
(3, 'Asesoría personalizada'),
(3, 'Seguimiento de progreso e historial'),
(3, 'Reservas con 7 días de anticipación');

-- Inserción de membresías vigentes (las fechas son relativas a la fecha en que se ejecuta el script)
-- Standard: 3, 9, 10 | Premium: 1, 4, 5, 6, 7, 8 | VIP: 2, 11, 12, 13, 14, 15
-- La fecha de vencimiento se calcula con la duración del plan; el cliente 3 (José Vargas) queda vencida.
INSERT INTO membresia (cedula_cliente, id_plan, fecha_inicio, fecha_vencimiento, estado)
SELECT t.cedula, t.id_plan, t.inicio, DATE_ADD(t.inicio, INTERVAL p.duracion_dias DAY), 'Activa'
FROM (
  SELECT '200000001' AS cedula, 2 AS id_plan, DATE_SUB(CURDATE(), INTERVAL 5 DAY)  AS inicio
  UNION ALL SELECT '200000002', 3, DATE_SUB(CURDATE(), INTERVAL 12 DAY)
  UNION ALL SELECT '200000003', 1, DATE_SUB(CURDATE(), INTERVAL 45 DAY)
  UNION ALL SELECT '200000004', 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY)
  UNION ALL SELECT '200000005', 2, DATE_SUB(CURDATE(), INTERVAL 20 DAY)
  UNION ALL SELECT '200000006', 2, DATE_SUB(CURDATE(), INTERVAL 8 DAY)
  UNION ALL SELECT '200000007', 2, DATE_SUB(CURDATE(), INTERVAL 15 DAY)
  UNION ALL SELECT '200000008', 2, DATE_SUB(CURDATE(), INTERVAL 10 DAY)
  UNION ALL SELECT '200000009', 1, DATE_SUB(CURDATE(), INTERVAL 7 DAY)
  UNION ALL SELECT '200000010', 1, DATE_SUB(CURDATE(), INTERVAL 18 DAY)
  UNION ALL SELECT '200000011', 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY)
  UNION ALL SELECT '200000012', 3, DATE_SUB(CURDATE(), INTERVAL 22 DAY)
  UNION ALL SELECT '200000013', 3, DATE_SUB(CURDATE(), INTERVAL 9 DAY)
  UNION ALL SELECT '200000014', 3, DATE_SUB(CURDATE(), INTERVAL 14 DAY)
  UNION ALL SELECT '200000015', 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY)
) t JOIN plan_membresia p ON p.id_plan = t.id_plan;

-- Membresía anterior (ya vencida) de Carlos Rodríguez, para probar el historial de pagos
INSERT INTO membresia (cedula_cliente, id_plan, fecha_inicio, fecha_vencimiento, estado)
SELECT '200000001', 1, DATE_SUB(CURDATE(), INTERVAL 65 DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 65 DAY), INTERVAL duracion_dias DAY), 'Activa'
FROM plan_membresia WHERE id_plan = 1;

-- Inserción de pagos: uno por membresía, con el precio del plan y registrado por Recepción
INSERT INTO pago (id_membresia, monto, fecha, metodo_pago, cedula_registra)
SELECT m.id_membresia, p.precio, m.fecha_inicio,
       ELT(1 + MOD(m.id_membresia, 4), 'Efectivo', 'Tarjeta', 'SINPE', 'Transferencia'),
       '100000002'
FROM membresia m JOIN plan_membresia p ON p.id_plan = m.id_plan;

-- Inserción de actividades
INSERT INTO actividad (nombre, descripcion, activo) VALUES
('Yoga',     'Clase de yoga para flexibilidad, equilibrio y respiración.', true),
('Spinning', 'Ciclismo indoor de alta intensidad en grupo.', true),
('Funcional','Entrenamiento funcional con ejercicios de cuerpo completo.', true),
('Pilates',  'Fortalecimiento del core y control postural.', true),
('Boxeo',    'Boxeo recreativo para acondicionamiento y coordinación (exclusivo VIP).', true);

-- Planes que pueden tomar cada actividad (1=Standard, 2=Premium, 3=VIP)
INSERT INTO actividad_plan (id_actividad, id_plan) VALUES
(1,1),(1,2),(1,3),
(2,2),(2,3),
(3,1),(3,2),(3,3),
(4,2),(4,3),
(5,3);

-- Inserción de horarios (fechas relativas a hoy)
-- 1 Yoga (cupo 15, 8 reservas) | 2 Spinning (cupo 20, 12 reservas) | 3 Funcional (cupo 14, lleno)
-- 4 Pilates | 5 Yoga dentro de 6 días (solo reservable por VIP) | 6 Boxeo (solo VIP)
INSERT INTO horario (id_actividad, fecha, hora_inicio, hora_fin, cupo_maximo, cedula_entrenador) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00', '19:00:00', 15, '100000003'),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:00:00', '20:00:00', 20, '100000004'),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:00:00', '21:00:00', 14, '100000004'),
(4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '17:00:00', '18:00:00', 12, '100000003'),
(1, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '18:00:00', '19:00:00', 15, '100000003'),
(5, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '18:30:00', '19:30:00', 10, '100000004');

-- Inserción de reservas
-- Yoga (horario 1): 8 reservas confirmadas
INSERT INTO reserva (cedula_cliente, id_horario, estado) VALUES
('200000001',1,'Confirmada'),('200000002',1,'Confirmada'),('200000004',1,'Confirmada'),('200000005',1,'Confirmada'),
('200000006',1,'Confirmada'),('200000009',1,'Confirmada'),('200000010',1,'Confirmada'),('200000011',1,'Confirmada');
-- Spinning (horario 2): 12 reservas confirmadas (solo Premium y VIP)
INSERT INTO reserva (cedula_cliente, id_horario, estado) VALUES
('200000001',2,'Confirmada'),('200000002',2,'Confirmada'),('200000004',2,'Confirmada'),('200000005',2,'Confirmada'),
('200000006',2,'Confirmada'),('200000007',2,'Confirmada'),('200000008',2,'Confirmada'),('200000011',2,'Confirmada'),
('200000012',2,'Confirmada'),('200000013',2,'Confirmada'),('200000014',2,'Confirmada'),('200000015',2,'Confirmada');
-- Funcional (horario 3): cupo lleno, 14 de 14
INSERT INTO reserva (cedula_cliente, id_horario, estado) VALUES
('200000001',3,'Confirmada'),('200000002',3,'Confirmada'),('200000004',3,'Confirmada'),('200000005',3,'Confirmada'),
('200000006',3,'Confirmada'),('200000007',3,'Confirmada'),('200000008',3,'Confirmada'),('200000009',3,'Confirmada'),
('200000010',3,'Confirmada'),('200000011',3,'Confirmada'),('200000012',3,'Confirmada'),('200000013',3,'Confirmada'),
('200000014',3,'Confirmada'),('200000015',3,'Confirmada');
-- Pilates (horario 4): 2 confirmadas y 1 cancelada
INSERT INTO reserva (cedula_cliente, id_horario, estado) VALUES
('200000001',4,'Confirmada'),('200000002',4,'Confirmada'),('200000004',4,'Cancelada');
-- Boxeo (horario 6): 1 reserva de un cliente VIP
INSERT INTO reserva (cedula_cliente, id_horario, estado) VALUES
('200000002',6,'Confirmada');

-- Inserción de ejercicios
INSERT INTO ejercicio (nombre, descripcion) VALUES
('Press banca',      'Empuje horizontal con barra para pecho, hombros y tríceps.'),
('Remo con barra',   'Tirón horizontal para espalda y bíceps.'),
('Curl de bíceps',   'Flexión de codo con mancuernas o barra.'),
('Tríceps en polea', 'Extensión de codo en polea alta.'),
('Sentadilla',       'Flexión de cadera y rodilla para piernas y glúteos.'),
('Peso muerto',      'Levantamiento de barra desde el suelo para cadena posterior.'),
('Plancha',          'Isométrico abdominal para fortalecer el core.'),
('Dominadas',        'Tirón vertical con el peso corporal.');

-- Inserción de una rutina de ejemplo para Carlos Rodríguez, creada por Andrés Campos
INSERT INTO rutina (cedula_cliente, cedula_entrenador, nombre, observaciones, activa) VALUES
('200000001', '100000004', 'Tren Superior', 'Descansar 60 segundos entre series y mantener la técnica.', true);

INSERT INTO rutina_ejercicio (id_rutina, id_ejercicio, series, repeticiones, peso, observaciones) VALUES
(1, 1, 4, 12, 40, 'Controlar la bajada'),
(1, 2, 3, 10, 35, NULL),
(1, 3, 3, 12, 12, NULL),
(1, 4, 3, 12, 20, NULL);

-- Inserción de progreso de Carlos Rodríguez
INSERT INTO progreso (cedula_cliente, cedula_entrenador, fecha, peso, porcentaje_grasa, observaciones) VALUES
('200000001', '100000004', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 84.00, 21.50, 'Medición inicial'),
('200000001', '100000004', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 81.00, 19.50, 'Buen avance en el primer mes'),
('200000001', '100000004', DATE_SUB(CURDATE(), INTERVAL 3 DAY),  78.00, 18.00, 'Mantiene la constancia');

-- --- Consultas de verificación (opcionales) ---
-- Cupos por horario: cupo máximo, reservas confirmadas y disponibles
-- SELECT h.id_horario, a.nombre, h.fecha, h.hora_inicio, h.cupo_maximo,
--        COUNT(r.id_reserva) AS reservados, h.cupo_maximo - COUNT(r.id_reserva) AS disponibles
-- FROM horario h
-- JOIN actividad a ON a.id_actividad = h.id_actividad
-- LEFT JOIN reserva r ON r.id_horario = h.id_horario AND r.estado = 'Confirmada'
-- GROUP BY h.id_horario, a.nombre, h.fecha, h.hora_inicio, h.cupo_maximo;
--
-- Estado de la membresía de cada cliente (Vigente/Vencida se calcula)
-- SELECT u.nombre, u.apellido1, p.nombre AS plan, m.fecha_vencimiento,
--        IF(m.estado = 'Activa' AND m.fecha_vencimiento >= CURDATE(), 'Vigente', 'Vencida') AS estado_actual
-- FROM membresia m
-- JOIN usuario u ON u.cedula = m.cedula_cliente
-- JOIN plan_membresia p ON p.id_plan = m.id_plan;
