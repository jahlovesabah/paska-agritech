-- ============================================================
-- PASKA AGRITECH - SCRIPT POSTGRESQL (Neon)
-- La app crea este esquema automaticamente al arrancar, pero
-- puedes ejecutarlo manualmente en Neon si lo prefieres.
-- ============================================================

-- =================================================================
-- ESQUEMA POSTGRESQL - PASKA AGRITECH (compatible con Neon)
-- Se ejecuta automaticamente al arrancar (idempotente).
-- =================================================================

-- ===================== FASE 1: OLTP ==============================
CREATE TABLE IF NOT EXISTS Agricultor (
    IdAgricultor SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    DNI CHAR(8) NOT NULL UNIQUE,
    Telefono VARCHAR(15),
    Correo VARCHAR(100),
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    hora_creacion TIME DEFAULT CURRENT_TIME,
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    ultimo_acceso TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Parcela (
    IdParcela SERIAL PRIMARY KEY,
    IdAgricultor INT NOT NULL REFERENCES Agricultor(IdAgricultor),
    Ubicacion VARCHAR(255) NOT NULL,
    Hectareas NUMERIC(5,2) NOT NULL,
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'Activo',
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Sensor (
    IdSensor SERIAL PRIMARY KEY,
    IdParcela INT NOT NULL REFERENCES Parcela(IdParcela),
    TipoSensor VARCHAR(50) NOT NULL,
    Bateria NUMERIC(5,2),
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'Activo',
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS PersonalPaska (
    IdPersonal SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    DNI CHAR(8) NOT NULL UNIQUE,
    CorreoInsti VARCHAR(100),
    Rol VARCHAR(50) NOT NULL,
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'Activo',
    ip_usuario VARCHAR(45)
);

CREATE TABLE IF NOT EXISTS Reporte (
    IdReporte SERIAL PRIMARY KEY,
    IdSensor INT NOT NULL REFERENCES Sensor(IdSensor),
    FechaSolicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TipoReporte VARCHAR(50) NOT NULL,
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'Activo',
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);

-- ===================== FASE 2: OLAP (Data Warehouse) =============
CREATE TABLE IF NOT EXISTS Dim_Agricultor (
    IdDimAgricultor SERIAL PRIMARY KEY,
    NombreCompleto VARCHAR(200),
    DNI CHAR(8),
    fuente_dato VARCHAR(50) DEFAULT 'OLTP_Paska',
    sistema_origen VARCHAR(50) DEFAULT 'PostgreSQL',
    id_origen INT NOT NULL,
    lote_carga INT DEFAULT 1,
    fecha_carga_dw TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_operacion VARCHAR(10) DEFAULT 'INSERT',
    vigente_desde TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vigente_hasta TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Dim_Parcela (
    IdDimParcela SERIAL PRIMARY KEY,
    Ubicacion VARCHAR(255),
    RangoHectareas VARCHAR(50),
    id_origen INT NOT NULL,
    fecha_carga_dw TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Dim_Sensor (
    IdDimSensor SERIAL PRIMARY KEY,
    TipoSensor VARCHAR(50),
    id_origen INT NOT NULL,
    fecha_carga_dw TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Hecho_Lectura_Sensores (
    IdHechoLectura SERIAL PRIMARY KEY,
    IdDimAgricultor INT NOT NULL REFERENCES Dim_Agricultor(IdDimAgricultor),
    IdDimParcela INT NOT NULL REFERENCES Dim_Parcela(IdDimParcela),
    IdDimSensor INT NOT NULL REFERENCES Dim_Sensor(IdDimSensor),
    HumedadRegistrada NUMERIC(5,2),
    TemperaturaRegistrada NUMERIC(5,2),
    ConsumoBateria NUMERIC(5,2),
    fecha_proceso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_carga INT DEFAULT 1
);

-- ===================== DATOS DE PRUEBA =====================
-- Datos de prueba (se insertan solo si la tabla Agricultor esta vacia).
INSERT INTO Agricultor (Nombre, Apellido, DNI, Telefono, Correo, ip_usuario, dispositivo) VALUES
('Carlos', 'Mendoza', '71234568', '987654321', 'cmendoza@agro.com', '192.168.1.10', 'App Movil');
INSERT INTO Agricultor (Nombre, Apellido, DNI, Telefono, Correo, ip_usuario, dispositivo) VALUES
('Lucia', 'Ramirez', '78541236', '912345678', 'lramirez@agro.com', '192.168.1.11', 'Web');

INSERT INTO Parcela (IdAgricultor, Ubicacion, Hectareas, ip_usuario) VALUES
(1, 'Valle de Ica - Sector Norte', 15.50, '192.168.1.10');
INSERT INTO Parcela (IdAgricultor, Ubicacion, Hectareas, ip_usuario) VALUES
(2, 'Valle del Mantaro - Lote A', 8.20, '192.168.1.11');

INSERT INTO Sensor (IdParcela, TipoSensor, Bateria, ip_usuario, dispositivo) VALUES
(1, 'Humedad del Suelo', 85.00, '10.0.0.5', 'Sensor_IoT_V1');
INSERT INTO Sensor (IdParcela, TipoSensor, Bateria, ip_usuario, dispositivo) VALUES
(1, 'Temperatura Ambiental', 92.50, '10.0.0.6', 'Sensor_IoT_V1');
INSERT INTO Sensor (IdParcela, TipoSensor, Bateria, ip_usuario, dispositivo) VALUES
(2, 'Humedad del Suelo', 45.00, '10.0.0.7', 'Sensor_IoT_V1');

INSERT INTO PersonalPaska (Nombre, Apellido, DNI, CorreoInsti, Rol, ip_usuario) VALUES
('Jorge', 'Salinas', '45871236', 'jsalinas@paska.com', 'Ing. Agronomo', '172.16.0.5');
INSERT INTO PersonalPaska (Nombre, Apellido, DNI, CorreoInsti, Rol, ip_usuario) VALUES
('Andrea', 'Vargas', '74125896', 'avargas@paska.com', 'Soporte Tecnico', '172.16.0.6');

INSERT INTO Reporte (IdSensor, TipoReporte, ip_usuario, dispositivo) VALUES
(3, 'Alerta: Bateria Critica (45%)', '10.0.0.7', 'API_Alerta');
INSERT INTO Reporte (IdSensor, TipoReporte, ip_usuario, dispositivo) VALUES
(1, 'Alerta: Estres Hidrico Detectado', '10.0.0.5', 'API_Alerta');

INSERT INTO Dim_Agricultor (NombreCompleto, DNI, id_origen) VALUES ('Carlos Mendoza', '71234568', 1);
INSERT INTO Dim_Agricultor (NombreCompleto, DNI, id_origen) VALUES ('Lucia Ramirez', '78541236', 2);
INSERT INTO Dim_Parcela (Ubicacion, RangoHectareas, id_origen) VALUES ('Valle de Ica - Sector Norte', 'Mediana (5-20)', 1);
INSERT INTO Dim_Sensor (TipoSensor, id_origen) VALUES ('Humedad del Suelo', 1);
INSERT INTO Hecho_Lectura_Sensores (IdDimAgricultor, IdDimParcela, IdDimSensor, HumedadRegistrada, TemperaturaRegistrada, ConsumoBateria) VALUES (1, 1, 1, 18.5, 26.2, 5.0);
