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
