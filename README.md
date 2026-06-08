# IEP Continental Americano - Backend

Backend Java con Spring Boot para el sistema educativo IEP Continental Americano.

## Alcance

- API REST segura con JWT y Spring Security.
- Gestion de usuarios, estudiantes, docentes, padres, cursos, asistencia, notas, ubicacion GPS, justificaciones y reportes.
- Exportacion Excel institucional.
- Auditoria de seguridad y monitoreo operativo.

## Arquitectura

- `controller`: capa de entrada REST.
- `service`: reglas de negocio.
- `repository`: acceso a datos con Spring Data MongoDB.
- `model`: entidades del dominio.
- `dto`: contratos de entrada/salida.
- `security`: JWT, filtros y autorizacion por roles.

## Recursos Java

- Google Guava: cache de salud/monitoreo.
- Apache POI: reportes Excel.
- Apache Commons: normalizacion, validacion y manejo seguro de archivos.
- Logback/SLF4J: logs operativos y auditoria.
