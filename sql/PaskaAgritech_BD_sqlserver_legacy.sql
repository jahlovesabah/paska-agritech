-- =================================================================
-- SCRIPT MAESTRO DEFINITIVO: PASKA AGRITECH (SQL SERVER)
-- =================================================================
USE master;
GO

-- 1. "BOTON DE REINICIO": Borra la BD limpiando conexiones activas
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'PaskaAgritech_DB')
BEGIN
    ALTER DATABASE PaskaAgritech_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE PaskaAgritech_DB;
END
GO

-- 2. CREACION DE LA BASE DE DATOS
CREATE DATABASE PaskaAgritech_DB;
GO
USE PaskaAgritech_DB;
GO

-- =================================================================
-- FASE 1: MODELO RELACIONAL TRANSACCIONAL (OLTP)
-- =================================================================
CREATE TABLE Agricultor (
    IdAgricultor INT PRIMARY KEY IDENTITY(1,1),
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    DNI CHAR(8) NOT NULL UNIQUE,
    Telefono VARCHAR(15),
    Correo VARCHAR(100),
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT GETDATE(),
    hora_creacion TIME DEFAULT CAST(GETDATE() AS TIME),
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    usuario_eliminacion VARCHAR(50),
    fecha_eliminacion DATE,
    hora_eliminacion TIME,
    ultimo_acceso DATETIME,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);
GO

CREATE TABLE Parcela (
    IdParcela INT PRIMARY KEY IDENTITY(1,1),
    IdAgricultor INT NOT NULL,
    Ubicacion VARCHAR(255) NOT NULL,
    Hectareas DECIMAL(5,2) NOT NULL,
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT GETDATE(),
    hora_creacion TIME DEFAULT CAST(GETDATE() AS TIME),
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    usuario_eliminacion VARCHAR(50),
    fecha_eliminacion DATE,
    hora_eliminacion TIME,
    ultimo_acceso DATETIME,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50),
    FOREIGN KEY (IdAgricultor) REFERENCES Agricultor(IdAgricultor)
);
GO

CREATE TABLE Sensor (
    IdSensor INT PRIMARY KEY IDENTITY(1,1),
    IdParcela INT NOT NULL,
    TipoSensor VARCHAR(50) NOT NULL,
    Bateria DECIMAL(5,2),
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT GETDATE(),
    hora_creacion TIME DEFAULT CAST(GETDATE() AS TIME),
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    usuario_eliminacion VARCHAR(50),
    fecha_eliminacion DATE,
    hora_eliminacion TIME,
    ultimo_acceso DATETIME,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50),
    FOREIGN KEY (IdParcela) REFERENCES Parcela(IdParcela)
);
GO

CREATE TABLE PersonalPaska (
    IdPersonal INT PRIMARY KEY IDENTITY(1,1),
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    DNI CHAR(8) NOT NULL UNIQUE,
    CorreoInsti VARCHAR(100),
    Rol VARCHAR(50) NOT NULL,
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT GETDATE(),
    hora_creacion TIME DEFAULT CAST(GETDATE() AS TIME),
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    usuario_eliminacion VARCHAR(50),
    fecha_eliminacion DATE,
    hora_eliminacion TIME,
    ultimo_acceso DATETIME,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50)
);
GO

CREATE TABLE Reporte (
    IdReporte INT PRIMARY KEY IDENTITY(1,1),
    IdSensor INT NOT NULL,
    FechaSolicitud DATETIME NOT NULL DEFAULT GETDATE(),
    TipoReporte VARCHAR(50) NOT NULL,
    usuario_creacion VARCHAR(50) DEFAULT 'admin',
    fecha_creacion DATE DEFAULT GETDATE(),
    hora_creacion TIME DEFAULT CAST(GETDATE() AS TIME),
    usuario_modificacion VARCHAR(50),
    fecha_modificacion DATE,
    hora_modificacion TIME,
    usuario_eliminacion VARCHAR(50),
    fecha_eliminacion DATE,
    hora_eliminacion TIME,
    ultimo_acceso DATETIME,
    estado VARCHAR(20) DEFAULT 'Activo',
    session_id VARCHAR(100),
    ip_usuario VARCHAR(45),
    dispositivo VARCHAR(50),
    FOREIGN KEY (IdSensor) REFERENCES Sensor(IdSensor)
);
GO

-- =================================================================
-- FASE 2: MODELO DIMENSIONAL (OLAP - DATA WAREHOUSE)
-- =================================================================
CREATE TABLE Dim_Agricultor (
    IdDimAgricultor INT PRIMARY KEY IDENTITY(1,1),
    NombreCompleto VARCHAR(200),
    DNI CHAR(8),
    fuente_dato VARCHAR(50) DEFAULT 'OLTP_Paska',
    sistema_origen VARCHAR(50) DEFAULT 'SQL Server',
    id_origen INT NOT NULL,
    lote_carga INT DEFAULT 1,
    fecha_carga_dw DATETIME DEFAULT GETDATE(),
    tipo_operacion VARCHAR(10) DEFAULT 'INSERT',
    vigente_desde DATETIME DEFAULT GETDATE(),
    vigente_hasta DATETIME
);
GO

CREATE TABLE Dim_Parcela (
    IdDimParcela INT PRIMARY KEY IDENTITY(1,1),
    Ubicacion VARCHAR(255),
    RangoHectareas VARCHAR(50),
    fuente_dato VARCHAR(50) DEFAULT 'OLTP_Paska',
    sistema_origen VARCHAR(50) DEFAULT 'SQL Server',
    id_origen INT NOT NULL,
    lote_carga INT DEFAULT 1,
    fecha_carga_dw DATETIME DEFAULT GETDATE(),
    tipo_operacion VARCHAR(10) DEFAULT 'INSERT',
    vigente_desde DATETIME DEFAULT GETDATE(),
    vigente_hasta DATETIME
);
GO

CREATE TABLE Dim_Sensor (
    IdDimSensor INT PRIMARY KEY IDENTITY(1,1),
    TipoSensor VARCHAR(50),
    fuente_dato VARCHAR(50) DEFAULT 'OLTP_Paska',
    sistema_origen VARCHAR(50) DEFAULT 'SQL Server',
    id_origen INT NOT NULL,
    lote_carga INT DEFAULT 1,
    fecha_carga_dw DATETIME DEFAULT GETDATE(),
    tipo_operacion VARCHAR(10) DEFAULT 'INSERT',
    vigente_desde DATETIME DEFAULT GETDATE(),
    vigente_hasta DATETIME
);
GO

CREATE TABLE Hecho_Lectura_Sensores (
    IdHechoLectura INT PRIMARY KEY IDENTITY(1,1),
    IdDimAgricultor INT NOT NULL,
    IdDimParcela INT NOT NULL,
    IdDimSensor INT NOT NULL,
    HumedadRegistrada DECIMAL(5,2),
    TemperaturaRegistrada DECIMAL(5,2),
    ConsumoBateria DECIMAL(5,2),
    fecha_proceso DATETIME DEFAULT GETDATE(),
    id_carga INT DEFAULT 1,
    FOREIGN KEY (IdDimAgricultor) REFERENCES Dim_Agricultor(IdDimAgricultor),
    FOREIGN KEY (IdDimParcela) REFERENCES Dim_Parcela(IdDimParcela),
    FOREIGN KEY (IdDimSensor) REFERENCES Dim_Sensor(IdDimSensor)
);
GO

-- =================================================================
-- FASE 3: INSERCION DE DATOS DE PRUEBA (MOCK DATA)
-- =================================================================
INSERT INTO Agricultor (Nombre, Apellido, DNI, Telefono, Correo, ip_usuario, dispositivo)
VALUES
('Carlos', 'Mendoza', '71234568', '987654321', 'cmendoza@agro.com', '192.168.1.10', 'App Movil'),
('Lucia', 'Ramirez', '78541236', '912345678', 'lramirez@agro.com', '192.168.1.11', 'Web');
GO

INSERT INTO Parcela (IdAgricultor, Ubicacion, Hectareas, ip_usuario)
VALUES
(1, 'Valle de Ica - Sector Norte', 15.50, '192.168.1.10'),
(2, 'Valle del Mantaro - Lote A', 8.20, '192.168.1.11');
GO

INSERT INTO Sensor (IdParcela, TipoSensor, Bateria, ip_usuario, dispositivo)
VALUES
(1, 'Humedad del Suelo', 85.00, '10.0.0.5', 'Sensor_IoT_V1'),
(1, 'Temperatura Ambiental', 92.50, '10.0.0.6', 'Sensor_IoT_V1'),
(2, 'Humedad del Suelo', 45.00, '10.0.0.7', 'Sensor_IoT_V1');
GO

INSERT INTO PersonalPaska (Nombre, Apellido, DNI, CorreoInsti, Rol, ip_usuario)
VALUES
('Jorge', 'Salinas', '45871236', 'jsalinas@paska.com', 'Ing. Agronomo', '172.16.0.5'),
('Andrea', 'Vargas', '74125896', 'avargas@paska.com', 'Soporte Tecnico', '172.16.0.6');
GO

INSERT INTO Reporte (IdSensor, TipoReporte, ip_usuario, dispositivo)
VALUES
(3, 'Alerta: Bateria Critica (45%)', '10.0.0.7', 'API_Alerta'),
(1, 'Alerta: Estres Hidrico Detectado', '10.0.0.5', 'API_Alerta');
GO

INSERT INTO Dim_Agricultor (NombreCompleto, DNI, id_origen)
VALUES ('Carlos Mendoza', '71234568', 1), ('Lucia Ramirez', '78541236', 2);
GO
INSERT INTO Dim_Parcela (Ubicacion, RangoHectareas, id_origen)
VALUES ('Valle de Ica - Sector Norte', 'Mediana (5-20)', 1);
GO
INSERT INTO Dim_Sensor (TipoSensor, id_origen)
VALUES ('Humedad del Suelo', 1);
GO
INSERT INTO Hecho_Lectura_Sensores (IdDimAgricultor, IdDimParcela, IdDimSensor,
HumedadRegistrada, TemperaturaRegistrada, ConsumoBateria)
VALUES (1, 1, 1, 18.5, 26.2, 5.0);
GO
