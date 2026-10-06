# TrackFit

**Sistema web transaccional para la gestión integral de gimnasios**

Proyecto Final · SC-403 Desarrollo de Aplicaciones Web y Patrones
Universidad Fidélitas · Facultad de Ciencias de la Computación · III Cuatrimestre 2026

> **Estado del proyecto:** Avance 1 (Semana 5): planteamiento, historias de usuario, prototipo y modelo de datos. La implementación del código comienza en el Avance 2.

---

## Tabla de contenidos

1. [Descripción de la solución](#1-descripción-de-la-solución)
2. [Problema que resuelve](#2-problema-que-resuelve)
3. [Cliente potencial](#3-cliente-potencial)
4. [Usuarios meta y roles](#4-usuarios-meta-y-roles)
5. [Integrantes del equipo](#5-integrantes-del-equipo)
6. [Tecnologías previstas](#6-tecnologías-previstas)
7. [Organización del repositorio](#7-organización-del-repositorio)
8. [Acuerdo de trabajo con ramas](#8-acuerdo-de-trabajo-con-ramas)
9. [Ejecución del proyecto](#9-ejecución-del-proyecto)

---

## 1. Descripción de la solución

**TrackFit** es una aplicación web que permite administrar las principales operaciones de un gimnasio en un solo lugar:

- Gestión de clientes
- Membresías y pagos
- Actividades y clases grupales
- Reservas con control de cupos
- Rutinas de entrenamiento
- Seguimiento del progreso físico

El sistema distingue **tres niveles de membresía: Standard, Premium y VIP**. Los beneficios, las actividades disponibles y la anticipación permitida para reservar dependen del plan contratado por cada cliente.

## 2. Problema que resuelve

En gimnasios pequeños y medianos es común administrar los servicios con cuadernos, hojas de cálculo y mensajería, lo que genera:

| Área | Dificultad actual |
|---|---|
| **Membresías y pagos** | No se sabe en tiempo real quién tiene la membresía vigente, próxima a vencer o vencida; el historial de pagos es poco confiable. |
| **Reservas y actividades** | Sobreventa de cupos, conflictos de horarios y poco control de asistencia. |
| **Planes de membresía** | Aplicar manualmente los beneficios y restricciones de cada plan produce errores. |
| **Rutinas y progreso** | La información vive en papel o mensajes; el cliente no puede consultarla y el entrenador no mantiene un historial organizado. |

TrackFit centraliza y automatiza estos procesos para mejorar el control administrativo y la experiencia de los clientes.

## 3. Cliente potencial

El proyecto nace como propuesta del equipo (no de un cliente específico). Se define como cliente potencial un **gimnasio pequeño o mediano de la Gran Área Metropolitana de Costa Rica**, con aproximadamente **50 a 300 socios**, que ofrezca actividades grupales y servicios adicionales, y que hoy gestione parte de sus operaciones con procesos manuales, hojas de cálculo o herramientas generales.

## 4. Usuarios meta y roles

| Rol | Descripción | Funciones principales |
|---|---|---|
| **Administrador** | Responsable de la configuración y administración general. | Gestionar usuarios y roles, crear/modificar planes de membresía, gestionar actividades, consultar información general y generar reportes. |
| **Encargado de recepción** | Responsable de las operaciones cotidianas con los clientes. | Registrar clientes, gestionar membresías, registrar pagos, consultar reservas y controlar información de clientes. |
| **Entrenador** | Responsable de rutinas y seguimiento del progreso. | Crear rutinas, asignar ejercicios, registrar observaciones y datos de progreso. |
| **Cliente** (Standard, Premium o VIP) | Usuario final del gimnasio. | Consultar su membresía y actividades, reservar y cancelar, consultar su rutina y su progreso. |

## 5. Integrantes del equipo

**Grupo 6**

| Nombre | Usuario de GitHub |
|---|---|
| Daniel Barrientos Salas | `@Daniels2405` |
| Julianna Fonseca Rodríguez | `@julifonsecaa` |
| John Derek Jensen Arguedas | `@jjensen20153` |
| Víctor Hugo Mora Badilla | `@usuario-github` |

**Profesor:** Prof. Wilberth Molina Pérez
**Curso:** SC-403 Desarrollo de Aplicaciones Web y Patrones

## 6. Tecnologías previstas

Siguiendo la estructura y el estilo de codificación trabajados en el curso:

| Capa / Tema | Tecnología |
|---|---|
| Lenguaje y framework | Java + Spring Boot |
| Vistas | HTML5, CSS, Thymeleaf (plantillas, fragmentos y archivos de idioma) |
| Interfaz | Bootstrap |
| Persistencia | Hibernate/JPA + base de datos relacional |
| Arquitectura | MVC en capas: Controller, Service, Repository, Domain y vistas |
| Seguridad | Autenticación y autorización por roles |
| Internacionalización | Archivos de idioma |
| Control de versiones | Git y GitHub (trabajo colaborativo con ramas y pull requests) |


## 7. Organización del repositorio

La estructura base del proyecto Spring Boot (paquetes `controller`, `service`, `repository`, `domain` y plantillas Thymeleaf) se agregará al iniciar el desarrollo.

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

**Pendiente.** El proyecto aún está en fase de diseño. Las instrucciones de instalación, configuración, ejecución, script de base de datos y usuarios de prueba por rol se agregarán a partir del Avance 2.



---

<sub>TrackFit · Grupo 6 · Universidad Fidélitas · III Cuatrimestre 2026</sub>
