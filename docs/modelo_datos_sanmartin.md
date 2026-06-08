# Modelo de Datos - IEP Continental Americano - Backend Java

> Base de datos: **MongoDB** | AplicaciÃ³n: Spring Boot 3.2.5 + Java 17  
> Generado a partir de los modelos Java del proyecto `sanmartin-newbackend`

---

## 1. Modelo Conceptual

Representa las entidades principales y sus relaciones sin detalles tÃ©cnicos.

```mermaid
erDiagram
    INSTITUCION ||--o{ ANIO_ACADEMICO : "tiene"
    INSTITUCION ||--o{ NIVEL_GRADO : "define"
    INSTITUCION ||--o{ ASIGNATURA : "imparte"
    ANIO_ACADEMICO ||--o{ AULA : "contiene"
    NIVEL_GRADO ||--o{ AULA : "agrupa"
    AULA ||--o{ MATRICULA : "recibe"
    ESTUDIANTE ||--o{ MATRICULA : "se matricula"
    ESTUDIANTE ||--o{ NOTA : "obtiene"
    ESTUDIANTE ||--o{ ASISTENCIA : "registra"
    ESTUDIANTE ||--o{ UBICACION : "reporta"
    ESTUDIANTE ||--o{ JUSTIFICACION : "presenta"
    DOCENTE ||--o{ CURSO : "dicta"
    DOCENTE ||--o{ AULA : "tutorea"
    DOCENTE ||--o{ NOTA : "registra"
    DOCENTE ||--o{ ASISTENCIA : "toma"
    DOCENTE ||--o{ EVALUACION : "crea"
    CURSO ||--o{ NOTA : "genera"
    CURSO ||--o{ ASISTENCIA : "registra"
    CURSO ||--o{ EVALUACION : "contiene"
    ASIGNATURA ||--o{ CURSO_SECCION : "se imparte"
    AULA ||--o{ CURSO_SECCION : "aloja"
    PADRE ||--o{ ESTUDIANTE : "es apoderado de"
    USUARIO ||--o{ NOTIFICACION : "recibe"
    USUARIO ||--o{ MENSAJE : "envia"
    USUARIO ||--o{ CONVERSACION : "participa"
    USUARIO ||--o{ EVENTO : "crea"
    USUARIO ||--o{ UBICACION : "reporta"
    CONVERSACION ||--o{ MENSAJE : "contiene"
```

### Entidades Principales
| Entidad | DescripciÃ³n |
|---------|-------------|
| **InstituciÃ³n** | IEP Continental Americano - configuracion global del colegio |
| **AÃ±o AcadÃ©mico** | Ciclo escolar (ej: 2025) con bimestres y fechas importantes |
| **Nivel de Grado** | 1Â° a 5Â° Primaria, 1Â° a 5Â° Secundaria |
| **Aula** | Grupo-SecciÃ³n-Shift (ej: 1Â° Primaria A - MaÃ±ana) |
| **Estudiante** | Alumno matriculado con datos personales, mÃ©dicos y acadÃ©micos |
| **Docente** | Profesor con datos profesionales, contrato y certificaciones |
| **Padre** | Apoderado con preferencias de notificaciÃ³n y documentos |
| **Usuario** | Sistema de autenticaciÃ³n unificado (JWT) con roles |
| **Curso** | Asignatura dictada en un grado/secciÃ³n especÃ­fica |
| **SecciÃ³n de Curso** | Instancia de asignatura en un aula concreta (horario, recursos) |
| **MatrÃ­cula** | InscripciÃ³n de estudiante en aula para un aÃ±o acadÃ©mico |
| **Nota** | Registro bimestral de calificaciones por estudiante y curso |
| **EvaluaciÃ³n** | Prueba, tarea o proyecto dentro de un curso y bimestre |
| **Asistencia** | Registro diario de presencia/ausencia por estudiante y curso |
| **JustificaciÃ³n** | Solicitud de excusa por inasistencia con documentos adjuntos |
| **Evento** | Actividades, reuniones, exÃ¡menes y dÃ­as festivos escolares |
| **UbicaciÃ³n** | Rastreo GPS en tiempo real de estudiantes/padres |
| **ConversaciÃ³n** | Chat entre usuarios (directo, grupo o soporte) |
| **Mensaje** | Mensaje individual dentro de una conversaciÃ³n |
| **NotificaciÃ³n** | Alertas push/email para padres, docentes y estudiantes |

---

## 2. Modelo LÃ³gico

Representa atributos, tipos de datos, claves e Ã­ndices. En MongoDB las "tablas" son **colecciones** y las filas son **documentos JSON**.

```mermaid
erDiagram
    institutions {
        ObjectId _id PK
        string name
        string code UK
        string logo
        Address address
        string phone
        string email
        string website
        ObjectId director FK
        InstitutionEvaluationSystem evaluationSystem
        GradeScale gradeScale
        string[] shifts
        ShiftSchedules shiftSchedules
        AcademicLevels academicLevels
        int maxSectionsPerGrade
        int defaultClassroomCapacity
        string[] evaluationTypes
        EvaluationWeights defaultEvaluationWeights
        boolean isActive
        date createdAt
        date updatedAt
    }

    users {
        ObjectId _id PK
        string firstName
        string lastName
        string email UK
        string dni UK
        string password
        string phone
        string role IX
        string photo
        Address address
        boolean isActive
        boolean isVerified
        date lastLogin
        string studentProfile FK
        ChildReference[] children
        string[] permissions
        PushToken[] pushTokens
        UserSettings settings
        Coordinates lastKnownLocation
        date lastLocationUpdate
        boolean isOnline
        date lastActive
        string passwordResetToken
        date passwordResetExpires
        int loginAttempts
        date lockUntil
        date createdAt
        date updatedAt
    }

    students {
        ObjectId _id PK
        string firstName TI
        string lastName TI
        string dni UK
        string email UK
        string password
        string phone
        date birthDate
        string gender
        string photo
        Address address
        string gradeLevel
        string section
        string studentCode UK
        string enrollmentNumber UK
        Guardian[] guardians
        MedicalInfo medicalInfo
        StudentDocuments documents
        string status
        boolean isActive
        ObjectId userId FK
        date createdAt
        date updatedAt
    }

    teachers {
        ObjectId _id PK
        string firstName TI
        string lastName TI
        string dni UK
        string email UK
        string password
        string phone
        string secondaryPhone
        date birthDate
        string gender
        string photo
        Address address
        string employeeCode UK
        string specialty
        string academicDegree
        string professionalTitle
        Certification[] certifications
        string contractType
        date contractStartDate
        date contractEndDate
        double salary
        BankAccount bankAccount
        TeacherDocuments documents
        boolean isActive
        ObjectId userId FK
        ObjectId institution FK
        date createdAt
        date updatedAt
    }

    parents {
        ObjectId _id PK
        string firstName TI
        string lastName TI
        string dni UK
        string email UK
        string password
        string phone
        string secondaryPhone
        date birthDate
        string gender
        string photo
        Address address
        string occupation
        string workplace
        string workPhone
        Guardian[] children
        NotificationPreferences notifications
        ParentDocuments documents
        PushToken[] pushTokens
        boolean isActive
        boolean isVerified
        date lastLogin
        date lastActive
        boolean isOnline
        ObjectId userId FK
        string internalNotes
        date createdAt
        date updatedAt
    }

    academicyears {
        ObjectId _id PK
        ObjectId institution FK
        int year
        string name
        date startDate
        date endDate
        AcademicPeriod[] periods
        ImportantDate[] importantDates
        boolean isCurrent IX
        string status
        AcademicYearStats stats
        boolean isActive
        date createdAt
        date updatedAt
    }

    gradelevels {
        ObjectId _id PK
        ObjectId institution FK
        string name
        string shortName
        int level
        string type
        int order
        string description
        boolean isActive
        date createdAt
        date updatedAt
    }

    subjects {
        ObjectId _id PK
        ObjectId institution FK
        string name
        string code
        string description
        ObjectId[] gradeLevels FK
        string applicableTo
        int hoursPerWeek
        boolean isRequired
        string area
        EvaluationWeights defaultWeights
        Competency[] competencies
        string color
        string icon
        int order
        boolean isActive
        date createdAt
        date updatedAt
    }

    classrooms {
        ObjectId _id PK
        ObjectId gradeLevel FK
        ObjectId academicYear FK
        string section
        string shift
        ObjectId tutor FK
        int capacity
        ClassroomLocation location
        ClassroomStats stats
        boolean isActive
        date createdAt
        date updatedAt
    }

    coursesections {
        ObjectId _id PK
        ObjectId subject FK
        ObjectId classroom FK
        ObjectId teacher FK
        ObjectId academicYear FK
        Schedule[] schedule
        EvaluationWeights evaluationWeights
        PeriodEvaluation[] periodEvaluations
        CourseSectionStats stats
        Resource[] resources
        ObjectId[] students FK
        boolean isActive
        date createdAt
        date updatedAt
    }

    courses {
        ObjectId _id PK
        string name
        string code UK
        string description
        string gradeLevel
        string section
        ObjectId teacher FK
        ObjectId[] students FK
        Schedule[] schedule
        EvaluationWeights evaluationWeights
        int academicYear
        string period
        boolean isActive
        date createdAt
        date updatedAt
    }

    enrollments {
        ObjectId _id PK
        ObjectId student FK
        ObjectId classroom FK
        ObjectId academicYear FK
        date enrollmentDate
        string enrollmentNumber UK
        string status
        date statusDate
        string statusReason
        string enrollmentType
        StatusHistoryEntry[] statusHistory
        EnrollmentDocuments documents
        ObjectId previousClassroom FK
        string previousSchool
        string observations
        ObjectId enrolledBy FK
        boolean isActive
        date createdAt
        date updatedAt
    }

    grades {
        ObjectId _id PK
        ObjectId student FK
        ObjectId course FK
        int bimester
        int academicYear
        ScoreEntry[] scores
        double average
        string status
        date closedAt
        string closedBy
        date publishedAt
        ObjectId teacher FK
        date createdAt
        date updatedAt
    }

    attendances {
        ObjectId _id PK
        ObjectId student FK
        ObjectId course FK
        ObjectId teacher FK
        date date IX
        string status
        string arrivalTime
        string observations
        ObjectId justification FK
        date createdAt
        date updatedAt
    }

    evaluations {
        ObjectId _id PK
        ObjectId course FK
        ObjectId teacher FK
        string name
        string type
        int bimester
        double maxGrade
        double weight
        date date
        string description
        int academicYear
        boolean isActive
        int order
        date createdAt
        date updatedAt
    }

    events {
        ObjectId _id PK
        string title
        string date IX
        string time
        string type IX
        string description
        string location
        string participants
        boolean notifyStudents
        boolean notifyParents
        boolean notifyTeachers
        ObjectId createdBy FK
        boolean isActive IX
        date createdAt
        date updatedAt
    }

    justifications {
        ObjectId _id PK
        ObjectId student FK
        ObjectId parent FK
        date[] dates
        string reason
        string observations
        DocumentFile[] documents
        ObjectId[] coursesAffected FK
        string status IX
        ObjectId reviewedBy FK
        date reviewedAt
        string reviewNote
        date createdAt
        date updatedAt
    }

    locations {
        ObjectId _id PK
        ObjectId user FK IX
        Coordinates coordinates
        DeviceInfo deviceInfo
        string sessionStatus
        string updateType
        Address address
        double batteryLevel
        string networkType
        date clientTimestamp
        date createdAt IX
        date updatedAt
    }

    conversations {
        ObjectId _id PK
        ObjectId[] participants FK
        string type
        string name
        LastMessage lastMessage
        map unreadCount
        boolean isActive
        ConversationMetadata metadata
        date createdAt
        date updatedAt
    }

    messages {
        ObjectId _id PK
        ObjectId conversation FK
        ObjectId sender FK
        string content
        string type
        Attachment[] attachments
        ReadReceipt[] readBy
        boolean isDeleted
        date createdAt
        date updatedAt
    }

    notifications {
        ObjectId _id PK
        ObjectId recipient FK
        string title
        string message
        string type
        NotificationData data
        boolean isRead
        date readAt
        date expiresAt
        date createdAt
        date updatedAt
    }

    users ||--o{ students : "studentProfile"
    users ||--o{ teachers : "userId"
    users ||--o{ parents : "userId"
    institutions ||--o{ academicyears : "institution"
    institutions ||--o{ gradelevels : "institution"
    institutions ||--o{ subjects : "institution"
    institutions ||--o{ teachers : "institution"
    academicyears ||--o{ classrooms : "academicYear"
    academicyears ||--o{ coursesections : "academicYear"
    academicyears ||--o{ enrollments : "academicYear"
    gradelevels ||--o{ classrooms : "gradeLevel"
    gradelevels ||--o{ subjects : "gradeLevels"
    classrooms ||--o{ coursesections : "classroom"
    classrooms ||--o{ enrollments : "classroom"
    subjects ||--o{ coursesections : "subject"
    students ||--o{ enrollments : "student"
    students ||--o{ grades : "student"
    students ||--o{ attendances : "student"
    students ||--o{ justifications : "student"
    teachers ||--o{ courses : "teacher"
    teachers ||--o{ grades : "teacher"
    teachers ||--o{ attendances : "teacher"
    teachers ||--o{ evaluations : "teacher"
    teachers ||--o{ coursesections : "teacher"
    courses ||--o{ grades : "course"
    courses ||--o{ attendances : "course"
    courses ||--o{ evaluations : "course"
    parents ||--o{ justifications : "parent"
    users ||--o{ events : "createdBy"
    users ||--o{ locations : "user"
    users ||--o{ conversations : "participants"
    users ||--o{ messages : "sender"
    users ||--o{ notifications : "recipient"
    conversations ||--o{ messages : "conversation"
```

### Leyenda de SÃ­mbolos
| SÃ­mbolo | Significado |
|---------|-------------|
| `PK` | Primary Key (`_id` de MongoDB) |
| `FK` | Foreign Key (referencia ObjectId a otra colecciÃ³n) |
| `UK` | Unique Key (Ã­ndice Ãºnico) |
| `IX` | Ãndice simple o compuesto |
| `TI` | Text Index (bÃºsqueda full-text) |

---

## 3. Modelo FÃ­sico

Representa la implementaciÃ³n exacta en MongoDB: colecciones, documentos embebidos, referencias, Ã­ndices y tipos de datos nativos.

### Estrategia de Modelado Documental

| PatrÃ³n | AplicaciÃ³n | JustificaciÃ³n |
|--------|-----------|---------------|
| **Embebido** | Address, MedicalInfo, Guardian, NotificationPreferences, ScoreEntry, Schedule | Datos de alta cohesiÃ³n, consulta conjunta, baja cardinalidad |
| **Referencia** | Studentâ†”Course, Teacherâ†”CourseSection, Parentâ†”Student | Alta cardinalidad, relaciones N:M, necesidad de independencia |
| **Array de Referencias** | `Course.students[]`, `Classroom.enrollments[]` | RelaciÃ³n 1:N donde el "1" necesita listar los "N" |
| **Map/Diccionario** | `Conversation.unreadCount` | Contadores por usuario con acceso O(1) |
| **Polimorfismo** | `User.role` (administrativo/docente/estudiante/padre) | UnificaciÃ³n de autenticaciÃ³n con perfiles especializados en colecciones separadas |

### Ãndices Clave

| ColecciÃ³n | Ãndice | Tipo | PropÃ³sito |
|-----------|--------|------|-----------|
| `users` | `{email: 1}` | Ãšnico | Login por email |
| `users` | `{dni: 1}` | Ãšnico, Sparse | IdentificaciÃ³n nacional |
| `users` | `{role: 1, isActive: 1}` | Compuesto | Filtrado por rol |
| `students` | `{dni: 1}` | Ãšnico | IdentificaciÃ³n del estudiante |
| `students` | `{studentCode: 1}` | Ãšnico, Sparse | CÃ³digo institucional |
| `teachers` | `{employeeCode: 1}` | Ãšnico, Sparse | CÃ³digo de docente |
| `classrooms` | `{gradeLevel: 1, academicYear: 1, section: 1, shift: 1}` | Ãšnico | Evitar duplicados de secciÃ³n |
| `grades` | `{student: 1, course: 1, bimester: 1, academicYear: 1}` | Ãšnico | Una nota por estudiante/curso/bimestre |
| `attendances` | `{student: 1, course: 1, date: 1}` | Ãšnico | Evitar doble registro de asistencia |
| `enrollments` | `{student: 1, academicYear: 1}` | Ãšnico | Una matrÃ­cula por aÃ±o |
| `messages` | `{conversation: 1, createdAt: -1}` | Compuesto | Listar mensajes de conversaciÃ³n ordenados |
| `locations` | `{user: 1, createdAt: -1}` | Compuesto | Ãšltima ubicaciÃ³n por usuario |

### Diagrama FÃ­sico (Data Modeler de MongoDB Compass)

> Abre **MongoDB Compass** â†’ Conecta a `localhost:27017/iep_continental_db` â†’ Selecciona la pestaÃ±a **Data Modeling** â†’ El diagrama se generarÃ¡ automÃ¡ticamente con las colecciones reciÃ©n pobladas por el seed script.

Las colecciones ahora contienen documentos representativos de todos los modelos Java del nuevo backend:
- `users`, `students`, `teachers`, `parents`
- `institutions`, `academicyears`, `gradelevels`, `subjects`
- `classrooms`, `coursesections`, `courses`, `enrollments`
- `grades`, `attendances`, `evaluations`, `events`
- `justifications`, `locations`
- `conversations`, `messages`, `notifications`

---

## Archivos Generados

| Archivo | DescripciÃ³n |
|---------|-------------|
| `seed_newbackend.js` | Script para poblar MongoDB con datos de prueba del nuevo backend |
| `modelo_datos_sanmartin.md` | Este documento con los 3 modelos en Mermaid |

### Instrucciones para regenerar el diagrama en MongoDB Compass

1. AsegÃºrate de que MongoDB estÃ© corriendo (`mongod`)
2. Ejecuta el seed:
   ```bash
   mongosh localhost:27017/iep_continental_db seed_newbackend.js
   ```
3. Abre **MongoDB Compass**
4. Conecta a `mongodb://localhost:27017/iep_continental_db`
5. En el panel izquierdo, haz clic en **Data Modeling**
6. Selecciona la conexiÃ³n `localhost:27017/iep_continental_db`
7. Compass generarÃ¡ automÃ¡ticamente el diagrama de entidades con campos, tipos y relaciones basado en los documentos insertados

