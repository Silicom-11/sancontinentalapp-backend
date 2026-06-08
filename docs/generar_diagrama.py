import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, ConnectionPatch
import numpy as np

# Configuración de estilo MongoDB Compass
plt.rcParams['font.family'] = 'Segoe UI'
plt.rcParams['font.size'] = 8

fig, ax = plt.subplots(1, 1, figsize=(28, 20))
ax.set_xlim(0, 28)
ax.set_ylim(0, 20)
ax.axis('off')
ax.set_facecolor('#f5f6f7')
fig.patch.set_facecolor('#f5f6f7')

# Colores estilo Compass
colors = {
    'header': '#e8f5e9',
    'header_border': '#4caf50',
    'box_bg': '#ffffff',
    'box_border': '#e0e0e0',
    'field_type': '#9e9e9e',
    'ref': '#2196f3',
    'embedded': '#ff9800',
    'index': '#9c27b0',
    'pk': '#f44336'
}

def draw_collection(ax, x, y, width, height, title, fields, color_key='header'):
    """Dibuja una caja de colección estilo MongoDB Compass"""
    # Header
    header_height = 0.5
    rect_header = FancyBboxPatch((x, y + height - header_height), width, header_height,
                                  boxstyle="round,pad=0.02,rounding_size=0.1",
                                  facecolor=colors[color_key], edgecolor=colors['header_border'],
                                  linewidth=1.5, zorder=3)
    ax.add_patch(rect_header)
    ax.text(x + width/2, y + height - header_height/2, title,
            ha='center', va='center', fontsize=10, fontweight='bold', color='#1b5e20', zorder=4)

    # Body
    rect_body = FancyBboxPatch((x, y), width, height - header_height,
                                boxstyle="round,pad=0.02,rounding_size=0.1",
                                facecolor=colors['box_bg'], edgecolor=colors['box_border'],
                                linewidth=1, zorder=2)
    ax.add_patch(rect_body)

    # Fields
    line_height = 0.32
    start_y = y + height - header_height - 0.35
    for i, (name, ftype, tags) in enumerate(fields):
        fy = start_y - i * line_height
        if fy < y + 0.1:
            break
        tag_str = " ".join([f"[{t}]" for t in tags])
        color = '#333333'
        if 'PK' in tags:
            color = colors['pk']
        elif 'FK' in tags:
            color = colors['ref']
        elif 'EMB' in tags:
            color = colors['embedded']
        elif 'IX' in tags or 'UK' in tags:
            color = colors['index']

        ax.text(x + 0.15, fy, name, ha='left', va='center', fontsize=7.5,
                color=color, fontweight='bold' if 'PK' in tags else 'normal', zorder=4)
        ax.text(x + width - 0.15, fy, ftype, ha='right', va='center', fontsize=7,
                color=colors['field_type'], style='italic', zorder=4)
        if tag_str:
            ax.text(x + width/2, fy, tag_str, ha='center', va='center', fontsize=6,
                    color='#888888', zorder=4)

# Definición de colecciones y campos
# Format: (x, y, width, height, title, [(name, type, [tags])])
collections_data = [
    (0.3, 15.5, 3.8, 4.2, "users", [
        ("_id", "objectId", ["PK"]),
        ("firstName", "string", []),
        ("lastName", "string", []),
        ("email", "string", ["UK"]),
        ("dni", "string", ["UK", "IX"]),
        ("password", "string", []),
        ("phone", "string", []),
        ("role", "string", ["IX"]),
        ("photo", "string", []),
        ("address", "Address", ["EMB"]),
        ("isActive", "bool", []),
        ("isVerified", "bool", []),
        ("lastLogin", "date", []),
        ("studentProfile", "objectId", ["FK"]),
        ("children", "ChildRef[]", ["EMB"]),
        ("permissions", "string[]", []),
        ("pushTokens", "PushToken[]", ["EMB"]),
        ("settings", "UserSettings", ["EMB"]),
        ("lastKnownLocation", "Coordinates", ["EMB"]),
        ("isOnline", "bool", []),
        ("loginAttempts", "int", []),
        ("lockUntil", "date", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (4.5, 16.2, 3.6, 3.3, "students", [
        ("_id", "objectId", ["PK"]),
        ("firstName", "string", ["TI"]),
        ("lastName", "string", ["TI"]),
        ("dni", "string", ["UK"]),
        ("email", "string", ["UK"]),
        ("password", "string", []),
        ("phone", "string", []),
        ("birthDate", "date", []),
        ("gender", "string", []),
        ("photo", "string", []),
        ("address", "Address", ["EMB"]),
        ("gradeLevel", "string", []),
        ("section", "string", []),
        ("studentCode", "string", ["UK"]),
        ("enrollmentNumber", "string", ["UK"]),
        ("guardians", "Guardian[]", ["EMB"]),
        ("medicalInfo", "MedicalInfo", ["EMB"]),
        ("documents", "StudentDocs", ["EMB"]),
        ("status", "string", []),
        ("isActive", "bool", []),
        ("userId", "objectId", ["FK"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (4.5, 12.5, 3.6, 3.3, "teachers", [
        ("_id", "objectId", ["PK"]),
        ("firstName", "string", ["TI"]),
        ("lastName", "string", ["TI"]),
        ("dni", "string", ["UK"]),
        ("email", "string", ["UK"]),
        ("password", "string", []),
        ("phone", "string", []),
        ("secondaryPhone", "string", []),
        ("birthDate", "date", []),
        ("gender", "string", []),
        ("photo", "string", []),
        ("address", "Address", ["EMB"]),
        ("employeeCode", "string", ["UK"]),
        ("specialty", "string", []),
        ("academicDegree", "string", []),
        ("professionalTitle", "string", []),
        ("certifications", "Cert[]", ["EMB"]),
        ("contractType", "string", []),
        ("contractStartDate", "date", []),
        ("contractEndDate", "date", []),
        ("salary", "double", []),
        ("bankAccount", "BankAccount", ["EMB"]),
        ("documents", "TeacherDocs", ["EMB"]),
        ("isActive", "bool", []),
        ("userId", "objectId", ["FK"]),
        ("institution", "objectId", ["FK"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (0.3, 11.5, 3.8, 3.6, "parents", [
        ("_id", "objectId", ["PK"]),
        ("firstName", "string", ["TI"]),
        ("lastName", "string", ["TI"]),
        ("dni", "string", ["UK"]),
        ("email", "string", ["UK"]),
        ("password", "string", []),
        ("phone", "string", ["IX"]),
        ("secondaryPhone", "string", []),
        ("birthDate", "date", []),
        ("gender", "string", []),
        ("photo", "string", []),
        ("address", "Address", ["EMB"]),
        ("occupation", "string", []),
        ("workplace", "string", []),
        ("workPhone", "string", []),
        ("children", "Guardian[]", ["EMB"]),
        ("notifications", "NotifPrefs", ["EMB"]),
        ("documents", "ParentDocs", ["EMB"]),
        ("pushTokens", "PushToken[]", ["EMB"]),
        ("isActive", "bool", []),
        ("isVerified", "bool", []),
        ("lastLogin", "date", []),
        ("lastActive", "date", []),
        ("isOnline", "bool", []),
        ("userId", "objectId", ["FK"]),
        ("internalNotes", "string", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (8.5, 16.5, 3.4, 2.8, "institutions", [
        ("_id", "objectId", ["PK"]),
        ("name", "string", []),
        ("code", "string", ["UK"]),
        ("logo", "string", []),
        ("address", "Address", ["EMB"]),
        ("phone", "string", []),
        ("email", "string", []),
        ("website", "string", []),
        ("director", "objectId", ["FK"]),
        ("evaluationSystem", "EvalSystem", ["EMB"]),
        ("gradeScale", "GradeScale", ["EMB"]),
        ("shifts", "string[]", []),
        ("shiftSchedules", "ShiftSched", ["EMB"]),
        ("academicLevels", "AcadLevels", ["EMB"]),
        ("maxSectionsPerGrade", "int", []),
        ("defaultClassroomCapacity", "int", []),
        ("evaluationTypes", "string[]", []),
        ("defaultEvaluationWeights", "EvalWeights", ["EMB"]),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (8.5, 13.5, 3.4, 2.6, "academicyears", [
        ("_id", "objectId", ["PK"]),
        ("institution", "objectId", ["FK"]),
        ("year", "int", []),
        ("name", "string", []),
        ("startDate", "date", []),
        ("endDate", "date", []),
        ("periods", "AcadPeriod[]", ["EMB"]),
        ("importantDates", "ImpDate[]", ["EMB"]),
        ("isCurrent", "bool", ["IX"]),
        ("status", "string", []),
        ("stats", "YearStats", ["EMB"]),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (8.5, 10.8, 3.4, 2.4, "gradelevels", [
        ("_id", "objectId", ["PK"]),
        ("institution", "objectId", ["FK"]),
        ("name", "string", []),
        ("shortName", "string", []),
        ("level", "int", []),
        ("type", "string", []),
        ("order", "int", []),
        ("description", "string", []),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (8.5, 8.0, 3.4, 2.5, "subjects", [
        ("_id", "objectId", ["PK"]),
        ("institution", "objectId", ["FK"]),
        ("name", "string", []),
        ("code", "string", ["UK"]),
        ("description", "string", []),
        ("gradeLevels", "objectId[]", ["FK"]),
        ("applicableTo", "string", []),
        ("hoursPerWeek", "int", []),
        ("isRequired", "bool", []),
        ("area", "string", []),
        ("defaultWeights", "EvalWeights", ["EMB"]),
        ("competencies", "Competency[]", ["EMB"]),
        ("color", "string", []),
        ("icon", "string", []),
        ("order", "int", []),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (12.3, 16.0, 3.4, 3.2, "classrooms", [
        ("_id", "objectId", ["PK"]),
        ("gradeLevel", "objectId", ["FK"]),
        ("academicYear", "objectId", ["FK"]),
        ("section", "string", []),
        ("shift", "string", []),
        ("tutor", "objectId", ["FK"]),
        ("capacity", "int", []),
        ("location", "ClassLoc", ["EMB"]),
        ("stats", "ClassStats", ["EMB"]),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (12.3, 12.3, 3.4, 3.3, "coursesections", [
        ("_id", "objectId", ["PK"]),
        ("subject", "objectId", ["FK"]),
        ("classroom", "objectId", ["FK"]),
        ("teacher", "objectId", ["FK"]),
        ("academicYear", "objectId", ["FK"]),
        ("schedule", "Schedule[]", ["EMB"]),
        ("evaluationWeights", "EvalWeights", ["EMB"]),
        ("periodEvaluations", "PeriodEval[]", ["EMB"]),
        ("stats", "SectionStats", ["EMB"]),
        ("resources", "Resource[]", ["EMB"]),
        ("students", "objectId[]", ["FK"]),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (12.3, 8.8, 3.4, 3.0, "courses", [
        ("_id", "objectId", ["PK"]),
        ("name", "string", []),
        ("code", "string", ["UK"]),
        ("description", "string", []),
        ("gradeLevel", "string", []),
        ("section", "string", []),
        ("teacher", "objectId", ["FK"]),
        ("students", "objectId[]", ["FK"]),
        ("schedule", "Schedule[]", ["EMB"]),
        ("evaluationWeights", "EvalWeights", ["EMB"]),
        ("academicYear", "int", []),
        ("period", "string", []),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (16.1, 16.0, 3.4, 3.0, "enrollments", [
        ("_id", "objectId", ["PK"]),
        ("student", "objectId", ["FK"]),
        ("classroom", "objectId", ["FK"]),
        ("academicYear", "objectId", ["FK"]),
        ("enrollmentDate", "date", []),
        ("enrollmentNumber", "string", ["UK"]),
        ("status", "string", []),
        ("statusDate", "date", []),
        ("statusReason", "string", []),
        ("enrollmentType", "string", []),
        ("statusHistory", "StatusHist[]", ["EMB"]),
        ("documents", "EnrollDocs", ["EMB"]),
        ("previousClassroom", "objectId", ["FK"]),
        ("previousSchool", "string", []),
        ("observations", "string", []),
        ("enrolledBy", "objectId", ["FK"]),
        ("isActive", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (16.1, 12.5, 3.4, 3.0, "grades", [
        ("_id", "objectId", ["PK"]),
        ("student", "objectId", ["FK"]),
        ("course", "objectId", ["FK"]),
        ("bimester", "int", []),
        ("academicYear", "int", []),
        ("scores", "ScoreEntry[]", ["EMB"]),
        ("average", "double", []),
        ("status", "string", []),
        ("closedAt", "date", []),
        ("closedBy", "string", []),
        ("publishedAt", "date", []),
        ("teacher", "objectId", ["FK"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (16.1, 9.0, 3.4, 3.0, "attendances", [
        ("_id", "objectId", ["PK"]),
        ("student", "objectId", ["FK"]),
        ("course", "objectId", ["FK"]),
        ("teacher", "objectId", ["FK"]),
        ("date", "date", ["IX"]),
        ("status", "string", []),
        ("arrivalTime", "string", []),
        ("observations", "string", []),
        ("justification", "objectId", ["FK"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (20.0, 16.5, 3.2, 2.5, "evaluations", [
        ("_id", "objectId", ["PK"]),
        ("course", "objectId", ["FK"]),
        ("teacher", "objectId", ["FK"]),
        ("name", "string", []),
        ("type", "string", []),
        ("bimester", "int", []),
        ("maxGrade", "double", []),
        ("weight", "double", []),
        ("date", "date", []),
        ("description", "string", []),
        ("academicYear", "int", []),
        ("isActive", "bool", []),
        ("order", "int", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (20.0, 13.5, 3.2, 2.6, "events", [
        ("_id", "objectId", ["PK"]),
        ("title", "string", []),
        ("date", "string", ["IX"]),
        ("time", "string", []),
        ("type", "string", ["IX"]),
        ("description", "string", []),
        ("location", "string", []),
        ("participants", "string", []),
        ("notifyStudents", "bool", []),
        ("notifyParents", "bool", []),
        ("notifyTeachers", "bool", []),
        ("createdBy", "objectId", ["FK"]),
        ("isActive", "bool", ["IX"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (20.0, 10.5, 3.2, 2.6, "justifications", [
        ("_id", "objectId", ["PK"]),
        ("student", "objectId", ["FK"]),
        ("parent", "objectId", ["FK"]),
        ("dates", "date[]", []),
        ("reason", "string", []),
        ("observations", "string", []),
        ("documents", "DocFile[]", ["EMB"]),
        ("coursesAffected", "objectId[]", ["FK"]),
        ("status", "string", ["IX"]),
        ("reviewedBy", "objectId", ["FK"]),
        ("reviewedAt", "date", []),
        ("reviewNote", "string", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (20.0, 7.5, 3.2, 2.7, "locations", [
        ("_id", "objectId", ["PK"]),
        ("user", "objectId", ["FK", "IX"]),
        ("coordinates", "Coordinates", ["EMB"]),
        ("deviceInfo", "DeviceInfo", ["EMB"]),
        ("sessionStatus", "string", []),
        ("updateType", "string", []),
        ("address", "Address", ["EMB"]),
        ("batteryLevel", "double", []),
        ("networkType", "string", []),
        ("clientTimestamp", "date", []),
        ("createdAt", "date", ["IX"]),
        ("updatedAt", "date", [])
    ]),
    (24.0, 16.0, 3.0, 2.5, "conversations", [
        ("_id", "objectId", ["PK"]),
        ("participants", "objectId[]", ["FK"]),
        ("type", "string", []),
        ("name", "string", []),
        ("lastMessage", "LastMsg", ["EMB"]),
        ("unreadCount", "map", []),
        ("isActive", "bool", []),
        ("metadata", "ConvMeta", ["EMB"]),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (24.0, 13.0, 3.0, 2.6, "messages", [
        ("_id", "objectId", ["PK"]),
        ("conversation", "objectId", ["FK"]),
        ("sender", "objectId", ["FK"]),
        ("content", "string", []),
        ("type", "string", []),
        ("attachments", "Attach[]", ["EMB"]),
        ("readBy", "ReadRcpt[]", ["EMB"]),
        ("isDeleted", "bool", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ]),
    (24.0, 10.0, 3.0, 2.6, "notifications", [
        ("_id", "objectId", ["PK"]),
        ("recipient", "objectId", ["FK"]),
        ("title", "string", []),
        ("message", "string", []),
        ("type", "string", []),
        ("data", "NotifData", ["EMB"]),
        ("isRead", "bool", []),
        ("readAt", "date", []),
        ("expiresAt", "date", []),
        ("createdAt", "date", []),
        ("updatedAt", "date", [])
    ])
]

# Dibujar todas las colecciones
for x, y, w, h, title, fields in collections_data:
    draw_collection(ax, x, y, w, h, title, fields)

# Dibujar líneas de referencia (Foreign Keys)
# Estilo MongoDB Compass: líneas grises con puntos en los extremos
ref_lines = [
    # users -> students (studentProfile)
    ((4.1, 17.5), (4.5, 17.85)),
    # users -> teachers (userId)
    ((4.1, 14.8), (4.5, 14.6)),
    # users -> parents (userId)
    ((4.1, 13.2), (0.3, 13.8)),
    # institutions -> academicyears
    ((11.9, 17.5), (12.3, 17.2)),
    # institutions -> gradelevels
    ((11.9, 16.0), (12.3, 15.5)),
    # institutions -> subjects
    ((11.9, 14.5), (12.3, 14.2)),
    # institutions -> teachers
    ((8.5, 15.5), (8.1, 14.5)),
    # academicyears -> classrooms
    ((11.9, 15.0), (12.3, 15.5)),
    # academicyears -> coursesections
    ((11.9, 14.5), (12.3, 14.0)),
    # academicyears -> enrollments
    ((11.9, 14.0), (16.1, 15.5)),
    # gradelevels -> classrooms
    ((11.9, 12.0), (12.3, 12.5)),
    # gradelevels -> subjects
    ((11.9, 10.5), (12.3, 10.0)),
    # classrooms -> coursesections
    ((15.7, 14.5), (16.1, 14.0)),
    # classrooms -> enrollments
    ((15.7, 14.0), (16.1, 14.5)),
    # subjects -> coursesections
    ((15.7, 12.5), (16.1, 13.0)),
    # students -> enrollments
    ((8.1, 14.5), (8.5, 15.0)),
    # students -> grades
    ((8.1, 13.5), (8.5, 14.0)),
    # students -> attendances
    ((8.1, 12.5), (8.5, 13.0)),
    # students -> justifications
    ((8.1, 11.5), (8.5, 12.0)),
    # teachers -> courses
    ((8.1, 13.5), (8.5, 14.0)),
    # teachers -> grades
    ((8.1, 12.5), (8.5, 13.0)),
    # teachers -> attendances
    ((8.1, 11.5), (8.5, 12.0)),
    # teachers -> evaluations
    ((8.1, 10.5), (8.5, 11.0)),
    # teachers -> coursesections
    ((8.1, 9.5), (8.5, 10.0)),
    # courses -> grades
    ((15.7, 10.0), (16.1, 10.5)),
    # courses -> attendances
    ((15.7, 9.5), (16.1, 10.0)),
    # courses -> evaluations
    ((15.7, 9.0), (16.1, 9.5)),
    # parents -> justifications
    ((4.1, 11.0), (4.5, 11.5)),
    # users -> events
    ((4.1, 16.0), (4.5, 16.5)),
    # users -> locations
    ((4.1, 14.5), (4.5, 15.0)),
    # users -> conversations
    ((4.1, 13.0), (4.5, 13.5)),
    # users -> messages
    ((4.1, 11.5), (4.5, 12.0)),
    # users -> notifications
    ((4.1, 10.0), (4.5, 10.5)),
    # conversations -> messages
    ((27.0, 14.5), (27.0, 13.5)),
]

for (x1, y1), (x2, y2) in ref_lines:
    ax.plot([x1, x2], [y1, y2], color='#bdbdbd', linewidth=1.2, zorder=1, linestyle='-')
    ax.plot(x1, y1, 'o', color='#2196f3', markersize=4, zorder=2)
    ax.plot(x2, y2, 'o', color='#2196f3', markersize=4, zorder=2)

# Leyenda
legend_items = [
    ("PK (Primary Key)", colors['pk']),
    ("FK (Foreign Key / Ref)", colors['ref']),
    ("EMB (Embedded Document)", colors['embedded']),
    ("IX / UK / TI (Índice)", colors['index']),
]
leg_x, leg_y = 0.5, 6.8
for i, (label, color) in enumerate(legend_items):
    ax.plot(leg_x, leg_y - i*0.4, 's', color=color, markersize=8, zorder=4)
    ax.text(leg_x + 0.3, leg_y - i*0.4, label, va='center', fontsize=9, color='#333333', zorder=4)

# Título
ax.text(14, 19.5, 'Modelo Físico de Datos - IEP Continental Americano - Backend Java',
        ha='center', va='center', fontsize=18, fontweight='bold', color='#1b5e20')
ax.text(14, 19.1, 'MongoDB Document Model | 21 Colecciones | Spring Boot 3.2.5 + Java 17',
        ha='center', va='center', fontsize=11, color='#666666')

plt.tight_layout()
plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico_sanmartin.png',
            dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
print("✅ Diagrama físico generado: modelo_fisico_sanmartin.png")
