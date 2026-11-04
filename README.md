# TrackFit

**Sistema web transaccional para la gestión integral de gimnasios**

Proyecto Final · SC-403 Desarrollo de Aplicaciones Web y Patrones
Universidad Fidélitas · III Cuatrimestre 2026

> **Estado del proyecto:** en desarrollo (posterior al Avance 1). Ya están implementados los módulos de usuarios, clientes, planes, membresías, pagos, actividades, horarios, reservas y rutinas. **Pendientes:** inicio de sesión y control de acceso por roles, cancelación de reservas, seguimiento del progreso, reportes e internacionalización en inglés. Ver el [estado por historia de usuario](#10-estado-de-las-historias-de-usuario).

---

## Tabla de contenidos

1. [Descripción de la solución](#1-descripción-de-la-solución)
2. [Problema que resuelve](#2-problema-que-resuelve)
3. [Cliente potencial](#3-cliente-potencial)
4. [Usuarios meta y roles](#4-usuarios-meta-y-roles)
5. [Integrantes del equipo](#5-integrantes-del-equipo)
6. [Tecnologías utilizadas](#6-tecnologías-utilizadas)
7. [Organización del repositorio](#7-organización-del-repositorio)
8. [Acuerdo de trabajo con ramas](#8-acuerdo-de-trabajo-con-ramas)
9. [Ejecución del proyecto](#9-ejecución-del-proyecto)
10. [Estado de las historias de usuario](#10-estado-de-las-historias-de-usuario)
11. [Modelo de datos](#11-modelo-de-datos)
12. [Reglas de negocio implementadas](#12-reglas-de-negocio-implementadas)
13. [Pendientes y limitaciones conocidas](#13-pendientes-y-limitaciones-conocidas)

---

## 1. Descripción de la solución

**TrackFit** es una aplicación web que permite administrar las principales operaciones de un gimnasio en un solo lugar:

- Gestión de clientes
- Membresías y pagos
- Actividades y clases grupales
- Reservas con control de cupos
- Rutinas de entrenamiento
- Seguimiento del progreso físico (modelado en base de datos; pantallas pendientes)

El sistema distingue **tres niveles de membresía: Standard, Premium y VIP**. Los beneficios, las actividades disponibles y la anticipación permitida para reservar dependen del plan contratado por cada cliente.

## 2. Problema que resuelve

En gimnasios pequeños y medianos es común administrar los servicios con cuadernos, hojas de cálculo y mensajería, lo que genera:

| Área                       | Dificultad actual                                                                                                                |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| **Membresías y pagos**     | No se sabe en tiempo real quién tiene la membresía vigente, próxima a vencer o vencida; el historial de pagos es poco confiable. |
| **Reservas y actividades** | Sobreventa de cupos, conflictos de horarios y poco control de asistencia.                                                        |
| **Planes de membresía**    | Aplicar manualmente los beneficios y restricciones de cada plan produce errores.                                                 |
| **Rutinas y progreso**     | La información vive en papel o mensajes; el cliente no puede consultarla y el entrenador no mantiene un historial organizado.    |

TrackFit centraliza y automatiza estos procesos para mejorar el control administrativo y la experiencia de los clientes.

## 3. Cliente potencial

El proyecto nace como propuesta del equipo (no de un cliente específico). Se define como cliente potencial un **gimnasio pequeño o mediano de la Gran Área Metropolitana de Costa Rica**, con aproximadamente **50 a 300 socios**, que ofrezca actividades grupales y servicios adicionales, y que hoy gestione parte de sus operaciones con procesos manuales, hojas de cálculo o herramientas generales.

## 4. Usuarios meta y roles

| Rol                                   | Descripción                                                 | Funciones principales                                                                                                                     |
| ------------------------------------- | ----------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Administrador**                     | Responsable de la configuración y administración general.   | Gestionar usuarios y roles, crear/modificar planes de membresía, gestionar actividades, consultar información general y generar reportes. |
| **Encargado de recepción**            | Responsable de las operaciones cotidianas con los clientes. | Registrar clientes, gestionar membresías, registrar pagos, consultar reservas y controlar información de clientes.                        |
| **Entrenador**                        | Responsable de rutinas y seguimiento del progreso.          | Crear rutinas, asignar ejercicios, registrar observaciones y datos de progreso.                                                           |
| **Cliente** (Standard, Premium o VIP) | Usuario final del gimnasio.                                 | Consultar su membresía y actividades, reservar y cancelar, consultar su rutina y su progreso.                                             |

En la base de datos los roles son `ADMINISTRADOR`, `RECEPCION`, `ENTRENADOR` y `CLIENTE`.

## 5. Integrantes del equipo

**Grupo 6**

| Nombre                     | Usuario de GitHub |
| -------------------------- | ----------------- |
| Daniel Barrientos Salas    | `@Daniels2405`    |
| Julianna Fonseca Rodríguez | `@julifonsecaa`   |
| John Derek Jensen Arguedas | `@jjensen20153`   |
| Víctor Hugo Mora Badilla   | `@vhmora-mb`      |

**Profesor:** Prof. Wilberth Molina Pérez
**Curso:** SC-403 Desarrollo de Aplicaciones Web y Patrones

## 6. Tecnologías utilizadas

| Capa / Tema                | Tecnología                                                                     |
| -------------------------- | ------------------------------------------------------------------------------ |
| Lenguaje                   | Java 21                                                                        |
| Framework                  | Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Bean Validation)           |
| Vistas                     | HTML5, CSS, JavaScript, Thymeleaf (plantillas y fragmentos)                    |
| Interfaz                   | Bootstrap 5.3.8, Font Awesome 7.3.0, jQuery 4.0.0, Popper 2.11.7 (vía WebJars) |
| Persistencia               | Hibernate/JPA + MySQL 8.0.16 o superior (conector `mysql-connector-j`)         |
| Productividad              | Lombok, Spring Boot DevTools                                                   |
| Internacionalización       | `messages.properties` (español) y selector de idioma por parámetro `lang`      |
| Almacenamiento de imágenes | Firebase Admin SDK 9.10.0 (dependencia incluida; sin uso aún en el código)     |
| Arquitectura               | MVC en capas: Controller, Service, Repository, Domain y vistas                 |
| Construcción               | Maven (el artefacto generado se llama `app.jar`)                               |
| Control de versiones       | Git y GitHub (trabajo colaborativo con ramas y pull requests)                  |

## 7. Organización del repositorio

```
TrackFit/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/TrackFit/
    │   ├── TrackFitApplication.java      # Clase principal
    │   ├── ProjectConfig.java            # Idioma, mensajes y resolvedor de plantillas
    │   ├── controller/                   # Actividad, Cliente, Horario, Index, Membresia,
    │   │                                 # Pago, PlanBeneficio, PlanMembresia, Reserva,
    │   │                                 # Rutina, Usuario
    │   ├── service/                      # Lógica de negocio y reglas de validación
    │   ├── repository/                   # Interfaces Spring Data JPA
    │   └── domain/                       # Entidades JPA
    └── resources/
        ├── application.properties        # Configuración (puerto, base de datos, logging)
        ├── creaTablas.sql                # Esquema, usuarios de BD y datos de demostración
        ├── messages.properties           # Textos de la interfaz (español)
        ├── firebase/                     # Credenciales de Firebase (no se versionan)
        ├── static/                       # css/, js/, img/ y fav/
        └── templates/                    # Vistas Thymeleaf por módulo:
                                          # actividad, cliente, general, horario, membresia,
                                          # pago, plan, reserva, rutina, usuario, index
```

## 8. Acuerdo de trabajo con ramas

El equipo trabaja en **un único repositorio colaborativo**, con todos los integrantes agregados como colaboradores.

1. **`main`** representa la versión estable e integrable. No se trabaja directamente sobre ella.
2. Cada funcionalidad, corrección o mejora se desarrolla en su **propia rama**:
   - `feature/autenticacion-roles`
   - `feature/gestion-membresias`
   - `fix/validacion-formulario`
3. Los **commits** deben ser significativos y descriptivos (qué se agregó, corrigió o modificó).
4. Al terminar, se publica la rama y se abre un **pull request** hacia `main`.
5. Todo pull request debe ser **revisado por al menos otro integrante** antes de aceptarse.
6. Se atienden observaciones y se resuelven conflictos antes de la unión.
7. Solo se acepta un cambio si es funcional, coherente con el proyecto y no afecta otras funcionalidades.
8. Cada integrante hace `pull` de `main` con frecuencia para evitar divergencias.

> No se suben contraseñas, llaves, tokens ni datos personales reales.

## 9. Ejecución del proyecto

### Requisitos

- JDK 21
- Maven 3.9 o superior (o el wrapper del IDE)
- MySQL 8.0.16 o superior (necesario para que se apliquen las restricciones `CHECK`)
- Acceso a una instancia MySQL (local o en la nube)

### 1. Crear la base de datos

Ejecute el script `src/main/resources/creaTablas.sql` con un usuario administrador de MySQL. El script:

- Elimina y vuelve a crear la base de datos `trackfit` (**borra los datos existentes**; usar solo en desarrollo).
- Crea los usuarios de base de datos `trackfit_app` (lectura y escritura) y `trackfit_reportes` (solo lectura).
- Crea las tablas y carga datos ficticios de demostración.

```bash
mysql -u root -p < src/main/resources/creaTablas.sql
```

### 2. Configurar la conexión

Edite `src/main/resources/application.properties` con los datos de su servidor:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trackfit
spring.datasource.username=trackfit_app
spring.datasource.password=<contraseña definida en creaTablas.sql>
```

### 3. Ejecutar

```bash
mvn spring-boot:run
```

O bien genere el JAR y ejecútelo:

```bash
mvn clean package
java -jar target/app.jar
```

La aplicación queda disponible en **http://localhost/** (el puerto configurado es el **80**; en Linux y macOS puede requerir privilegios elevados, o cambiar `server.port` a, por ejemplo, `8080`).

### Datos de demostración

El script carga 2 entrenadores, 1 administrador, 1 encargado de recepción y 15 clientes ficticios, junto con planes, membresías, pagos, actividades, horarios, reservas, rutinas y registros de progreso.

| Rol           | Usuario                                   | Contraseña        |
| ------------- | ----------------------------------------- | ----------------- |
| Administrador | `admin`                                   | `Admin2026*`      |
| Recepción     | `recepcion`                               | `Recepcion2026*`  |
| Entrenador    | `maria.solano`, `andres.campos`           | `Entrenador2026*` |
| Cliente       | `carlos.rodriguez` (y los demás clientes) | `Cliente2026*`    |

> Estas cuentas son ficticias y están almacenadas con BCrypt en el script. **Todavía no hay pantalla de inicio de sesión**, por lo que hoy no se utilizan para entrar al sistema (ver [pendientes](#13-pendientes-y-limitaciones-conocidas)).

### Rutas principales

| Ruta                                        | Módulo                                                                 |
| ------------------------------------------- | ---------------------------------------------------------------------- |
| `/`                                         | Panel de inicio con totales de planes, clientes, membresías y usuarios |
| `/usuario/listado`                          | Usuarios y roles                                                       |
| `/cliente/listado`                          | Clientes                                                               |
| `/plan/listado`                             | Planes de membresía y sus beneficios                                   |
| `/membresia/listado`, `/membresia/consulta` | Asignación y consulta de membresías                                    |
| `/pago/listado`                             | Registro e historial de pagos                                          |
| `/actividad/listado`                        | Actividades                                                            |
| `/horario/listado`                          | Horarios y cupos                                                       |
| `/reserva/disponibles`                      | Consulta de actividades disponibles y reservas                         |
| `/rutina/listado`, `/rutina/mi-rutina`      | Rutinas (entrenador) y consulta de rutina (cliente)                    |

## 10. Estado de las historias de usuario

| ID   | Historia                                       | Prioridad | Estado                                                           |
| ---- | ---------------------------------------------- | --------- | ---------------------------------------------------------------- |
| HU01 | Registrar usuarios del sistema                 | Alta      | Implementada                                                     |
| HU02 | Iniciar sesión                                 | Alta      | Pendiente                                                        |
| HU03 | Registrar y actualizar clientes                | Alta      | Implementada                                                     |
| HU04 | Crear y modificar planes de membresía          | Alta      | Implementada (incluye beneficios del plan)                       |
| HU05 | Asignar membresía a un cliente                 | Alta      | Implementada                                                     |
| HU06 | Consultar estado y vencimiento de la membresía | Alta      | Implementada                                                     |
| HU07 | Registrar pagos de membresías                  | Alta      | Implementada                                                     |
| HU08 | Consultar historial de pagos                   | Media     | Implementada (listado general de pagos)                          |
| HU09 | Crear actividades y clases                     | Alta      | Implementada                                                     |
| HU10 | Establecer horarios y cupos máximos            | Alta      | Implementada                                                     |
| HU11 | Consultar actividades disponibles              | Alta      | Implementada (filtra por plan y anticipación)                    |
| HU12 | Reservar un espacio en una actividad           | Alta      | Implementada                                                     |
| HU13 | Cancelar una reserva                           | Media     | Pendiente (no hay acción de cancelar)                            |
| HU14 | Consultar inscritos en una actividad           | Media     | Pendiente                                                        |
| HU15 | Crear rutinas de entrenamiento                 | Alta      | Implementada                                                     |
| HU16 | Consultar rutina de entrenamiento              | Alta      | Implementada                                                     |
| HU17 | Registrar datos de progreso                    | Media     | Pendiente (existe la tabla `progreso`, sin entidad ni pantallas) |
| HU18 | Consultar historial de progreso                | Media     | Pendiente                                                        |
| HU19 | Reportes de membresías, pagos y reservas       | Media     | Pendiente                                                        |
| HU20 | Control de permisos según el rol               | Alta      | Pendiente                                                        |

## 11. Modelo de datos

El esquema `trackfit` (MySQL, `utf8mb4`) contiene 16 tablas agrupadas por módulo:

| Módulo                 | Tablas                                                  |
| ---------------------- | ------------------------------------------------------- |
| Seguridad              | `rol`, `usuario`, `usuario_rol`                         |
| Membresías y pagos     | `plan_membresia`, `plan_beneficio`, `membresia`, `pago` |
| Actividades y reservas | `actividad`, `actividad_plan`, `horario`, `reserva`     |
| Entrenamiento          | `ejercicio`, `rutina`, `rutina_ejercicio`, `progreso`   |
| Marketing (opcional)   | `promocion`                                             |

Relaciones principales:

- Un **usuario** puede tener varios **roles** (`usuario_rol`); el tipo de usuario (cliente, entrenador, etc.) se define por su rol.
- Un **plan de membresía** tiene varios **beneficios** y define los días de anticipación para reservar (`anticipacion_reserva_dias`). La relación `actividad_plan` indica qué actividades permite cada plan.
- Una **membresía** asocia un cliente con un plan, con fecha de inicio, fecha de vencimiento y estado. Cada **pago** se asocia a una membresía y al usuario que lo registra.
- Una **actividad** tiene varios **horarios**, cada uno con fecha, hora de inicio y fin, cupo máximo y entrenador. Una **reserva** une a un cliente con un horario.
- Una **rutina** pertenece a un cliente y a un entrenador y contiene varios **ejercicios** (`rutina_ejercicio`) con series, repeticiones, peso y observaciones.
- El **progreso** registra, por cliente y fecha, el peso, el porcentaje de grasa y observaciones del entrenador.

Las entidades JPA (`domain/`) cubren `Usuario`, `Rol`, `PlanMembresia`, `PlanBeneficio`, `Membresia`, `Pago`, `Actividad`, `Horario`, `Reserva`, `Ejercicio`, `Rutina` y `RutinaEjercicio`.

## 12. Reglas de negocio implementadas

- **Reservas:** solo se puede reservar con una membresía vigente; se valida que existan espacios disponibles en el horario; la reserva se registra como `Confirmada`.
- **Restricciones por plan:** el cliente solo ve horarios desde hoy hasta los días de anticipación de su plan y únicamente de las actividades que su plan permite. No se muestran horarios que ya iniciaron.
- **Cupos:** cada horario tiene un cupo máximo y el sistema calcula los espacios disponibles.
- **Actividades:** el nombre es obligatorio y no puede repetirse (sin distinguir mayúsculas).
- **Usuarios:** la cédula, el nombre de usuario y el correo deben ser únicos, y todo usuario requiere al menos un rol.
- **Rutinas:** el nombre es obligatorio, las series y repeticiones deben ser válidas y un ejercicio no puede repetirse dentro de la misma rutina.
- **Validaciones:** se usa Bean Validation en las entidades y los mensajes se toman de `messages.properties`.

## 13. Pendientes y limitaciones conocidas

- **Sin autenticación ni autorización.** El proyecto aún no incluye Spring Security: no hay pantalla de login y todas las rutas son accesibles sin sesión. Al reservar o consultar rutinas, el cliente se selecciona por cédula en lugar de tomarse del usuario autenticado. Esto cubre las HU02 y HU20.
- **Contraseñas sin cifrar al guardar.** Los usuarios del script SQL tienen contraseñas en BCrypt, pero `UsuarioService.save` guarda la contraseña tal como llega del formulario. Debe cifrarse al integrar la seguridad.
- **Credenciales en el repositorio.** `application.properties` contiene una URL, usuario y contraseña de una base de datos en la nube. Deben retirarse del repositorio, **rotarse** y sustituirse por variables de entorno (ver sección 9).
- **Internacionalización incompleta.** El menú ofrece español e inglés, pero solo existe `messages.properties` (español); falta `messages_en.properties`.
- **Funcionalidades sin construir:** cancelación de reservas, consulta de inscritos por actividad, módulo de progreso físico y reportes.
- **Pruebas automatizadas:** no hay pruebas unitarias ni de integración todavía.
- **Firebase:** la dependencia y la carpeta `firebase/` están preparadas para subir imágenes, pero aún no se usan.
- **Puerto 80:** pensado para despliegue; en desarrollo local puede cambiarse a `8080`.

---

<sub>TrackFit · Grupo 6 · Universidad Fidélitas · III Cuatrimestre 2026</sub>
