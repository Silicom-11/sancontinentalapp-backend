# Plan de Monitoreo y Mantenimiento

## Monitoreo

- Revisar logs de aplicacion en `logs/continental-americano.log`.
- Verificar disponibilidad del backend con `/api/system/health-details`.
- Monitorear consumo de memoria, uptime y procesadores disponibles desde el endpoint de salud.
- Revisar errores de autenticacion JWT y excepciones globales registradas con SLF4J/Logback.
- Revisar latencia desde la app Android usando el interceptor de OkHttp.

## Mantenimiento preventivo

- Limpieza programada de ubicaciones antiguas mediante `MaintenanceScheduler`.
- Respaldo semanal de MongoDB Atlas.
- Rotacion de `JWT_SECRET` ante sospecha de exposicion.
- Revision mensual de dependencias Maven.
- Verificacion de permisos en Cloudflare R2 y Firebase.

## Mantenimiento correctivo

- Ante errores 5xx, revisar `GlobalExceptionHandler` y logs por timestamp.
- Ante problemas de login, validar JWT, BCrypt y estado `isActive` del usuario.
- Ante problemas de GPS, revisar permisos de Android y endpoint `/api/locations`.
- Ante problemas de reportes, validar `StudentExcelReportService` y disponibilidad de memoria.

## Backups

- Base de datos: snapshot/export de MongoDB Atlas.
- Archivos: bucket Cloudflare R2.
- Codigo: repositorio Git/GitHub con ramas por avance.
