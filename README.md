# 🦅 Kunturtatto — Plataforma Web para Estudio de Tatuajes

Aplicación web full-stack desarrollada con Spring Boot y Thymeleaf para la gestión integral de un estudio de tatuajes. Permite a los clientes explorar diseños y reservar citas, mientras que el equipo administrativo gestiona el catálogo, las consultas y el calendario desde un panel de control dedicado.

---

## 📋 Tabla de Contenidos

- [Descripción general](#descripción-general)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Primeros pasos](#primeros-pasos)
  - [Requisitos previos](#requisitos-previos)
  - [Con Docker (recomendado)](#con-docker-recomendado)
  - [En local (desarrollo)](#en-local-desarrollo)
- [Configuración](#configuración)
  - [Variables de entorno](#variables-de-entorno)
  - [Propiedades de la aplicación](#propiedades-de-la-aplicación)
- [Módulos principales](#módulos-principales)
- [Documentación de la API](#documentación-de-la-api)
- [Tests](#tests)

---

## Descripción general

Kunturtatto (desplegada en producción como **Muthabara**) es una plataforma web orientada a estudios de tatuajes. Combina una vista pública para clientes con un panel de administración completo.

**Funcionalidades principales:**
- Galería de diseños organizada por categorías y subcategorías
- Formulario de consulta de tatuajes con notificaciones por correo
- Gestión de citas con calendario y recordatorios automáticos
- Panel de administración para usuarios, roles y contenido
- Subida y gestión de imágenes
- Integración con Google Analytics
- Auditoría de correos enviados
- Caché de consultas para mejorar el rendimiento

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Runtime | Java 17 |
| Framework | Spring Boot |
| Motor de plantillas | Thymeleaf |
| Base de datos | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Seguridad | Spring Security |
| Correo | Spring Mail (Gmail SMTP) |
| Caché | Spring Cache (Simple) |
| Mapeos | MapStruct |
| Build | Maven |
| Contenedores | Docker + Docker Compose |
| Documentación | Swagger / SpringDoc |
| Analytics | Google Analytics |

---

## Estructura del proyecto

```
kunturtatto/
└── src/main/java/com/example/kunturtatto/
    ├── common/          # Constantes de logging
    ├── components/      # Tareas programadas (recordatorios de citas)
    ├── config/          # Seguridad, caché, mail, Swagger, recursos estáticos
    ├── controller/      # Controladores web (admin, usuario, citas, analytics)
    ├── dto/             # Objetos de transferencia de datos
    ├── exception/       # Excepciones personalizadas
    ├── mapper/          # Mappers MapStruct
    ├── model/           # Entidades JPA (User, Appointment, Design, Category...)
    ├── repository/      # Repositorios Spring Data
    ├── request/         # Clases de petición y respuesta
    └── service/         # Interfaces e implementaciones de servicios
└── src/main/resources/
    ├── templates/
    │   ├── admin/       # Panel de administración (citas, diseños, usuarios...)
    │   └── user/        # Vistas públicas (galería, contacto, consultas...)
    ├── static/          # CSS y JS propios
    └── images/          # Imágenes estáticas por defecto
```

---

## Primeros pasos

### Requisitos previos

- **Docker y Docker Compose** (recomendado)
- **Java 17** (para desarrollo local)
- **Maven 3.9+** (para desarrollo local)
- **MySQL 8.0** (para desarrollo local sin Docker)

---

### Con Docker (recomendado)

**1. Clona el repositorio:**
```bash
git clone https://github.com/tu-usuario/kunturtatto.git
cd kunturtatto
```

**2. Crea tu archivo `.env`** (ver [Variables de entorno](#variables-de-entorno)):
```bash
cp .env.example .env
# Edita .env con tus valores
```

**3. Levanta los servicios:**
```bash
docker compose up -d
```

**4. Verifica que todo está corriendo:**
```bash
docker compose ps
```

| Servicio | URL |
|---------|-----|
| Aplicación web | http://localhost:8081 |
| Base de datos MySQL | localhost:3308 |

---

### En local (desarrollo)

**1. Levanta solo la base de datos:**
```bash
docker compose up -d db
```

**2. Configura las variables de entorno** o edita directamente `application.properties` con tus valores locales.

**3. Ejecuta la aplicación:**
```bash
./mvnw spring-boot:run
```

La aplicación arrancará en `http://localhost:8080`.

Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`

---

## Configuración

### Variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
# Base de datos
DB_ROOT_PASSWORD=tu_contraseña_root
DB_PASSWORD=tu_contraseña_usuario

# Correo electrónico (Gmail)
EMAIL_USER=tu-estudio@gmail.com
EMAIL_PASS=tu_contraseña_de_aplicacion

# Google Analytics (opcional)
GOOGLE_ANALYTICS_TRACKING_ID=G-XXXXXXXXXX
GOOGLE_ANALYTICS_ENABLED=true

# URLs de producción
APP_URL=https://tu-dominio.com
SERVER_URL=https://tu-dominio.com

# Perfil de Spring
SPRING_PROFILES_ACTIVE=prod
```

> **Nota sobre la contraseña de Gmail:** debes generar una *contraseña de aplicación* desde tu cuenta de Google (Seguridad → Verificación en dos pasos → Contraseñas de aplicaciones), no usar tu contraseña habitual.

### Propiedades de la aplicación

Las principales propiedades configurables en `application.properties`:

| Propiedad | Descripción | Por defecto |
|-----------|-------------|-------------|
| `app.upload.dir` | Directorio de subida de imágenes | `./images` |
| `spring.servlet.multipart.max-file-size` | Tamaño máximo por archivo | `10MB` |
| `logging.file.name` | Ruta del fichero de log | `logs/muthabara.log` |
| `spring.jpa.hibernate.ddl-auto` | Gestión del esquema | `update` |
| `spring.cache.type` | Tipo de caché | `simple` |

---

## Módulos principales

### Vistas públicas (`/`)
- **Inicio** — presentación del estudio con galería y vídeo de fondo
- **Diseños** — catálogo filtrado por categorías y subcategorías
- **Consulta de tatuaje** — formulario para solicitar presupuesto o información
- **Contacto** — formulario de contacto general
- **Registro / Login** — autenticación de usuarios

### Panel de administración (`/admin`)
- **Citas** — creación, edición, visualización en calendario y listado
- **Diseños** — CRUD completo con subida de imágenes
- **Categorías y subcategorías** — organización del catálogo
- **Consultas** — gestión y seguimiento de solicitudes de clientes
- **Usuarios** — administración de cuentas y roles
- **Estadísticas** — métricas y gráficas de actividad
- **Analytics** — dashboard integrado con Google Analytics

### Servicios internos
- **`RemainderServiceImpl`** — envío automático de recordatorios de cita por correo
- **`MailScheduler`** — tarea programada para el envío de recordatorios
- **`EmailAuditServiceImpl`** — registro y auditoría de todos los correos enviados
- **`ImageServiceImpl`** — gestión de subida y almacenamiento de imágenes
- **`AnalyticsService`** — integración con la API de Google Analytics

---

## Documentación de la API

Disponible en desarrollo en:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Tests

El proyecto incluye tests unitarios e de integración organizados por capa:

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar un test específico
./mvnw test -Dtest=AppointmentServiceImplTest
```

| Tipo | Clases de test |
|------|---------------|
| Controladores | `AdminControllerTest`, `AppointmentControllerTest`, `UserControllerTest`, `MailControllerTest` |
| Servicios | `AppointmentServiceImplTest`, `CategoryServiceImplTest`, `SubCategoryServiceImplTest`, `SubCategoryServiceCacheTest` |
| Mappers | `DesignMapperTest`, `SubCategoryMapperTest` |
| Repositorios | `SubCategoryRepositoryTest` |