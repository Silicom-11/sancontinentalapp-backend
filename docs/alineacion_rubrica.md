# Alineacion con Rubrica - Backend IEP Continental Americano

## Unidad 1: Planificacion y analisis

- Proyecto definido como sistema de gestion educativa para la IEP Continental Americano.
- Lean Canvas, BPMN y Gantt se mantienen en `sanmartin-ultrawaoz`.
- Requerimientos funcionales cubiertos por modulos: autenticacion, gestion academica, asistencia, calificaciones, GPS, justificaciones, mensajeria, notificaciones, reportes y administracion.

## Unidad 2: Diseno

- Modelo de datos documentado en `docs/modelo_datos_sanmartin.md`.
- Arquitectura por capas:
  - `controller`: API REST / MVC.
  - `service`: reglas de negocio.
  - `repository`: DAO usando Spring Data MongoDB.
  - `model`: documentos de MongoDB.
  - `dto`: contratos de entrada/salida.
  - `security`: autenticacion JWT y filtros.
- Diseno preparado para Android nativo Java como cliente principal. No existe dependencia de dashboard web.

## Unidad 3: Desarrollo Java

- Lenguaje: Java 17.
- Build y despliegue: Maven.
- MVC: controladores REST, servicios y modelos.
- DAO/Repository: interfaces `MongoRepository`.
- SOLID: inyeccion por constructor, responsabilidades separadas y servicios por dominio.
- Seguridad: Spring Security, JWT, BCrypt, CORS controlado y configuracion por variables de entorno.
- Librerias requeridas por rubrica:
  - Google Guava: `SystemHealthService` usa cache en memoria con `CacheBuilder`.
  - Apache POI: `StudentExcelReportService` genera reportes Excel.
  - Apache Commons: `StudentExcelReportService` usa `StringUtils` y `FileStorageService` usa `FilenameUtils`.
  - Logback/SLF4J: logging de servicios, seguridad, errores y mantenimiento.

## Unidad 4: Pruebas, despliegue y mantenimiento

- Pruebas unitarias:
  - `JwtTokenProviderTest`
  - `StudentExcelReportServiceTest`
  - `SystemHealthServiceTest`
- Despliegue:
  - `mvn clean package`
  - `java -jar target/continental-backend-1.0.0.jar`
- Monitoreo:
  - logs en `logs/continental-americano.log`
  - endpoint protegido `/api/system/health-details`
- Mantenimiento:
  - `MaintenanceScheduler` ejecuta limpieza programada de ubicaciones antiguas.
  - variables `LOCATION_RETENTION_DAYS` y `LOCATION_CLEANUP_CRON`.

## Configuracion segura

Las credenciales no deben guardarse en el repositorio. Para Atlas se debe configurar:

```powershell
$env:MONGODB_URI="mongodb+srv://<usuario>:<password>@cluster0.nombk2e.mongodb.net/iep_continental_db?retryWrites=true&w=majority&appName=Cluster0"
$env:JWT_SECRET="clave_larga_de_32_o_mas_caracteres"
mvn spring-boot:run
```
