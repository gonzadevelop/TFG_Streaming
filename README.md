# KeySound — Plataforma de Streaming Musical
**TFG · DAW · IES Alonso de Avellaneda · 2024-2026**

---

**IES ALONSO DE AVELLANEDA**

Ciclo Formativo de Grado Superior — Desarrollo de Aplicaciones Web

## KeySound
### Plataforma Web de Streaming Musical

**Autores:**

- Gonzalo Vicente (@gonzadevelop)
- Enrique Ruiz (@kikeruuuiz) 
- Elena Cristina Chiru (@KiruuxX) 

**Tutora:** Mariluz

**Año académico:** 2024 – 2026

---

## Índice de contenidos

1. [Introducción y justificación](#1-introducción-y-justificación)
   - 1.1. Descripción del proyecto
   - 1.2. Finalidad
   - 1.3. Objetivos
   - 1.4. Motivación
2. [Análisis y diseño del proyecto](#2-análisis-y-diseño-del-proyecto)
   - 2.1. Arquitectura web
   - 2.2. Tecnologías y herramientas utilizadas
   - 2.3. Análisis de usuarios (perfiles de usuario)
   - 2.4. Requisitos funcionales y no funcionales
   - 2.5. Estructura de navegación
   - 2.6. Organización de la lógica de negocio
   - 2.7. Modelo de datos simplificado
3. [Conclusiones](#3-conclusiones)
   - 3.1. Resultados obtenidos
   - 3.2. Retos encontrados y soluciones
   - 3.3. Planificación y metodología
   - 3.4. Aprendizajes y mejoras futuras
4. [Bibliografía y fuentes de información](#4-bibliografía-y-fuentes-de-información)
5. [Anexos](#5-anexos)
   - Anexo A: Guía de instalación y ejecución en local
   - Anexo B: Estructura de carpetas del repositorio
   - Anexo C: Endpoints principales de la API REST

---

## 1. Introducción y justificación

### 1.1. Descripción del proyecto

KeySound es una plataforma web de streaming musical diseñada para ofrecer a los usuarios una experiencia de escucha fluida y personalizada, y a los artistas un espacio propio para gestionar y publicar su catálogo. La aplicación permite reproducir canciones en tiempo real, organizar el contenido en álbumes y playlists, seguir a artistas y llevar un historial detallado de reproducciones.

El proyecto se enmarca en el ciclo formativo de Grado Superior en Desarrollo de Aplicaciones Web (DAW) del IES Alonso de Avellaneda y constituye el Trabajo de Fin de Grado (TFG) del curso 2024-2026. Ha sido desarrollado íntegramente por tres alumnos que han distribuido los roles de forma vertical: un desarrollador de backend y dos desarrolladoras de frontend, cada una responsable de una zona de la interfaz.

### 1.2. Finalidad

La finalidad de KeySound es doble. Por un lado, ofrece a los oyentes una plataforma donde descubrir música, seguir a sus artistas favoritos, gestionar playlists y consultar estadísticas de escucha personalizadas. Por otro lado, proporciona a los artistas un panel de control desde el que publicar álbumes, subir pistas de audio, monitorizar sus lanzamientos y conocer el número de reproducciones de cada canción.

### 1.3. Objetivos

Los objetivos principales del proyecto son:

- Desarrollar una API REST robusta con Spring Boot que gestione autenticación JWT, catálogo musical, playlists, favoritos, historial de reproducciones y estadísticas.
- Construir una Single Page Application (SPA) con Angular que consuma dicha API, con separación clara entre la zona de cliente y la zona de artista.
- Integrar Firebase Storage como solución de almacenamiento en la nube para los ficheros de audio y las imágenes de portada.
- Implementar un sistema de seguridad completo basado en Spring Security y tokens JWT.
- Desplegar la aplicación en un entorno contenerizado con Docker y una base de datos MySQL en Google Cloud SQL.
- Aplicar buenas prácticas de desarrollo: patrón repositorio, DTOs con MapStruct, manejo global de excepciones y documentación de la API con OpenAPI/Swagger.

### 1.4. Motivación

La música es una de las industrias digitales con mayor proyección. Plataformas como Spotify o Apple Music han transformado la forma en que se consume contenido musical, y entender cómo funcionan sus tecnologías subyacentes —streaming de audio, APIs REST, autenticación, almacenamiento en la nube— supone un reto técnico completo y altamente motivador.

Desde el punto de vista formativo, el proyecto permite integrar los conocimientos adquiridos en todos los módulos del ciclo: programación en Java, desarrollo frontend con frameworks modernos, bases de datos relacionales, despliegue con contenedores y seguridad en aplicaciones web. Además, el uso de tecnologías no vistas en clase como Firebase Storage, Google Cloud SQL o la librería MapStruct añade un componente de aprendizaje autónomo especialmente valioso.

---

## 2. Análisis y diseño del proyecto

### 2.1. Arquitectura web

KeySound sigue una arquitectura cliente-servidor desacoplada compuesta por dos grandes bloques:

- **Frontend SPA (Single Page Application):** desarrollada en Angular 21, carga una única vez y navega entre vistas sin recargar la página. Se comunica exclusivamente con el backend a través de peticiones HTTP (fetch/XMLHttpRequest) al endpoint `/api`.
- **Backend REST API:** desarrollado con Spring Boot 3.5, expone endpoints RESTful en el puerto 8080 bajo el context-path `/api`. Gestiona la lógica de negocio, la persistencia en MySQL y el acceso a Firebase Storage.

La separación entre frontend y backend permite que ambas capas evolucionen de forma independiente y facilita el despliegue en contenedores separados. El frontend se sirve desde un contenedor Nginx, mientras que el backend se ejecuta en un contenedor de la JVM.

Dentro del backend se aplica el patrón MVC adaptado a APIs REST:

- **Controllers:** reciben las peticiones HTTP, delegan en los servicios y devuelven respuestas DTO.
- **Services:** contienen la lógica de negocio y coordinan repositorios y servicios externos.
- **Repositories:** interfaces JPA que abstraen el acceso a la base de datos MySQL.
- **Entities:** clases Java que mapean las tablas de la base de datos.
- **Mappers (MapStruct):** convierten automáticamente entre entidades y DTOs, eliminando código boilerplate.

### 2.2. Tecnologías y herramientas utilizadas

#### Frontend

| Tecnología / Librería | Versión | Uso |
|---|---|---|
| Angular | 21.0.x | Framework principal de la SPA |
| TypeScript | 5.x | Lenguaje de programación tipado |
| HTML5 / CSS3 / SCSS | — | Maquetación y estilos |
| RxJS | 7.x | Programación reactiva (Observables) |
| Angular Router | incluida | Navegación SPA y guards de rutas |
| Angular HttpClient | incluida | Comunicación con la API REST |
| Angular Forms | incluida | Formularios reactivos |

#### Backend

| Tecnología / Librería | Versión | Uso |
|---|---|---|
| Java | 21 (LTS) | Lenguaje principal del backend |
| Spring Boot | 3.5.9 | Framework principal REST |
| Spring Security | incluida en Boot | Autenticación y autorización |
| Spring Data JPA / Hibernate | incluida en Boot | ORM y acceso a base de datos |
| JJWT (jjwt-api) | 0.12.6 | Generación y validación de tokens JWT |
| MapStruct | 1.5.5 | Mapeo automático Entity ↔ DTO |
| Lombok | incluida en Boot | Reducción de código boilerplate |
| springdoc-openapi | 2.8.16 | Documentación Swagger / OpenAPI 3 |
| Firebase Admin SDK | 9.2.0 | Acceso a Firebase Storage desde Java |
| spring-dotenv | 4.0.0 | Carga de variables desde fichero .env |
| Spring Boot Actuator | incluida en Boot | Monitorización y health checks |

#### Base de datos

| Tecnología | Versión | Uso |
|---|---|---|
| MySQL | 8.x | Base de datos relacional principal |
| Google Cloud SQL | — | Hosting de MySQL en producción |
| HikariCP | incluida en Boot | Pool de conexiones JDBC |

#### Almacenamiento y servicios externos

| Servicio | Uso |
|---|---|
| Firebase Storage | Almacenamiento de ficheros de audio (.mp3) e imágenes de portada |
| Google Cloud SQL | Base de datos MySQL gestionada en la nube |

#### Infraestructura y despliegue

| Herramienta | Uso |
|---|---|
| Docker | Contenerización de backend, frontend y base de datos local |
| Docker Compose | Orquestación de los contenedores en desarrollo |
| Git / GitHub | Control de versiones y repositorio del proyecto |
| IntelliJ IDEA | IDE principal para el desarrollo del backend |
| Visual Studio Code | IDE para el desarrollo del frontend |

#### Pruebas y calidad

| Herramienta | Uso |
|---|---|
| Spring Boot Test / JUnit 5 | Tests de integración del backend |
| Spring Security Test | Tests de autenticación y autorización |
| Swagger UI | Pruebas manuales de los endpoints REST |

### 2.3. Análisis de usuarios (perfiles de usuario)

KeySound define tres perfiles de usuario con distintos niveles de acceso y funcionalidades:

#### Usuario cliente (oyente)

Es el perfil principal. Puede registrarse en la plataforma, iniciar sesión, buscar y escuchar música en streaming. Sus funcionalidades son:

- Registro, inicio de sesión y verificación de correo electrónico.
- Reproducción de canciones con controles de audio (play/pause, volumen, pista anterior/siguiente, cola de reproducción).
- Exploración del catálogo: álbumes, artistas, géneros.
- Creación y gestión de playlists personalizadas.
- Marcar canciones y álbumes como favoritos.
- Seguir a artistas.
- Consultar estadísticas de escucha personalizadas (canciones más escuchadas, tiempo de escucha, etc.).
- Gestión del perfil de usuario (avatar, datos personales).

#### Artista

El perfil de artista dispone de un panel propio (Zona Artista) con funcionalidades de gestión del catálogo:

- Visualizar sus álbumes publicados.
- Subir nuevos álbumes con portada y pistas de audio.
- Gestionar las canciones de cada álbum.
- Consultar estadísticas de sus canciones (reproducciones, popularidad).

#### Sistema / Administración

Existe una capa de roles gestionada en base de datos (tabla `roles`) que permite ampliar el sistema de permisos. El backend aplica autorización basada en roles mediante Spring Security, de modo que ciertos endpoints están restringidos según el rol del usuario autenticado.

### 2.4. Requisitos funcionales y no funcionales

#### Requisitos funcionales

| ID | Requisito | Perfil |
|---|---|---|
| RF-01 | El sistema permite el registro de nuevos usuarios con verificación de email | Cliente |
| RF-02 | El sistema autentica usuarios mediante usuario/contraseña y emite un token JWT | Todos |
| RF-03 | Los usuarios pueden reproducir canciones en streaming desde Firebase Storage | Cliente |
| RF-04 | Los usuarios pueden crear, editar y eliminar playlists propias | Cliente |
| RF-05 | Los usuarios pueden marcar canciones y álbumes como favoritos | Cliente |
| RF-06 | Los usuarios pueden seguir a artistas | Cliente |
| RF-07 | El sistema registra el historial de reproducciones de cada usuario | Cliente |
| RF-08 | El sistema genera estadísticas de escucha por usuario | Cliente |
| RF-09 | El sistema mantiene un ranking diario de canciones más escuchadas (top_musical_diario) | Sistema |
| RF-10 | Los artistas pueden subir álbumes con portada e imagen | Artista |
| RF-11 | Los artistas pueden subir pistas de audio asociadas a un álbum | Artista |
| RF-12 | Los artistas pueden consultar estadísticas de sus canciones | Artista |
| RF-13 | El sistema almacena los ficheros de audio en Firebase Storage | Sistema |
| RF-14 | La API expone documentación interactiva mediante Swagger UI | Desarrollo |

#### Requisitos no funcionales

| ID | Tipo | Descripción |
|---|---|---|
| RNF-01 | Seguridad | Toda comunicación está protegida por JWT; las contraseñas se almacenan con hash bcrypt |
| RNF-02 | Seguridad | Los endpoints sensibles requieren token válido; los roles restringen acciones por perfil |
| RNF-03 | Rendimiento | El pool de conexiones HikariCP gestiona eficientemente las conexiones a MySQL |
| RNF-04 | Escalabilidad | La arquitectura desacoplada (API REST + SPA) permite escalar frontend y backend de forma independiente |
| RNF-05 | Mantenibilidad | Se utilizan DTOs y MapStruct para evitar exponer entidades JPA directamente |
| RNF-06 | Usabilidad | La interfaz es responsive y funciona en escritorio y dispositivos móviles |
| RNF-07 | Disponibilidad | El sistema se despliega en contenedores Docker para garantizar portabilidad |
| RNF-08 | Tamaño de archivo | El backend limita los ficheros subidos: 10 MB por fichero y 120 MB por petición |
| RNF-09 | Documentación | La API está documentada con OpenAPI 3 accesible en `/api/swagger-ui.html` |
| RNF-10 | Zona horaria | El backend opera en UTC+1 (Europa/Madrid) para consistencia en fechas |

### 2.5. Estructura de navegación

La aplicación se divide en tres grandes zonas de navegación:

#### Zona pública (sin autenticar)

- `/login` — Formulario de inicio de sesión
- `/registro` — Formulario de registro de nuevo usuario
- `/email-verification` — Pantalla de verificación de correo

#### Zona cliente (usuario autenticado con rol oyente)

- `/home` — Página principal con recomendaciones y novedades
- `/album/:id` — Detalle de un álbum con lista de canciones
- `/artista/:id` — Página de perfil de artista con su discografía
- `/playlist/:id` — Detalle de una playlist
- `/lista-favoritos` — Lista de canciones y álbumes favoritos del usuario
- `/keysound-playlists` — Playlists creadas por la plataforma
- `/estadistica` — Estadísticas personalizadas de escucha
- `/perfil` — Gestión del perfil de usuario

El layout de la zona cliente incluye un componente Header, un Sidebar de navegación lateral, un Player de audio persistente en el Footer y un sistema de Cola de reproducción.

#### Zona artista (usuario autenticado con rol artista)

- `/artista-home` — Panel principal del artista
- `/artista-albums` — Listado de álbumes publicados por el artista
- `/artista-subir-album` — Formulario para publicar un nuevo álbum con pistas

Los guards de Angular protegen las rutas privadas y redirigen a `/login` si el usuario no está autenticado o no tiene el rol adecuado.

### 2.6. Organización de la lógica de negocio

#### Estructura del backend

El backend sigue una arquitectura en capas dentro del paquete raíz `tfg.KeySound`:

| Paquete / Clase | Responsabilidad |
|---|---|
| `config/` | Configuración global: FirebaseConfig, SecurityConfig, JwtFilter, StartupRankingUpdater |
| `controllers/` | Controladores REST: Album, Artista, Auth, Cancion, Estadisticas, Favoritos, Firebase, Home, Playlist, Usuario |
| `services/` | Lógica de negocio: Album, Artista, Auth, Cancion, Estadisticas, Favoritos, Home, Playlist, Ranking, Usuario |
| `repositorys/` | Interfaces JPA: Album, Cancion, HistorialReproducciones, Pista, PlaylistPista, Playlist, Rol, TopMusicalDiario, Usuario |
| `entitys/` | Entidades JPA que mapean las tablas de MySQL |
| `model/` | DTOs organizados por dominio: album, artista, auth, cancion, estadisticas, home, pista, playlist, usuario |
| `mappers/` | Interfaces MapStruct para conversión Entity ↔ DTO: Album, Artista, Cancion, Pista, Playlist, TopMusicalDiario, Usuario |
| `exception/` | Manejo de errores: KeySoundException, GlobalExceptionHandler y excepciones por dominio |
| `utils/` | Utilidades: ArtistaUtils, AudioUtils (parseo de metadatos MP3) |

#### Flujo de autenticación

El sistema de autenticación funciona de la siguiente manera: el usuario envía sus credenciales al endpoint `POST /api/auth/login`. `AuthService` valida la contraseña con BCrypt y, si es correcta, genera un token JWT firmado con HMAC-SHA256 mediante la librería JJWT. El token se devuelve al cliente, que lo almacena y lo incluye en la cabecera `Authorization: Bearer <token>` en todas las peticiones posteriores. `JwtFilter` intercepta cada petición, valida el token y establece el contexto de seguridad de Spring Security.

#### Servicio de ranking

`StartupRankingUpdater` es un componente que se ejecuta al arrancar la aplicación y actualiza la tabla `top_musical_diario` con las canciones más reproducidas del día. `RankingService` gestiona esta lógica consultando el historial de reproducciones y calculando el ranking.

#### Integración con Firebase Storage

`FirebaseConfig` inicializa el SDK de Firebase Admin al arrancar el backend, leyendo las credenciales desde el fichero `firebase-account.json` ubicado en `resources`. `FirebaseController` y el servicio externo correspondiente gestionan la subida de ficheros de audio e imágenes, almacenándolos en Firebase Storage y devolviendo la URL pública al cliente para su reproducción.

#### Conexión con APIs y servicios externos

| Servicio externo | Propósito | Integración |
|---|---|---|
| Firebase Storage | Almacenamiento de ficheros de audio (.mp3) y portadas de álbumes | Firebase Admin SDK 9.2.0; credenciales en `firebase-account.json` |
| Google Cloud SQL | Base de datos MySQL en la nube para producción | JDBC estándar con Spring Data JPA; configuración vía variables de entorno |

### 2.7. Modelo de datos simplificado

La base de datos KeySound contiene 15 tablas que se agrupan en los siguientes dominios:

#### Dominio de usuarios y seguridad

| Tabla | Descripción | Relaciones principales |
|---|---|---|
| `usuarios` | Almacena todos los usuarios: oyentes y artistas. Campos: id, nombre, email, password (bcrypt), avatar, fecha_registro, rol_id | FK a `roles` |
| `roles` | Catálogo de roles del sistema (ej: ROLE_USER, ROLE_ARTIST) | 1:N con `usuarios` |
| `seguidores` | Relación de seguimiento entre usuarios (oyente sigue a artista) | FK a `usuarios` (seguidor y seguido) |

#### Dominio musical

| Tabla | Descripción | Relaciones principales |
|---|---|---|
| `albums` | Álbumes musicales: título, portada (URL Firebase), fecha_lanzamiento, artista_id | FK a `usuarios` (artista) |
| `canciones` | Canciones del catálogo: título, duración, género, URL del audio en Firebase | N:M con artistas y productores |
| `pistas` | Asociación de una canción a un álbum con su número de pista | FK a `albums` y `canciones` |
| `cancion_artista` | Tabla intermedia: relación N:M entre canciones y artistas | FK a `canciones` y `usuarios` |
| `cancion_productores` | Tabla intermedia: relación N:M entre canciones y productores | FK a `canciones` y `usuarios` |
| `lanzamientos` | Registro de lanzamientos oficiales de canciones o álbumes | FK a `canciones`/`albums` |

#### Dominio de actividad del usuario

| Tabla | Descripción | Relaciones principales |
|---|---|---|
| `playlists` | Listas de reproducción creadas por los usuarios: nombre, descripción, portada, usuario_id | FK a `usuarios` |
| `playlist_pistas` | Relación N:M entre playlists y canciones con orden de pista | FK a `playlists` y `canciones` |
| `playlists_keysound` | Playlists oficiales curadas por la plataforma KeySound | — |
| `favoritos` | Registro de canciones marcadas como favoritas por cada usuario | FK a `usuarios` y `canciones` |
| `historial_reproducciones` | Log de cada reproducción: usuario, canción y timestamp | FK a `usuarios` y `canciones` |
| `top_musical_diario` | Ranking diario de canciones más escuchadas, actualizado al arranque | FK a `canciones` |

---

## 3. Conclusiones

### 3.1. Resultados obtenidos

El resultado del proyecto es una plataforma web de streaming musical funcional que cubre los dos perfiles principales: el oyente y el artista. Se ha conseguido implementar el núcleo de la aplicación con reproducción de audio en streaming, gestión de playlists y favoritos, panel de artista para la subida de álbumes y pistas, historial de reproducciones, estadísticas de escucha y un ranking diario de canciones.

Desde el punto de vista técnico, se ha logrado integrar con éxito un stack moderno y complejo: Angular en el frontend, Spring Boot en el backend, MySQL como base de datos relacional, Firebase Storage para el almacenamiento de ficheros multimedia y Docker para la contenerización del entorno. La API REST está completamente documentada mediante Swagger UI gracias a springdoc-openapi.

### 3.2. Retos encontrados y soluciones

| Reto | Solución implementada |
|---|---|
| Configuración de variables de entorno en Spring Boot con IntelliJ | Uso de la librería `spring-dotenv` para cargar el fichero `.env`, complementado con la configuración de Environment variables en las Run Configurations de IntelliJ |
| Permisos de directorio temporal en Windows (java.io.tmpdir) | Añadir la opción de JVM `-Djava.io.tmpdir=C:\Users\<user>\AppData\Local\Temp` en la configuración de arranque |
| Errores de autenticación MySQL ("Access denied for user") | Creación de un usuario de desarrollo (`kiru@localhost`) con GRANT explícito y verificación del plugin de autenticación |
| Integración de Firebase Admin SDK con Spring Boot | Configuración de `FirebaseConfig` como `@Component` con inicialización lazy al primer uso, leyendo las credenciales desde un fichero JSON en `resources` |
| Mapeo complejo entre entidades y DTOs | Adopción de MapStruct para generación automática de código de mapeo en tiempo de compilación, reduciendo errores manuales |
| Streaming de audio en el frontend | Uso de la Web Audio API del navegador con la URL pública de Firebase Storage, implementando controles personalizados en el componente Player |

### 3.3. Planificación y metodología

El desarrollo se ha organizado siguiendo un enfoque iterativo basado en funcionalidades, dividiendo el trabajo en sprints informales. La distribución de tareas ha seguido la especialización de cada miembro del equipo:

- **Sprint 1:** Configuración del entorno, diseño del modelo de datos y estructura inicial del proyecto (backend + frontend).
- **Sprint 2:** Implementación de la autenticación JWT, registro de usuarios y sistema de roles.
- **Sprint 3:** CRUD de álbumes, canciones y pistas; integración con Firebase Storage.
- **Sprint 4:** Funcionalidades de usuario (playlists, favoritos, historial) y zona artista en el frontend.
- **Sprint 5:** Estadísticas, ranking diario, mejoras de UX y corrección de errores.
- **Sprint 6:** Documentación, pruebas y preparación del despliegue.

El control de versiones se ha gestionado con Git y GitHub, con ramas de trabajo por funcionalidad y pull requests para integrar cambios en la rama principal.

### 3.4. Aprendizajes y mejoras futuras

Entre los principales aprendizajes del proyecto destacan: la gestión de la seguridad en una API REST con Spring Security y JWT, la integración de servicios externos en la nube (Firebase, Google Cloud SQL), el uso de Docker para normalizar entornos de desarrollo y la arquitectura desacoplada entre frontend y backend.

Como posibles mejoras futuras se identifican:

- Implementar búsqueda avanzada con Elasticsearch para indexar el catálogo musical.
- Añadir notificaciones en tiempo real con WebSockets (por ejemplo, cuando un artista al que el usuario sigue publica un álbum nuevo).
- Desarrollar una aplicación móvil nativa o PWA consumiendo la misma API.
- Añadir un sistema de recomendaciones basado en el historial de escucha del usuario.
- Implementar pruebas de integración completas con Testcontainers para la capa de repositorios.
- Incorporar un pipeline de CI/CD con GitHub Actions para automatizar el despliegue.

---

## 4. Bibliografía y fuentes de información

### Documentación oficial

- Spring Boot Reference Documentation. https://docs.spring.io/spring-boot/docs/current/reference/html/
- Spring Security Reference. https://docs.spring.io/spring-security/reference/
- Angular Documentation. https://angular.dev/docs
- Firebase Admin SDK for Java. https://firebase.google.com/docs/admin/setup
- MapStruct Reference Guide. https://mapstruct.org/documentation/stable/reference/html/
- JJWT Library. https://github.com/jwtk/jjwt
- springdoc-openapi. https://springdoc.org/
- Docker Documentation. https://docs.docker.com/
- MySQL 8.0 Reference Manual. https://dev.mysql.com/doc/refman/8.0/en/
- Google Cloud SQL Documentation. https://cloud.google.com/sql/docs

### Recursos adicionales

- Baeldung — Spring Security with JWT. https://www.baeldung.com/spring-security-oauth-jwt
- Baeldung — MapStruct Quick Guide. https://www.baeldung.com/mapstruct
- Spring Boot DevTools Documentation. https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools
- CycloneDX Maven Plugin. https://cyclonedx.github.io/cyclonedx-maven-plugin/

---

## 5. Anexos

### Anexo A: Guía de instalación y ejecución en local

#### Requisitos previos

- Java 21 (JDK)
- Node.js 18+ y npm
- MySQL 8.x en ejecución local
- Docker y Docker Compose (opcional, para entorno contenerizado)
- Cuenta de Firebase con proyecto configurado (fichero `firebase-account.json`)

#### Backend (KeySound-BackEnd)

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/gonzadevelop/TFG_Streaming.git
   ```

2. Navegar al directorio:
   ```bash
   cd TFG_Streaming/KeySound-BackEnd
   ```

3. Crear el fichero `.env` en la raíz del módulo con el siguiente contenido:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=KeySound
   DB_USER=<usuario_mysql>
   DB_PASSWORD=<password_mysql>
   FIREBASE_CONFIG_PATH=firebase-account.json
   ```

4. Colocar el fichero `firebase-account.json` en `src/main/resources/`

5. Crear la base de datos y el usuario en MySQL:
   ```sql
   CREATE DATABASE KeySound CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER '<usuario>'@'localhost' IDENTIFIED BY '<password>';
   GRANT ALL PRIVILEGES ON KeySound.* TO '<usuario>'@'localhost';
   FLUSH PRIVILEGES;
   ```

6. Ejecutar el backend:
   ```bash
   # Windows
   .\mvnw clean spring-boot:run
   # Linux/Mac
   ./mvnw clean spring-boot:run
   ```

El backend arranca en `http://localhost:8080/api` — Swagger UI disponible en `http://localhost:8080/api/swagger-ui.html`

#### Frontend (Streaming-FrontEnd)

1. Navegar al directorio:
   ```bash
   cd ../Streaming-FrontEnd
   ```

2. Instalar dependencias:
   ```bash
   npm install
   ```

3. Arrancar el servidor de desarrollo:
   ```bash
   ng serve
   ```

4. Abrir el navegador en `http://localhost:4200`

### Anexo B: Estructura de carpetas del repositorio

El repositorio está organizado en dos módulos principales:

| Ruta | Contenido |
|---|---|
| `TFG_Streaming/` | Raíz del repositorio con `.gitignore` y `README.md` |
| `KeySound-BackEnd/` | Módulo Maven del backend Spring Boot |
| `KeySound-BackEnd/src/main/java/tfg/KeySound/` | Código fuente Java organizado en paquetes |
| `KeySound-BackEnd/src/main/resources/` | `application.properties` y `firebase-account.json` |
| `Streaming-FrontEnd/` | Proyecto Angular del frontend |
| `Streaming-FrontEnd/src/app/` | Código fuente TypeScript organizado en componentes, servicios, guards, interceptors y modelos |

### Anexo C: Endpoints principales de la API REST

La documentación completa de la API está disponible en Swagger UI. A continuación se listan los grupos de endpoints más relevantes:

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/api/auth/login` | Autenticación y obtención de token JWT | No |
| POST | `/api/auth/register` | Registro de nuevo usuario | No |
| GET | `/api/albums` | Listado de álbumes del catálogo | Sí |
| GET | `/api/albums/{id}` | Detalle de un álbum | Sí |
| POST | `/api/albums` | Crear un nuevo álbum (artista) | Sí (ARTISTA) |
| GET | `/api/canciones` | Listado de canciones | Sí |
| GET | `/api/canciones/{id}` | Detalle de una canción | Sí |
| GET | `/api/artistas` | Listado de artistas | Sí |
| GET | `/api/artistas/{id}` | Perfil de un artista | Sí |
| GET | `/api/playlists` | Playlists del usuario autenticado | Sí |
| POST | `/api/playlists` | Crear una playlist | Sí |
| POST | `/api/playlists/{id}/canciones` | Añadir canción a una playlist | Sí |
| GET | `/api/favoritos` | Favoritos del usuario | Sí |
| POST | `/api/favoritos/{cancionId}` | Marcar canción como favorita | Sí |
| DELETE | `/api/favoritos/{cancionId}` | Eliminar de favoritos | Sí |
| GET | `/api/estadisticas` | Estadísticas de escucha del usuario | Sí |
| GET | `/api/home` | Contenido de la página principal | Sí |
| POST | `/api/firebase/upload` | Subir fichero a Firebase Storage | Sí (ARTISTA) |
| GET | `/api/usuarios/perfil` | Perfil del usuario autenticado | Sí |
| PUT | `/api/usuarios/perfil` | Actualizar perfil del usuario | Sí |
