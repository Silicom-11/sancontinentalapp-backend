import subprocess

dot_path = r"C:\Program Files\Graphviz\bin\dot.exe"

def table(name, color, header_fill, bg, rows):
    r = f'<TABLE BORDER="2" CELLBORDER="0" CELLSPACING="0" CELLPADDING="2" BGCOLOR="{bg}" COLOR="{color}">\n'
    r += f'<TR><TD COLSPAN="3" BGCOLOR="{header_fill}"><FONT COLOR="{color}" POINT-SIZE="10"><B>{name}</B></FONT></TD></TR>\n'
    for col_name, col_type, col_tag, col_tagcolor in rows:
        nm = f'<FONT COLOR="#c62828" POINT-SIZE="8"><B>{col_name}</B></FONT>' if col_tag == 'PK' else col_name
        tag_html = f'<FONT COLOR="{col_tagcolor}" POINT-SIZE="7"><B>{col_tag}</B></FONT>' if col_tag else ''
        r += f'<TR><TD ALIGN="LEFT">{nm}</TD><TD ALIGN="RIGHT"><FONT COLOR="#888" POINT-SIZE="7">{col_type}</FONT></TD><TD>{tag_html}</TD></TR>\n'
    r += '</TABLE>'
    return r

C_BLUE  ='#1565c0';  H_BLUE  ='#bbdefb';  BG_BLUE  ='#e3f2fd'
C_GREEN ='#2e7d32';  H_GREEN ='#c8e6c9';  BG_GREEN ='#e8f5e9'
C_ORANGE='#e65100';  H_ORANGE='#ffe0b2';  BG_ORANGE='#fff3e0'
C_PINK  ='#c2185b';  H_PINK  ='#f8bbd9';  BG_PINK  ='#fce4ec'
C_TEAL  ='#00695c';  H_TEAL  ='#b2dfdb';  BG_TEAL  ='#e0f2f1'
C_PURPLE='#7b1fa2';  H_PURPLE='#e1bee7';  BG_PURPLE='#f3e5f5'
C_RED   ='#bf360c';  H_RED   ='#ffccbc';  BG_RED   ='#fbe9e7'

def f(name, typ, tag=''):
    tc = '#c62828' if tag=='PK' else ('#1565c0' if tag=='FK' else ('#2e7d32' if tag=='UK' else ('#f57c00' if tag in ('EMB','TI') else ('#7b1fa2' if tag=='IX' else '#888'))))
    typ = typ.replace('&','&amp;').replace('<','&lt;').replace('>','&gt;')
    return (name, typ, tag, tc)

nodes = {}

nodes['USUARIO'] = table('USUARIO', C_BLUE, H_BLUE, BG_BLUE, [
    f('_id','ObjectId','PK'), f('nombre','string',''), f('apellido','string',''),
    f('email','string','UK'), f('dni','string','UK'),
    f('contrasena','string',''), f('telefono','string',''),
    f('rol','string','IX'), f('foto','string',''),
    f('direccion','Emb','EMB'), f('activo','boolean',''),
    f('verificado','boolean',''), f('ultimoLogin','Instant',''),
    f('hijos','Emb[]','EMB'), f('tokensPush','Emb[]','EMB'),
    f('ajustes','Emb','EMB'), f('intentosLogin','int',''),
    f('bloqueoHasta','Instant',''),
])

nodes['ESTUDIANTE'] = table('ESTUDIANTE', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('nombre','string','TI'), f('apellido','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('telefono','string',''), f('fechaNac','LocalDate',''),
    f('genero','string',''), f('foto','string',''),
    f('direccion','Emb','EMB'), f('grado','string',''), f('seccion','string',''),
    f('codEstudiante','string','UK'), f('numMatricula','string','UK'),
    f('apoderados','Emb[]','EMB'), f('infoMedica','Emb','EMB'),
    f('documentos','Emb','EMB'), f('estado','string',''),
    f('activo','boolean',''), f('idUsuario','ref','FK'),
])

nodes['DOCENTE'] = table('DOCENTE', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('nombre','string','TI'), f('apellido','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('telefono','string',''), f('telefono2','string',''),
    f('fechaNac','LocalDate',''), f('genero','string',''), f('foto','string',''),
    f('direccion','Emb','EMB'), f('codEmpleado','string','UK'),
    f('especialidad','string',''), f('gradoAcad','string',''),
    f('tituloProf','string',''), f('certificaciones','Emb[]','EMB'),
    f('tipoContrato','string',''), f('fechaInicioContr','LocalDate',''),
    f('fechaFinContr','LocalDate',''), f('salario','double',''),
    f('cuentaBanco','Emb','EMB'), f('documentos','Emb','EMB'),
    f('activo','boolean',''), f('idUsuario','ref','FK'), f('idInstitucion','ref','FK'),
])

nodes['PADRE'] = table('PADRE', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('nombre','string','TI'), f('apellido','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('telefono','string','IX'), f('telefono2','string',''),
    f('fechaNac','LocalDate',''), f('genero','string',''), f('foto','string',''),
    f('direccion','Emb','EMB'), f('ocupacion','string',''),
    f('lugarTrabajo','string',''), f('telefonoTrab','string',''),
    f('hijos','Emb[]','EMB'), f('preferencias','Emb','EMB'),
    f('documentos','Emb','EMB'), f('tokensPush','Emb[]','EMB'),
    f('activo','boolean',''), f('verificado','boolean',''),
    f('ultimoLogin','Instant',''), f('ultimoActivo','Instant',''),
    f('enLinea','boolean',''), f('idUsuario','ref','FK'),
    f('notasInternas','string',''),
])

nodes['NIVEL_GRADO'] = table('NIVEL GRADO', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('idInstitucion','ref','FK'),
    f('nombre','string',''), f('nombreCorto','string',''),
    f('nivel','int',''), f('tipo','string',''),
    f('orden','int',''), f('descripcion','string',''),
    f('activo','boolean',''),
])

nodes['ASIGNATURA'] = table('ASIGNATURA', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('idInstitucion','ref','FK'),
    f('nombre','string',''), f('codigo','string','UK'),
    f('descripcion','string',''), f('niveles','ref[]','FK'),
    f('aplicableA','string',''), f('horasSemana','int',''),
    f('obligatoria','boolean',''), f('area','string',''),
    f('pesosDefault','Emb','EMB'), f('competencias','Emb[]','EMB'),
    f('color','string',''), f('icono','string',''),
    f('orden','int',''), f('activo','boolean',''),
])

nodes['AULA'] = table('AULA', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('idNivel','ref','FK'), f('idAnio','ref','FK'),
    f('seccion','string',''), f('turno','string',''),
    f('tutor','ref','FK'), f('capacidad','int',''),
    f('ubicacion','Emb','EMB'), f('estadisticas','Emb','EMB'),
    f('activo','boolean',''),
])

nodes['CURSO'] = table('CURSO', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('nombre','string',''),
    f('codigo','string','UK'), f('descripcion','string',''),
    f('grado','string',''), f('seccion','string',''),
    f('idDocente','ref','FK'), f('estudiantes','ref[]','FK'),
    f('horario','Emb[]','EMB'), f('pesosEval','Emb','EMB'),
    f('anioAcad','int',''), f('periodo','string',''),
    f('activo','boolean',''),
])

nodes['SECCION_CURSO'] = table('SECCION CURSO', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('idAsignatura','ref','FK'),
    f('idAula','ref','FK'), f('idDocente','ref','FK'), f('idAnio','ref','FK'),
    f('horario','Emb[]','EMB'), f('pesosEval','Emb','EMB'),
    f('evalPeriodo','Emb[]','EMB'), f('estadisticas','Emb','EMB'),
    f('recursos','Emb[]','EMB'), f('estudiantes','ref[]','FK'),
    f('activo','boolean',''),
])

nodes['NOTA'] = table('NOTA', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('idEstudiante','ref','FK'),
    f('idCurso','ref','FK'), f('bimestre','int',''),
    f('anioAcad','int',''), f('puntajes','Emb[]','EMB'),
    f('promedio','double',''), f('estado','string',''),
    f('fechaCierre','Instant',''), f('cerradoPor','string',''),
    f('fechaPublicacion','Instant',''), f('idDocente','ref','FK'),
])

nodes['ASISTENCIA'] = table('ASISTENCIA', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('idEstudiante','ref','FK'),
    f('idCurso','ref','FK'), f('idDocente','ref','FK'),
    f('fecha','Instant','IX'), f('estado','string',''),
    f('horaLlegada','string',''), f('observaciones','string',''),
    f('idJustificacion','ref','FK'),
])

nodes['EVALUACION'] = table('EVALUACION', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('idCurso','ref','FK'),
    f('idDocente','ref','FK'), f('nombre','string',''),
    f('tipo','string',''), f('bimestre','int',''),
    f('notaMax','double',''), f('peso','double',''),
    f('fecha','Instant',''), f('descripcion','string',''),
    f('anioAcad','int',''), f('activo','boolean',''),
    f('orden','int',''),
])

nodes['EVENTO'] = table('EVENTO', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('titulo','string',''),
    f('fecha','string','IX'), f('hora','string',''),
    f('tipo','string','IX'), f('descripcion','string',''),
    f('lugar','string',''), f('participantes','string',''),
    f('notifEstudiantes','boolean',''), f('notifPadres','boolean',''),
    f('notifDocentes','boolean',''), f('creadoPor','ref','FK'),
    f('activo','boolean','IX'),
])

nodes['JUSTIFICACION'] = table('JUSTIFICACION', C_TEAL, H_TEAL, BG_TEAL, [
    f('_id','ObjectId','PK'), f('idEstudiante','ref','FK'),
    f('idPadre','ref','FK'), f('fechas','Instant[]',''),
    f('motivo','string',''), f('observaciones','string',''),
    f('documentos','Emb[]','EMB'), f('cursosAfectados','ref[]','FK'),
    f('estado','string','IX'), f('revisadoPor','ref','FK'),
    f('fechaRevision','Instant',''), f('notaRevision','string',''),
])

nodes['NOTIFICACION'] = table('NOTIFICACION', C_PURPLE, H_PURPLE, BG_PURPLE, [
    f('_id','ObjectId','PK'), f('idDestinatario','ref','FK'),
    f('titulo','string',''), f('mensaje','string',''),
    f('tipo','string',''), f('datos','Emb','EMB'),
    f('leido','boolean',''), f('fechaLectura','Instant',''),
    f('fechaExpira','Instant',''),
])

nodes['SEMAFORO_KPI'] = table('SEMAFORO KPI', C_RED, H_RED, BG_RED, [
    f('_id','ObjectId','PK'), f('idEstudiante','ref','FK'),
    f('idCurso','ref','FK'), f('bimestre','int',''),
    f('anioAcad','int',''), f('estadoGlobal','string',''),
    f('colorSemaforo','string',''),
    f('promedioNotas','double',''),
    f('tasaAsistencia','double',''),
    f('tasaAprobacion','double',''),
    f('pendientesEval','int',''),
    f('pendientesJustif','int',''),
    f('alertas','Emb[]','EMB'),
    f('ultimaActualizacion','Instant',''),
    f('idUsuario','ref','FK'),
])

# Build DOT
dot = 'digraph ModeloLogico {\n'
dot += 'rankdir=LR  dpi=250  fontname="Segoe UI"  fontsize=7\n'
dot += 'node [fontname="Segoe UI", fontsize=7, shape=plaintext, margin=0]\n'
dot += 'edge [fontname="Segoe UI", fontsize=7, arrowsize=0.6, penwidth=0.9, color="#90a4ae"]\n'
dot += 'graph [bgcolor="#f8f9fa", pad=1.0, nodesep=0.3, ranksep=0.6]\n'
dot += 'splines=ortho\n\n'

for name, label in nodes.items():
    dot += f'{name} [label=<{label}>]\n'

# Relaciones
dot += '\n// === RELACIONES ===\n'
rels = [
    ('USUARIO','ESTUDIANTE','1:1','#1565c0',True),
    ('USUARIO','DOCENTE','1:1','#1565c0',True),
    ('USUARIO','PADRE','1:1','#1565c0',True),
    ('NIVEL_GRADO','AULA','1:N','',False),
    ('NIVEL_GRADO','ASIGNATURA','1:N','',False),
    ('AULA','SECCION_CURSO','1:N','',False),
    ('ASIGNATURA','SECCION_CURSO','1:N','',False),
    ('AULA','CURSO','1:N','',False),
    ('DOCENTE','CURSO','1:N','',False),
    ('DOCENTE','AULA','1:N','',False),
    ('DOCENTE','SECCION_CURSO','1:N','',False),
    ('ESTUDIANTE','NOTA','1:N','',False),
    ('ESTUDIANTE','ASISTENCIA','1:N','',False),
    ('ESTUDIANTE','JUSTIFICACION','1:N','',False),
    ('DOCENTE','NOTA','1:N','',False),
    ('DOCENTE','ASISTENCIA','1:N','',False),
    ('DOCENTE','EVALUACION','1:N','',False),
    ('CURSO','NOTA','1:N','',False),
    ('CURSO','ASISTENCIA','1:N','',False),
    ('CURSO','EVALUACION','1:N','',False),
    ('PADRE','JUSTIFICACION','1:N','',False),
    ('USUARIO','EVENTO','1:N','',False),
    ('USUARIO','NOTIFICACION','1:N','',False),
    # Semaforo KPI
    ('ESTUDIANTE','SEMAFORO_KPI','1:1','#bf360c',True),
    ('CURSO','SEMAFORO_KPI','monitorea','#bf360c',True),
    ('SECCION_CURSO','SEMAFORO_KPI','monitorea','#bf360c',False),
    ('AULA','SEMAFORO_KPI','monitorea','#bf360c',False),
    ('NOTA','SEMAFORO_KPI','alimenta','#bf360c',False),
    ('ASISTENCIA','SEMAFORO_KPI','alimenta','#bf360c',False),
    ('EVALUACION','SEMAFORO_KPI','alimenta','#bf360c',False),
    ('EVENTO','SEMAFORO_KPI','alimenta','#bf360c',False),
    ('USUARIO','SEMAFORO_KPI','consulta','#bf360c',True),
]

for a, b, lab, col, bold in rels:
    style = 'style=bold' if bold else ''
    c = f' color="{col}"' if col else ''
    dot += f'{a} -> {b} [xlabel="{lab}"{style}{c}]\n'

dot += '\nlabelloc="t"\n'
dot += 'label="MODELO LOGICO DE DATOS\\nIEP Continental Americano - Backend Java | 16 Entidades con Atributos, PK, FK, UK, IX, EMB\\nIncluye nueva entidad SEMAFORO KPI - Dashboard de Indicadores"\n'
dot += 'fontsize=17  fontname="Segoe UI Bold"  fontcolor="#1a237e"\n}\n'

with open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico.dot', 'w', encoding='utf-8') as fout:
    fout.write(dot)

subprocess.run([dot_path, '-Kdot', '-Tpng',
    r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico.dot',
    '-o', r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico_sanmartin.png',
    '-Gdpi=200'], check=True)
print('[OK] Modelo Logico regenerado con SEMAFORO KPI')
