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
