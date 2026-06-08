import subprocess

dot_path = r"C:\Program Files\Graphviz\bin\dot.exe"

# ============================================================
# MODELO CONCEPTUAL - 17 Entidades (sin institutions, enrollments, conversations, messages)
# ============================================================
conceptual = r'''
digraph ModeloConceptual {
    rankdir=TB  dpi=300
    fontname="Segoe UI"  fontsize=10
    node [fontname="Segoe UI", fontsize=10, shape=box, style="rounded,filled", penwidth=2.0, margin="0.25,0.15"]
    edge [fontname="Segoe UI", fontsize=8, arrowsize=0.8, penwidth=1.2, color="#78909c"]
    graph [bgcolor="#fafbfc", pad=1.5, nodesep=0.8, ranksep=1.0]
    newrank=true  splines=ortho

    // === ENTIDADES ===
    ANIO_ACADEMICO [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]
    NIVEL_GRADO   [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]
    ASIGNATURA    [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]
    AULA          [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]
    SECCION_CURSO [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]
    MATRICULA     [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]

    USUARIO       [color="#1565c0", fillcolor="#e3f2fd", fontcolor="#1565c0", shape=rectangle]
    ESTUDIANTE    [color="#2e7d32", fillcolor="#e8f5e9", fontcolor="#2e7d32"]
    DOCENTE       [color="#2e7d32", fillcolor="#e8f5e9", fontcolor="#2e7d32"]
    PADRE         [color="#2e7d32", fillcolor="#e8f5e9", fontcolor="#2e7d32"]
    CURSO         [color="#e65100", fillcolor="#fff3e0", fontcolor="#e65100"]

    NOTA          [color="#c2185b", fillcolor="#fce4ec", fontcolor="#c2185b"]
    ASISTENCIA    [color="#c2185b", fillcolor="#fce4ec", fontcolor="#c2185b"]
    EVALUACION    [color="#c2185b", fillcolor="#fce4ec", fontcolor="#c2185b"]
    JUSTIFICACION [color="#00695c", fillcolor="#e0f2f1", fontcolor="#00695c"]
    EVENTO        [color="#c2185b", fillcolor="#fce4ec", fontcolor="#c2185b"]
    NOTIFICACION  [color="#7b1fa2", fillcolor="#ede7f6", fontcolor="#7b1fa2"]

    // === FORCE LAYERS ===
    { rank=source; ANIO_ACADEMICO NIVEL_GRADO ASIGNATURA AULA SECCION_CURSO MATRICULA }
    { rank=sink; NOTA ASISTENCIA EVALUACION JUSTIFICACION EVENTO NOTIFICACION }

    // === RELACIONES ===
    ANIO_ACADEMICO -> AULA [xlabel="1:N"]
    ANIO_ACADEMICO -> SECCION_CURSO [xlabel="1:N", constraint=false]
    ANIO_ACADEMICO -> MATRICULA [xlabel="1:N", constraint=false]
    NIVEL_GRADO -> AULA [xlabel="1:N"]
    NIVEL_GRADO -> ASIGNATURA [xlabel="1:N", constraint=false]
    AULA -> SECCION_CURSO [xlabel="1:N"]
    AULA -> MATRICULA [xlabel="1:N", constraint=false]
    ASIGNATURA -> SECCION_CURSO [xlabel="1:N"]

    USUARIO -> ESTUDIANTE [xlabel="1:1", color="#1565c0", style=bold]
    USUARIO -> DOCENTE [xlabel="1:1", color="#1565c0", style=bold]
    USUARIO -> PADRE [xlabel="1:1", color="#1565c0", style=bold]

    DOCENTE -> CURSO [xlabel="1:N"]
    DOCENTE -> AULA [xlabel="1:N"]
    DOCENTE -> SECCION_CURSO [xlabel="1:N", constraint=false]
    PADRE -> ESTUDIANTE [xlabel="1:N", color="#43a047", style=dashed]
    PADRE -> JUSTIFICACION [xlabel="1:N", constraint=false]

    ESTUDIANTE -> MATRICULA [xlabel="1:N", constraint=false]
    ESTUDIANTE -> NOTA [xlabel="1:N"]
    ESTUDIANTE -> ASISTENCIA [xlabel="1:N"]
    ESTUDIANTE -> JUSTIFICACION [xlabel="1:N"]

    DOCENTE -> NOTA [xlabel="1:N", constraint=false]
    DOCENTE -> ASISTENCIA [xlabel="1:N", constraint=false]
    DOCENTE -> EVALUACION [xlabel="1:N"]
    CURSO -> NOTA [xlabel="1:N", constraint=false]
    CURSO -> ASISTENCIA [xlabel="1:N", constraint=false]
    CURSO -> EVALUACION [xlabel="1:N", constraint=false]

    USUARIO -> EVENTO [xlabel="1:N", constraint=false]
    USUARIO -> NOTIFICACION [xlabel="1:N", constraint=false]

    labelloc="t"
    label="MODELO CONCEPTUAL DE DATOS\nIEP Continental Americano - Backend Java | 17 Entidades"
    fontsize=22  fontname="Segoe UI Bold"  fontcolor="#1a237e"
}
'''

with open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual.dot', 'w', encoding='utf-8') as f:
    f.write(conceptual)

subprocess.run([dot_path, '-Kdot', '-Tpng',
    r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual.dot',
    '-o', r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual_sanmartin.png',
    '-Gdpi=300'], check=True)
print('[OK] Conceptual')

# ============================================================
# MODELO LOGICO - 17 Entidades con atributos
# ============================================================

def table(name, color, header_fill, bg, rows):
    """Genera HTML-like label para un nodo tabla"""
    r = f'<TABLE BORDER="2" CELLBORDER="0" CELLSPACING="0" CELLPADDING="3" BGCOLOR="{bg}" COLOR="{color}">\n'
    r += f'<TR><TD COLSPAN="3" BGCOLOR="{header_fill}"><FONT COLOR="{color}" POINT-SIZE="11"><B>{name}</B></FONT></TD></TR>\n'
    for field, ftype, tag, tagcolor in rows:
        field_style = f'<FONT COLOR="#c62828" POINT-SIZE="9"><B>{field}</B></FONT>' if tag == 'PK' else field
        tag_str = f'<FONT COLOR="{tagcolor}"><B>{tag}</B></FONT>' if tag else ''
        r += f'<TR><TD>{field_style}</TD><TD>{ftype}</TD><TD>{tag_str}</TD></TR>\n'
    r += '</TABLE>'
    return r

# Colores
C_BLUE   = '#1565c0'; H_BLUE  = '#bbdefb'; BG_BLUE  = '#e3f2fd'
C_GREEN  = '#2e7d32'; H_GREEN = '#c8e6c9'; BG_GREEN = '#e8f5e9'
C_ORANGE = '#e65100'; H_ORANGE= '#ffe0b2'; BG_ORANGE= '#fff3e0'
C_PINK   = '#c2185b'; H_PINK  = '#f8bbd9'; BG_PINK  = '#fce4ec'
C_TEAL   = '#00695c'; H_TEAL  = '#b2dfdb'; BG_TEAL  = '#e0f2f1'
C_PURPLE = '#7b1fa2'; H_PURPLE= '#e1bee7'; BG_PURPLE= '#f3e5f5'

def tup(name, ftype, tag, tagcolor=''):
    return (name, ftype, tag, tagcolor)

logico = 'digraph ModeloLogico {\n'
logico += 'rankdir=LR  dpi=250  fontname="Segoe UI"  fontsize=7\n'
logico += 'node [fontname="Segoe UI", fontsize=7, shape=plaintext, margin=0]\n'
logico += 'edge [fontname="Segoe UI", fontsize=7, arrowsize=0.7, penwidth=1.0, color="#90a4ae"]\n'
logico += 'graph [bgcolor="#f8f9fa", pad=1.0, nodesep=0.4, ranksep=0.8]\n'
logico += 'splines=ortho\n\n'

nodes = {
    'USUARIO': table('USUARIO', C_BLUE, H_BLUE, BG_BLUE, [
        tup('_id','string','PK','#c62828'), tup('nombre','string','',''), tup('apellido','string','',''),
        tup('email','string','UK','#2e7d32'), tup('dni','string','UK','#2e7d32'), tup('contrasena','string','',''),
        tup('telefono','string','',''), tup('rol','string','IX','#7b1fa2'), tup('activo','boolean','',''),
        tup('ultimoLogin','date','',''), tup('hijos','Emb','',''), tup('tokensPush','Emb[]','',''),
        tup('ajustes','Emb','',''), tup('intentosLogin','int','',''),
    ]),
    'ESTUDIANTE': table('ESTUDIANTE', C_GREEN, H_GREEN, BG_GREEN, [
        tup('_id','string','PK','#c62828'), tup('nombre','string','',''), tup('apellido','string','',''),
        tup('dni','string','UK','#2e7d32'), tup('email','string','UK','#2e7d32'), tup('fechaNac','date','',''),
        tup('genero','string','',''), tup('grado','string','',''), tup('seccion','string','',''),
        tup('codEstudiante','string','UK','#2e7d32'), tup('numMatricula','string','UK','#2e7d32'),
        tup('apoderados','Emb[]','',''), tup('infoMedica','Emb','',''), tup('idUsuario','ref','FK','#1565c0'),
    ]),
    'DOCENTE': table('DOCENTE', C_GREEN, H_GREEN, BG_GREEN, [
        tup('_id','string','PK','#c62828'), tup('nombre','string','',''), tup('apellido','string','',''),
        tup('dni','string','UK','#2e7d32'), tup('email','string','UK','#2e7d32'), tup('fechaNac','date','',''),
        tup('codEmpleado','string','UK','#2e7d32'), tup('especialidad','string','',''), tup('gradoAcad','string','',''),
        tup('certificaciones','Emb[]','',''), tup('tipoContrato','string','',''), tup('salario','double','',''),
        tup('cuentaBanco','Emb','',''), tup('documentos','Emb','',''),
        tup('idUsuario','ref','FK','#1565c0'), tup('idInstitucion','ref','FK','#1565c0'),
    ]),
    'PADRE': table('PADRE', C_GREEN, H_GREEN, BG_GREEN, [
        tup('_id','string','PK','#c62828'), tup('nombre','string','',''), tup('apellido','string','',''),
        tup('dni','string','UK','#2e7d32'), tup('email','string','UK','#2e7d32'), tup('telefono','string','',''),
        tup('ocupacion','string','',''), tup('lugarTrabajo','string','',''), tup('hijos','Emb[]','',''),
        tup('preferencias','Emb','',''), tup('documentos','Emb','',''), tup('idUsuario','ref','FK','#1565c0'),
    ]),
    'ANIO_ACADEMICO': table('ANIO ACADEMICO', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idInstitucion','ref','FK','#1565c0'), tup('anio','int','',''),
        tup('nombre','string','',''), tup('fechaInicio','date','',''), tup('fechaFin','date','',''),
        tup('periodos','Emb[]','',''), tup('fechasImportantes','Emb[]','',''),
        tup('actual','boolean','IX','#7b1fa2'), tup('estado','string','',''),
    ]),
    'NIVEL_GRADO': table('NIVEL GRADO', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idInstitucion','ref','FK','#1565c0'), tup('nombre','string','',''),
        tup('nombreCorto','string','',''), tup('nivel','int','',''), tup('tipo','string','',''), tup('orden','int','',''),
    ]),
    'ASIGNATURA': table('ASIGNATURA', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idInstitucion','ref','FK','#1565c0'), tup('nombre','string','',''),
        tup('codigo','string','UK','#2e7d32'), tup('niveles','ref[]','FK','#1565c0'),
        tup('area','string','',''), tup('horasSemana','int','',''), tup('competencias','Emb[]','',''),
    ]),
    'AULA': table('AULA', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idNivel','ref','FK','#1565c0'), tup('idAnio','ref','FK','#1565c0'),
        tup('seccion','string','',''), tup('turno','string','',''), tup('tutor','ref','FK','#1565c0'),
        tup('capacidad','int','',''), tup('ubicacion','Emb','',''),
    ]),
    'CURSO': table('CURSO', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('nombre','string','',''), tup('codigo','string','UK','#2e7d32'),
        tup('grado','string','',''), tup('seccion','string','',''), tup('idDocente','ref','FK','#1565c0'),
        tup('estudiantes','ref[]','FK','#1565c0'), tup('horario','Emb[]','',''), tup('pesosEval','Emb','',''),
    ]),
    'SECCION_CURSO': table('SECCION CURSO', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idAsignatura','ref','FK','#1565c0'), tup('idAula','ref','FK','#1565c0'),
        tup('idDocente','ref','FK','#1565c0'), tup('idAnio','ref','FK','#1565c0'), tup('horario','Emb[]','',''),
        tup('pesosEval','Emb','',''), tup('evalPeriodo','Emb[]','',''), tup('recursos','Emb[]','',''),
        tup('estudiantes','ref[]','FK','#1565c0'),
    ]),
    'MATRICULA': table('MATRICULA', C_ORANGE, H_ORANGE, BG_ORANGE, [
        tup('_id','string','PK','#c62828'), tup('idEstudiante','ref','FK','#1565c0'), tup('idAula','ref','FK','#1565c0'),
        tup('idAnio','ref','FK','#1565c0'), tup('numMatricula','string','UK','#2e7d32'),
        tup('estado','string','',''), tup('tipoMatricula','string','',''), tup('documentos','Emb','',''),
    ]),
    'NOTA': table('NOTA', C_PINK, H_PINK, BG_PINK, [
        tup('_id','string','PK','#c62828'), tup('idEstudiante','ref','FK','#1565c0'), tup('idCurso','ref','FK','#1565c0'),
        tup('bimestre','int','',''), tup('anioAcad','int','',''), tup('puntajes','Emb[]','',''),
        tup('promedio','double','',''), tup('estado','string','',''), tup('idDocente','ref','FK','#1565c0'),
    ]),
    'ASISTENCIA': table('ASISTENCIA', C_PINK, H_PINK, BG_PINK, [
        tup('_id','string','PK','#c62828'), tup('idEstudiante','ref','FK','#1565c0'), tup('idCurso','ref','FK','#1565c0'),
        tup('idDocente','ref','FK','#1565c0'), tup('fecha','date','IX','#7b1fa2'), tup('estado','string','',''),
        tup('horaLlegada','string','',''), tup('idJustificacion','ref','FK','#1565c0'),
    ]),
    'EVALUACION': table('EVALUACION', C_PINK, H_PINK, BG_PINK, [
        tup('_id','string','PK','#c62828'), tup('idCurso','ref','FK','#1565c0'), tup('idDocente','ref','FK','#1565c0'),
        tup('nombre','string','',''), tup('tipo','string','',''), tup('bimestre','int','',''),
        tup('notaMax','double','',''), tup('peso','double','',''), tup('fecha','date','',''),
    ]),
    'EVENTO': table('EVENTO', C_PINK, H_PINK, BG_PINK, [
        tup('_id','string','PK','#c62828'), tup('titulo','string','',''), tup('fecha','string','IX','#7b1fa2'),
        tup('hora','string','',''), tup('tipo','string','IX','#7b1fa2'), tup('descripcion','string','',''),
        tup('lugar','string','',''), tup('notifEstudiantes','boolean','',''), tup('notifPadres','boolean','',''),
        tup('creadoPor','ref','FK','#1565c0'),
    ]),
    'JUSTIFICACION': table('JUSTIFICACION', C_TEAL, H_TEAL, BG_TEAL, [
        tup('_id','string','PK','#c62828'), tup('idEstudiante','ref','FK','#1565c0'), tup('idPadre','ref','FK','#1565c0'),
        tup('fechas','date[]','',''), tup('motivo','string','',''), tup('documentos','Emb[]','',''),
        tup('cursosAfectados','ref[]','FK','#1565c0'), tup('estado','string','IX','#7b1fa2'),
        tup('revisadoPor','ref','FK','#1565c0'),
    ]),
    'NOTIFICACION': table('NOTIFICACION', C_PURPLE, H_PURPLE, BG_PURPLE, [
        tup('_id','string','PK','#c62828'), tup('idDestinatario','ref','FK','#1565c0'), tup('titulo','string','',''),
        tup('mensaje','string','',''), tup('tipo','string','',''), tup('datos','Emb','',''),
        tup('leido','boolean','',''), tup('fechaExpira','date','',''),
    ]),
}

for name, label in nodes.items():
    logico += f'{name} [label=<{label}>]\n'

logico += '\n// === RELACIONES ===\n'
rels = [
    ('USUARIO','ESTUDIANTE','1:1',True,'#1565c0'),
    ('USUARIO','DOCENTE','1:1',True,'#1565c0'),
    ('USUARIO','PADRE','1:1',True,'#1565c0'),
    ('ANIO_ACADEMICO','AULA','1:N',False,''),
    ('ANIO_ACADEMICO','SECCION_CURSO','1:N',False,''),
    ('ANIO_ACADEMICO','MATRICULA','1:N',False,''),
    ('NIVEL_GRADO','AULA','1:N',False,''),
    ('NIVEL_GRADO','ASIGNATURA','1:N',False,''),
    ('AULA','SECCION_CURSO','1:N',False,''),
    ('AULA','MATRICULA','1:N',False,''),
    ('ASIGNATURA','SECCION_CURSO','1:N',False,''),
    ('ESTUDIANTE','MATRICULA','1:N',False,''),
    ('ESTUDIANTE','NOTA','1:N',False,''),
    ('ESTUDIANTE','ASISTENCIA','1:N',False,''),
    ('ESTUDIANTE','JUSTIFICACION','1:N',False,''),
    ('DOCENTE','CURSO','1:N',False,''),
    ('DOCENTE','AULA','1:N',False,''),
    ('DOCENTE','NOTA','1:N',False,''),
    ('DOCENTE','ASISTENCIA','1:N',False,''),
    ('DOCENTE','EVALUACION','1:N',False,''),
    ('DOCENTE','SECCION_CURSO','1:N',False,''),
    ('CURSO','NOTA','1:N',False,''),
    ('CURSO','ASISTENCIA','1:N',False,''),
    ('CURSO','EVALUACION','1:N',False,''),
    ('PADRE','JUSTIFICACION','1:N',False,''),
    ('USUARIO','EVENTO','1:N',False,''),
    ('USUARIO','NOTIFICACION','1:N',False,''),
]
for a, b, lab, bold, col in rels:
    style = ' style=bold' if bold else ''
    c = f' color="{col}"' if col else ''
    logico += f'{a} -> {b} [xlabel="{lab}"{style}{c}]\n'

logico += '\nlabelloc="t"\n'
logico += 'label="MODELO LOGICO DE DATOS\\nIEP Continental Americano - Backend Java | 17 Entidades con Atributos, PK, FK, UK"\n'
logico += 'fontsize=20  fontname="Segoe UI Bold"  fontcolor="#1a237e"\n'
logico += '}\n'

with open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico.dot', 'w', encoding='utf-8') as f:
    f.write(logico)

subprocess.run([dot_path, '-Kdot', '-Tpng',
    r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico.dot',
    '-o', r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico_sanmartin.png',
    '-Gdpi=200'], check=True)
print('[OK] Logico')

# ============================================================
# MODELO FISICO - Graphviz DOT con nombres de colecciones MongoDB
# ============================================================
fisico = 'digraph ModeloFisico {\n'
fisico += 'rankdir=TB  dpi=300  fontname="Segoe UI"  fontsize=8\n'
fisico += 'node [fontname="Segoe UI", fontsize=7, shape=box, style="rounded,filled", penwidth=2.0, margin="0.3,0.15"]\n'
fisico += 'edge [fontname="Segoe UI", fontsize=7, arrowsize=0.7, penwidth=1.0, color="#90a4ae"]\n'
fisico += 'graph [bgcolor="#fafbfc", pad=1.2, nodesep=0.6, ranksep=0.8]\n'
fisico += 'newrank=true  splines=ortho\n\n'

colls = [
    ('users','USUARIO',C_BLUE,BG_BLUE),
    ('students','ESTUDIANTE',C_GREEN,BG_GREEN),
    ('teachers','DOCENTE',C_GREEN,BG_GREEN),
    ('parents','PADRE',C_GREEN,BG_GREEN),
    ('academicyears','ANIO ACADEMICO',C_ORANGE,BG_ORANGE),
    ('gradelevels','NIVEL GRADO',C_ORANGE,BG_ORANGE),
    ('subjects','ASIGNATURA',C_ORANGE,BG_ORANGE),
    ('classrooms','AULA',C_ORANGE,BG_ORANGE),
    ('courses','CURSO',C_ORANGE,BG_ORANGE),
    ('coursesections','SECCION CURSO',C_ORANGE,BG_ORANGE),
    ('enrollments','MATRICULA',C_ORANGE,BG_ORANGE),
    ('grades','NOTA',C_PINK,BG_PINK),
    ('attendances','ASISTENCIA',C_PINK,BG_PINK),
    ('evaluations','EVALUACION',C_PINK,BG_PINK),
    ('events','EVENTO',C_PINK,BG_PINK),
    ('justifications','JUSTIFICACION',C_TEAL,BG_TEAL),
    ('notifications','NOTIFICACION',C_PURPLE,BG_PURPLE),
]

for coll, label, col, fill in colls:
    fisico += f'{coll} [label="{label}", color="{col}", fillcolor="{fill}", fontcolor="{col}"]\n'

# Relaciones
fisico += '\n// === REFERENCIAS ENTRE COLECCIONES ===\n'
refs = [
    ('users','students','userId'),('users','teachers','userId'),('users','parents','userId'),
    ('academicyears','classrooms','academicYear'),('academicyears','coursesections','academicYear'),
    ('academicyears','enrollments','academicYear'),
    ('gradelevels','classrooms','gradeLevel'),('gradelevels','subjects','gradeLevels'),
    ('classrooms','coursesections','classroom'),('classrooms','enrollments','classroom'),
    ('subjects','coursesections','subject'),
    ('students','enrollments','student'),('students','grades','student'),
    ('students','attendances','student'),('students','justifications','student'),
    ('teachers','courses','teacher'),('teachers','grades','teacher'),
    ('teachers','attendances','teacher'),('teachers','evaluations','teacher'),
    ('teachers','coursesections','teacher'),
    ('courses','grades','course'),('courses','attendances','course'),('courses','evaluations','course'),
    ('parents','justifications','parent'),
    ('users','events','createdBy'),('users','notifications','recipient'),
]
for a, b, field in refs:
    fisico += f'{a} -> {b} [xlabel="{field}"]\n'

fisico += '\nlabelloc="t"\n'
fisico += 'label="MODELO FISICO DE DATOS\\nMongoDB Document Model | 17 Colecciones | Spring Boot 3.2.5 + Java 17"\n'
fisico += 'fontsize=20  fontname="Segoe UI Bold"  fontcolor="#1a237e"\n'
fisico += '}\n'

with open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico.dot', 'w', encoding='utf-8') as f:
    f.write(fisico)

subprocess.run([dot_path, '-Kdot', '-Tpng',
    r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico.dot',
    '-o', r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico_sanmartin.png',
    '-Gdpi=300'], check=True)
print('[OK] Fisico')

print('\n=== LOS 3 DIAGRAMAS GENERADOS (17 entidades) ===')
print('1. modelo_conceptual_sanmartin.png (300 DPI)')
print('2. modelo_logico_sanmartin.png (200 DPI)')
print('3. modelo_fisico_sanmartin.png (300 DPI)')
