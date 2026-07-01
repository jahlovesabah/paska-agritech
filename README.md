# 🌱 Paska Agritech

Sistema web concurrente para reducir la latencia de procesamiento de datos en
parcelas agrícolas del Perú (2026). Aplicación Java / Spring Boot que integra los
patrones de diseño, la programación funcional, la concurrencia y los temas de
teoría de lenguajes exigidos por el proyecto.

Base de datos: **PostgreSQL** (compatible con **Neon**). Despliegue: **Render**.

---

## 1. Requisitos previos

| Herramienta | Versión |
|-------------|---------|
| Java JDK    | 17 o superior |
| Maven       | 3.8 o superior |
| PostgreSQL  | 14+ (local) o una cuenta gratuita en Neon |

> La aplicación **arranca aunque no haya base de datos** (modo demostración con
> datos simulados). Con PostgreSQL/Neon conectado, **crea el esquema y carga los
> datos de prueba automáticamente** al iniciar.

---

## 2. Ejecutar en local

### Opción A – con PostgreSQL local

1. Crea una base llamada `PaskaAgritech_DB` en tu PostgreSQL.
2. Por defecto la app usa `usuario=postgres`, `clave=postgres`. Si difieren,
   define las variables de entorno antes de arrancar:
   ```bash
   export DATABASE_URL="jdbc:postgresql://localhost:5432/PaskaAgritech_DB"
   export DB_USER="postgres"
   export DB_PASSWORD="tu_clave"
   ```
3. Arranca:
   ```bash
   mvn spring-boot:run
   ```
4. Abre **http://localhost:8080**

### Opción B – sin base de datos (demostración)

Solo ejecuta `mvn spring-boot:run`. Las secciones de consola de consultas y
concurrencia funcionan con datos simulados.

---

## 3. Desplegar en línea (Render + Neon)

### Paso 1 – Crear la base de datos en Neon

1. Entra a https://neon.com y crea un proyecto (la base `neondb` ya viene lista).
2. Pulsa **Connect** y copia la **cadena de conexión**, con este aspecto:
   ```
   postgresql://USUARIO:CLAVE@ep-xxxx-pooler.us-east-2.aws.neon.tech/neondb?sslmode=require
   ```

### Paso 2 – Subir el proyecto a GitHub

```bash
git init
git add .
git commit -m "Paska Agritech"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/paska-agritech.git
git push -u origin main
```

### Paso 3 – Crear el servicio web en Render

**Opción con Blueprint (recomendada):** el repositorio ya incluye `render.yaml`.

1. En https://render.com pulsa **New > Blueprint** y conecta tu repositorio.
2. Render detecta `render.yaml` y crea un servicio web Docker.
3. Cuando pida la variable `DATABASE_URL`, pega la **cadena de conexión de Neon**
   del Paso 1.
4. Pulsa **Deploy**. Render construye la imagen con el `Dockerfile` y publica la
   app en una URL `https://paska-agritech.onrender.com`.

**Opción manual:** New > Web Service > conecta el repo > runtime **Docker** >
agrega la variable de entorno `DATABASE_URL` con la cadena de Neon > Create.

> Al arrancar, la app crea las tablas en Neon y carga los datos de prueba
> automáticamente. No necesitas ejecutar SQL a mano.

### Variables de entorno en Render

| Variable | Valor |
|----------|-------|
| `DATABASE_URL` | Cadena de conexión de Neon (`postgresql://...`) |
| `PORT` | La asigna Render automáticamente (no la definas tú) |

---

## 4. Módulos de la web

| Ruta            | Función |
|-----------------|---------|
| `/`             | Panel principal y mapa de requerimientos |
| `/monitoreo`    | Inventario de parcelas y sensores (datos de PostgreSQL) |
| `/consulta`     | Consola del lenguaje PASKA-QL (léxico, sintáctico, semántico) |
| `/agricultor`   | Registro con validación por autómata finito y regex |
| `/concurrencia` | Comparación secuencial vs concurrente + estadísticas funcionales |

---

## 5. Pruebas (requerimiento 3.4)

Pruebas unitarias con **JUnit 5**:

```bash
mvn test
```

Cubre el autómata finito, el compilador completo (léxico/sintáctico/semántico),
la programación funcional, el patrón Observer y la concurrencia. Archivos en
`src/test/java/`.

---

## 6. Correspondencia con los objetivos del proyecto

| Objetivo / Tema | Implementación | Archivo principal |
|-----------------|----------------|-------------------|
| **OE1** Arquitectura MVC | Spring MVC: Model / Controller / Vistas Thymeleaf | `model/`, `controller/`, `templates/` |
| **OE2** Programación funcional | Streams y lambdas sobre 10 000 registros | `service/ProcesadorTelemetria.java` |
| **OE3** Patrón Singleton | Conexión única a la base de datos | `singleton/ConexionBD.java` |
| **OE4** Patrón Observer | Dos alertas automatizadas reactivas | `observer/EstacionMonitoreo.java` |
| **OE5** Hilos concurrentes | Pool de hilos evaluando 5 parcelas a la vez | `service/ProcesadorConcurrente.java` |
| Expresiones regulares | Validación de correo, teléfono, palabras reservadas | `compiler/lexer/AnalizadorLexico.java` |
| Autómatas finitos | AFD para números y DNI | `compiler/lexer/AutomataFinito.java` |
| Gramáticas libres de contexto | Analizador descendente recursivo | `compiler/parser/AnalizadorSintactico.java` |
| Análisis sintáctico | Construcción del AST | `compiler/parser/NodoConsulta.java` |
| Validación semántica | Coherencia de rangos y reglas de dominio | `compiler/semantic/ValidadorSemantico.java` |
| Pruebas (3.4) | JUnit 5 | `src/test/java/` |

---

## 7. Lenguaje de consultas PASKA-QL

```
<consulta>   ::= <comando> <metrica> <fuente> <filtro_opt>
<comando>    ::= CONSULTAR | MONITOREAR | ALERTAR
<metrica>    ::= HUMEDAD | TEMPERATURA | BATERIA
<fuente>     ::= DE PARCELA NUMERO | DE SENSOR NUMERO
<filtro_opt> ::= DONDE VALOR <operador> NUMERO | (vacio)
<operador>   ::= < | > | <= | >= | =
```

Ejemplos válidos:
```
CONSULTAR HUMEDAD DE PARCELA 1
MONITOREAR TEMPERATURA DE SENSOR 3
ALERTAR BATERIA DE PARCELA 2 DONDE VALOR < 50
```

---

## 8. Estructura del proyecto

```
paska-agritech/
├── pom.xml                 # PostgreSQL + JUnit
├── Dockerfile              # build multi-etapa para Render
├── render.yaml             # Blueprint de Render (Docker)
├── .dockerignore / .gitignore
├── README.md
├── sql/
│   ├── PaskaAgritech_BD_postgres.sql        # esquema + datos (PostgreSQL/Neon)
│   └── PaskaAgritech_BD_sqlserver_legacy.sql# script SQL Server original
└── src/
    ├── main/
    │   ├── java/com/paska/agritech/
    │   │   ├── PaskaAgritechApplication.java
    │   │   ├── config/InicializadorBD.java   # crea esquema + datos al arrancar
    │   │   ├── singleton/ConexionBD.java      (OE3, PostgreSQL/Neon)
    │   │   ├── model/                         (OE1 - Model)
    │   │   ├── dao/                           (Agricultor, Parcela, Sensor)
    │   │   ├── observer/                      (OE4)
    │   │   ├── compiler/  (lexer, parser, semantic)
    │   │   ├── service/                       (OE2 + OE5)
    │   │   └── controller/                    (OE1 - Controller)
    │   └── resources/
    │       ├── application.properties        # server.port=${PORT}
    │       ├── db/schema.sql, data.sql        # inicializacion automatica
    │       ├── templates/                     (OE1 - Vistas)
    │       └── static/css/
    └── test/java/com/paska/agritech/          # pruebas JUnit (3.4)
```
