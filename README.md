### README.md para `KunturTattoo`

```markdown
# KunturTattoo

## Descripción
KunturTattoo es una aplicación web para la gestión de un estudio de tatuajes. La aplicación permite a los clientes explorar diseños de tatuajes por categorías, enviar solicitudes de contacto con detalles específicos, y a los administradores gestionar diseños, categorías, usuarios y citas. La aplicación incluye funcionalidades de filtrado por categorías, notificaciones diarias de citas y seguridad con roles y permisos definidos mediante Spring Security.

## Estado del Proyecto
El proyecto se encuentra en la fase de desarrollo con funcionalidades clave implementadas. Todavía se están mejorando algunas características y optimizando el rendimiento.

## Tabla de Contenidos
- [Descripción](#descripción)
- [Estado del Proyecto](#estado-del-proyecto)
- [Instalación](#instalación)
- [Uso](#uso)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Contribución](#contribución)
- [Pruebas](#pruebas)
- [Roadmap](#roadmap)
- [Autores](#autores)
- [Licencia](#licencia)
- [Reconocimientos](#reconocimientos)
- [Soporte](#soporte)

## Instalación

### Requisitos Previos
- Java Development Kit (JDK) 11 o superior.
- Maven 3.6 o superior.
- MySQL 8.0 o superior (u otra base de datos relacional compatible).
- IDE como IntelliJ IDEA o Eclipse para el desarrollo.

### Configuración Local
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu_usuario/KunturTattoo.git
   ```
2. Navega al directorio del proyecto:
   ```bash
   cd KunturTattoo
   ```
3. Configura la base de datos en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/kuntur_tattoo
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
   ```
4. Ejecuta el siguiente comando para construir el proyecto:
   ```bash
   mvn clean install
   ```
5. Inicia la aplicación:
   ```bash
   mvn spring-boot:run
   ```
6. Accede a la aplicación en `http://localhost:8080`.

## Uso

### Funcionalidades del Usuario
- **Explorar diseños de tatuajes**: Los usuarios pueden ver los diseños de tatuajes filtrados por categorías.
- **Contacto**: Los usuarios pueden enviar un formulario de contacto que incluye su correo electrónico, asunto, tamaño en cm, parte del cuerpo a tatuar, links de ejemplo y mensaje. Este formulario envía un correo al administrador.

### Funcionalidades del Administrador
- **Gestión de Diseños**: Crear, editar y eliminar diseños de tatuajes.
- **Gestión de Categorías**: Crear y eliminar categorías de diseños.
- **Gestión de Usuarios**: Ver la lista de usuarios registrados.
- **Gestión de Citas**: Crear, editar, eliminar y ver detalles de las citas. Un correo se envía automáticamente al administrador a las 9 AM todos los días con un resumen de las citas pendientes del día.
- **Seguridad**: Roles definidos como admin, user, read con permisos para crear, eliminar, y actualizar mediante Spring Security.


## Estructura del Proyecto

```bash
KunturTattoo/
├── src/
│   ├── main/
│   │   ├── java/com/example/kunturtatto/
│   │   │   ├── components/         # Componentes para tareas programadas y otras utilidades
│   │   │   ├── config/             # Configuraciones de la aplicación
│   │   │   ├── controller/         # Controladores REST
│   │   │   ├── exception/          # Clases para manejo de excepciones personalizadas
│   │   │   ├── model/              # Entidades JPA
│   │   │   ├── repository/         # Repositorios JPA
│   │   │   ├── service/            # Servicios y lógica de negocio
│   │   │   │   └── impl/           # Implementaciones de servicios
│   │   │   └── KunturtattoApplication.java  # Clase principal de Spring Boot
│   │   ├── resources/
│   │   │   ├── static/             # Archivos estáticos como CSS y JS
│   │   │   │   ├── css/            # Hojas de estilo CSS
│   │   │   │   └── js/             # Scripts JavaScript
│   │   │   ├── templates/          # Plantillas Thymeleaf para las vistas
│   │   │   │   ├── admin/          # Vistas y plantillas para administración
│   │   │   │   │   ├── appointment/   # Plantillas para la gestión de citas
│   │   │   │   │   ├── categoryDesign/ # Plantillas para la gestión de categorías de diseño
│   │   │   │   │   ├── design/        # Plantillas para la gestión de diseños
│   │   │   │   │   └── users/         # Plantillas para la gestión de usuarios
│   │   │   │   └── user/           # Plantillas para vistas de usuario normal
│   │   │   └── application.properties # Configuración de la aplicación
│   └── test/                       # Pruebas unitarias y de integración
│       ├── java/com/example/kunturtatto/
│       │   ├── controller/         # Pruebas de controladores
│       │   └── KunturtattoApplicationTests.java  # Clase de pruebas principales
├── README.md                       # Documentación del proyecto
└── pom.xml    
```

## Contribución
Las contribuciones son bienvenidas. Sigue estos pasos para contribuir:
1. Haz un fork del repositorio.
2. Crea una rama para tu función (`git checkout -b feature/nueva-funcion`).
3. Haz commit de tus cambios (`git commit -am 'Añade una nueva función'`).
4. Empuja la rama (`git push origin feature/nueva-funcion`).
5. Abre un Pull Request.

## Pruebas
- Para ejecutar las pruebas unitarias y de integración, utiliza el siguiente comando:
  ```bash
  mvn test
  ```
- Las pruebas se encuentran en el directorio `src/test/java`.

## Autores
- **Jordy Camacho** - *Desarrollador Principal* - [Jordycamacho](https://github.com/Jordycamacho)

