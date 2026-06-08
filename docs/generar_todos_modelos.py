import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, ConnectionPatch, Circle
import numpy as np

# Configuracion global
plt.rcParams['font.family'] = 'Segoe UI'
plt.rcParams['font.size'] = 8
plt.rcParams['axes.unicode_minus'] = False

def draw_entity(ax, x, y, w, h, title, attrs, color_border, color_bg, color_header):
    """Dibuja una entidad con titulo y atributos"""
    hh = 0.4
    # Header
    hdr = FancyBboxPatch((x, y+h-hh), w, hh, boxstyle="round,pad=0.02,rounding_size=0.1",
                         facecolor=color_header, edgecolor=color_border, linewidth=2, zorder=3)
    ax.add_patch(hdr)
    ax.text(x+w/2, y+h-hh/2, title, ha='center', va='center', fontsize=10,
            fontweight='bold', color=color_border, zorder=4)
    # Body
    body = FancyBboxPatch((x, y), w, h-hh, boxstyle="round,pad=0.02,rounding_size=0.1",
                          facecolor=color_bg, edgecolor=color_border, linewidth=1.5, zorder=2)
    ax.add_patch(body)
    # Atributos
    lh = 0.26
    sy = y + h - hh - 0.28
    for i, (name, tags) in enumerate(attrs):
        fy = sy - i*lh
        if fy < y + 0.06:
            break
        col = '#333'
        if 'PK' in tags: col = '#c62828'
        elif 'FK' in tags: col = '#1565c0'
        wt = 'bold' if 'PK' in tags else 'normal'
        ax.text(x+0.12, fy, name, ha='left', va='center', fontsize=7.5, color=col, fontweight=wt, zorder=4)
        if tags:
            ax.text(x+w-0.1, fy, ' '.join(tags), ha='right', va='center', fontsize=6.5, color='#888', zorder=4)

def draw_relation(ax, x1, y1, x2, y2, label='', color='#90a4ae'):
    """Dibuja una linea de relacion con etiqueta"""
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle='-', color=color, lw=1.2, connectionstyle='arc3,rad=0.1'),
                zorder=1)
    if label:
        mx, my = (x1+x2)/2, (y1+y2)/2
        ax.text(mx, my+0.15, label, fontsize=7, color='#455a64', ha='center', va='center',
                bbox=dict(boxstyle='round,pad=0.15', facecolor='white', edgecolor='#cfd8dc', alpha=0.95), zorder=2)

def draw_zone(ax, x, y, w, h, label, color):
    """Dibuja una zona de dominio con borde punteado"""
    rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.3",
                          facecolor=color, edgecolor=color, linewidth=1.5, linestyle='--', alpha=0.12, zorder=0)
    ax.add_patch(rect)
    ax.text(x+w/2, y+h-0.2, label, ha='center', va='center', fontsize=9, fontweight='bold',
            color=color, alpha=0.5, zorder=1)

# ============================================================
# 1. MODELO CONCEPTUAL
# ============================================================
fig, ax = plt.subplots(figsize=(24, 14))
ax.set_xlim(0, 24)
ax.set_ylim(0, 14)
ax.axis('off')
ax.set_facecolor('#fafbfc')
fig.patch.set_facecolor('#fafbfc')

ax.text(12, 13.5, 'MODELO CONCEPTUAL DE DATOS', ha='center', va='center',
        fontsize=20, fontweight='bold', color='#1a237e')
ax.text(12, 13.1, 'IEP Continental Americano - Backend Java | 21 Entidades', ha='center', va='center', fontsize=11, color='#666')

# Zonas
draw_zone(ax, 0.3, 9.5, 23.4, 3.2, 'ESTRUCTURA INSTITUCIONAL', '#e65100')
draw_zone(ax, 0.3, 5.8, 23.4, 3.2, 'PERSONAS Y CURSOS', '#2e7d32')
draw_zone(ax, 0.3, 2.1, 18.0, 3.2, 'REGISTROS Y OPERACIONES', '#c2185b')
draw_zone(ax, 18.6, 2.1, 5.1, 3.2, 'COMUNICACION', '#7b1fa2')

# Entidades - fila 1 (estructura)
def ent(x,y,w,h,title,c,attrs=[]):
    cmap={'core':('#1565c0','#e3f2fd','#bbdefb'),'acad':('#e65100','#fff3e0','#ffe0b2'),
          'people':('#2e7d32','#e8f5e9','#c8e6c9'),'rec':('#c2185b','#fce4ec','#f8bbd9'),
          'comm':('#7b1fa2','#f3e5f5','#e1bee7'),'ops':('#00695c','#e0f2f1','#b2dfdb')}
    cb,bg,ch=cmap[c]
    draw_entity(ax,x,y,w,h,title,attrs,cb,bg,ch)

ent(1.0, 10.8, 2.4, 0.7, 'INSTITUCION', 'core')
ent(4.0, 10.8, 2.6, 0.7, 'ANIO_ACADEMICO', 'acad')
ent(7.2, 10.8, 2.4, 0.7, 'NIVEL_GRADO', 'acad')
ent(10.2, 10.8, 2.4, 0.7, 'ASIGNATURA', 'acad')
ent(13.2, 10.8, 2.0, 0.7, 'AULA', 'acad')
ent(15.8, 10.8, 2.6, 0.7, 'SECCION_CURSO', 'acad')
ent(19.0, 10.8, 2.4, 0.7, 'MATRICULA', 'acad')

# Entidades - fila 2 (personas)
ent(1.0, 7.1, 2.2, 0.7, 'USUARIO', 'core')
ent(3.8, 7.1, 2.4, 0.7, 'ESTUDIANTE', 'people')
ent(6.6, 7.1, 2.4, 0.7, 'DOCENTE', 'people')
ent(9.4, 7.1, 2.2, 0.7, 'PADRE', 'people')
ent(12.2, 7.1, 2.4, 0.7, 'CURSO', 'acad')

# Entidades - fila 3 (registros)
ent(1.0, 3.4, 2.2, 0.7, 'NOTA', 'rec')
ent(3.8, 3.4, 2.4, 0.7, 'ASISTENCIA', 'rec')
ent(6.6, 3.4, 2.4, 0.7, 'EVALUACION', 'rec')
ent(9.4, 3.4, 2.4, 0.7, 'JUSTIFICACION', 'ops')
ent(12.2, 3.4, 2.2, 0.7, 'EVENTO', 'rec')
ent(14.8, 3.4, 2.4, 0.7, 'UBICACION', 'ops')

# Comunicacion
ent(19.0, 3.4, 2.6, 0.7, 'CONVERSACION', 'comm')
ent(19.0, 6.0, 2.4, 0.7, 'MENSAJE', 'comm')
ent(19.0, 1.0, 2.6, 0.7, 'NOTIFICACION', 'comm')

# Relaciones
rel = draw_relation
# Estructura horizontal
rel(ax, 3.4, 11.15, 4.0, 11.15, '1:N')
rel(ax, 6.6, 11.15, 7.2, 11.15, '1:N')
rel(ax, 9.6, 11.15, 10.2, 11.15, '1:N')
rel(ax, 12.6, 11.15, 13.2, 11.15, '1:N')
rel(ax, 15.2, 11.15, 15.8, 11.15, '1:N')
rel(ax, 18.4, 11.15, 19.0, 11.15, '1:N')
# Verticales estructura
rel(ax, 5.3, 10.8, 5.3, 10.1, '1:N')
rel(ax, 8.4, 10.8, 8.4, 10.1, '1:N')
rel(ax, 11.4, 10.8, 11.4, 10.1, '1:N')
rel(ax, 14.2, 10.8, 14.2, 10.1, '1:N')
rel(ax, 17.1, 10.8, 17.1, 10.1, '1:N')
rel(ax, 20.2, 10.8, 20.2, 10.1, '1:N')

# Usuario -> perfiles
rel(ax, 2.1, 7.1, 2.1, 6.4, '1:1')
rel(ax, 2.1, 7.1, 4.2, 6.4, '1:1')
rel(ax, 2.1, 7.1, 6.6, 6.4, '1:1')
rel(ax, 2.1, 7.1, 9.4, 6.4, '1:1')

# Personas -> registros
rel(ax, 4.2, 7.1, 4.2, 6.4, '1:N')
rel(ax, 4.2, 7.1, 2.1, 4.1, '1:N')
rel(ax, 4.2, 7.1, 5.0, 4.1, '1:N')
rel(ax, 4.2, 7.1, 10.6, 4.1, '1:N')
rel(ax, 4.2, 7.1, 16.0, 4.1, '1:N')
rel(ax, 4.2, 7.1, 20.2, 3.75, '1:N')

rel(ax, 7.8, 7.1, 7.8, 6.4, '1:N')
rel(ax, 7.8, 7.1, 13.4, 7.1, '1:N')
rel(ax, 7.8, 7.1, 2.1, 4.1, '1:N')
rel(ax, 7.8, 7.1, 5.0, 4.1, '1:N')
rel(ax, 7.8, 7.1, 7.8, 4.1, '1:N')

rel(ax, 10.5, 7.1, 10.5, 6.4, '1:N')
rel(ax, 10.5, 7.1, 10.6, 4.1, '1:N')

# Curso -> registros
rel(ax, 13.4, 7.1, 13.4, 6.4, '1:N')
rel(ax, 13.4, 7.1, 2.1, 4.1, '1:N')
rel(ax, 13.4, 7.1, 5.0, 4.1, '1:N')
rel(ax, 13.4, 7.1, 7.8, 4.1, '1:N')

# Conversacion -> Mensaje
rel(ax, 20.2, 6.0, 20.2, 4.1, '1:N')

# Usuario -> comunicacion/ops
rel(ax, 2.1, 7.1, 13.4, 3.75, '1:N')
rel(ax, 2.1, 7.1, 16.0, 3.75, '1:N')
rel(ax, 2.1, 7.1, 20.2, 6.3, '1:N')
rel(ax, 2.1, 7.1, 20.2, 1.35, '1:N')

# Leyenda
ax.text(0.8, 0.6, 'SIMBOLOGIA:', fontsize=10, fontweight='bold', color='#333')
ax.text(0.8, 0.3, 'Rectangulo = Entidad | Linea = Relacion | 1:1 = Uno a Uno | 1:N = Uno a Muchos', fontsize=9, color='#555')

plt.tight_layout()
plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual_sanmartin.png',
            dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
print('Conceptual OK')
plt.close()

# ============================================================
# 2. MODELO LOGICO
# ============================================================
fig, ax = plt.subplots(figsize=(28, 20))
ax.set_xlim(0, 28)
ax.set_ylim(0, 20)
ax.axis('off')
ax.set_facecolor('#f8f9fa')
fig.patch.set_facecolor('#f8f9fa')

ax.text(14, 19.5, 'MODELO LOGICO DE DATOS', ha='center', va='center', fontsize=22, fontweight='bold', color='#1a237e')
ax.text(14, 19.1, 'IEP Continental Americano - Backend Java | 21 Entidades | Atributos, Claves e Indices', ha='center', va='center', fontsize=11, color='#666')

# Grid de entidades: 7 columnas x 3 filas
cols = [0.5, 4.0, 7.5, 11.0, 14.5, 18.0, 21.5]
rows = [15.5, 11.0, 6.5, 2.0]

def ent_grid(col, row, title, attrs, domain):
    x = cols[col]
    y = rows[row]
    cmap = {'core':('#1565c0','#e3f2fd','#bbdefb'), 'people':('#2e7d32','#e8f5e9','#c8e6c9'),
            'acad':('#e65100','#fff3e0','#ffe0b2'), 'rec':('#c2185b','#fce4ec','#f8bbd9'),
            'comm':('#7b1fa2','#f3e5f5','#e1bee7'), 'ops':('#00695c','#e0f2f1','#b2dfdb')}
    cb, bg, ch = cmap[domain]
    draw_entity(ax, x, y, 3.0, 3.8, title, attrs, cb, bg, ch)

# FILA 0: Auth + Institucion + Personas
ent_grid(0, 0, 'USUARIO', [
    ('_id', ['PK']), ('nombre', []), ('apellido', []), ('email', ['UK']),
    ('dni', ['UK']), ('contrasena', []), ('telefono', []), ('rol', []),
    ('foto', []), ('activo', []), ('verificado', []), ('ultimoLogin', []),
], 'core')
ent_grid(1, 0, 'INSTITUCION', [
    ('_id', ['PK']), ('nombre', []), ('codigo', ['UK']), ('logo', []),
    ('direccion', []), ('telefono', []), ('email', []), ('sitioWeb', []),
    ('director', ['FK']), ('sistemaEval', []), ('escalaNotas', []),
    ('turnos', []), ('nivelesAcad', []), ('activo', []),
], 'acad')
ent_grid(2, 0, 'ESTUDIANTE', [
    ('_id', ['PK']), ('nombre', []), ('apellido', []), ('dni', ['UK']),
    ('email', ['UK']), ('telefono', []), ('fechaNac', []), ('genero', []),
    ('grado', []), ('seccion', []), ('codEstudiante', ['UK']), ('numMatricula', ['UK']),
    ('apoderados', []), ('infoMedica', []), ('documentos', []), ('estado', []),
    ('activo', []), ('idUsuario', ['FK']),
], 'people')
ent_grid(3, 0, 'DOCENTE', [
    ('_id', ['PK']), ('nombre', []), ('apellido', []), ('dni', ['UK']),
    ('email', ['UK']), ('telefono', []), ('fechaNac', []), ('genero', []),
    ('codEmpleado', ['UK']), ('especialidad', []), ('gradoAcad', []), ('tituloProf', []),
    ('certificaciones', []), ('tipoContrato', []), ('salario', []), ('cuentaBanco', []),
    ('documentos', []), ('activo', []), ('idUsuario', ['FK']), ('idInstitucion', ['FK']),
], 'people')
ent_grid(4, 0, 'PADRE', [
    ('_id', ['PK']), ('nombre', []), ('apellido', []), ('dni', ['UK']),
    ('email', ['UK']), ('telefono', []), ('telefono2', []), ('fechaNac', []),
    ('ocupacion', []), ('lugarTrabajo', []), ('hijos', []), ('preferencias', []),
    ('documentos', []), ('tokensPush', []), ('activo', []), ('verificado', []),
    ('idUsuario', ['FK']),
], 'people')

# FILA 1: Academico
ent_grid(0, 1, 'ANIO_ACADEMICO', [
    ('_id', ['PK']), ('idInstitucion', ['FK']), ('anio', []), ('nombre', []),
    ('fechaInicio', []), ('fechaFin', []), ('periodos', []), ('fechasImportantes', []),
    ('actual', []), ('estado', []), ('estadisticas', []), ('activo', []),
], 'acad')
ent_grid(1, 1, 'NIVEL_GRADO', [
    ('_id', ['PK']), ('idInstitucion', ['FK']), ('nombre', []), ('nombreCorto', []),
    ('nivel', []), ('tipo', []), ('orden', []), ('descripcion', []), ('activo', []),
], 'acad')
ent_grid(2, 1, 'ASIGNATURA', [
    ('_id', ['PK']), ('idInstitucion', ['FK']), ('nombre', []), ('codigo', ['UK']),
    ('descripcion', []), ('niveles', ['FK']), ('aplicableA', []), ('horasSemana', []),
    ('obligatoria', []), ('area', []), ('pesosDefault', []), ('competencias', []), ('activo', []),
], 'acad')
ent_grid(3, 1, 'AULA', [
    ('_id', ['PK']), ('idNivel', ['FK']), ('idAnio', ['FK']), ('seccion', []),
    ('turno', []), ('tutor', ['FK']), ('capacidad', []), ('ubicacion', []),
    ('estadisticas', []), ('activo', []),
], 'acad')
ent_grid(4, 1, 'CURSO', [
    ('_id', ['PK']), ('nombre', []), ('codigo', ['UK']), ('descripcion', []),
    ('grado', []), ('seccion', []), ('idDocente', ['FK']), ('estudiantes', ['FK']),
    ('horario', []), ('pesosEval', []), ('anioAcad', []), ('periodo', []), ('activo', []),
], 'acad')
ent_grid(5, 1, 'SECCION_CURSO', [
    ('_id', ['PK']), ('idAsignatura', ['FK']), ('idAula', ['FK']), ('idDocente', ['FK']),
    ('idAnio', ['FK']), ('horario', []), ('pesosEval', []), ('evalPeriodo', []),
    ('estadisticas', []), ('recursos', []), ('estudiantes', ['FK']), ('activo', []),
], 'acad')
ent_grid(6, 1, 'MATRICULA', [
    ('_id', ['PK']), ('idEstudiante', ['FK']), ('idAula', ['FK']), ('idAnio', ['FK']),
    ('fechaMatricula', []), ('numMatricula', ['UK']), ('estado', []), ('fechaEstado', []),
    ('motivoEstado', []), ('tipoMatricula', []), ('historialEstado', []), ('documentos', []),
    ('matriculadoPor', ['FK']), ('activo', []),
], 'acad')

# FILA 2: Registros
ent_grid(0, 2, 'NOTA', [
    ('_id', ['PK']), ('idEstudiante', ['FK']), ('idCurso', ['FK']), ('bimestre', []),
    ('anioAcad', []), ('puntajes', []), ('promedio', []), ('estado', []),
    ('fechaCierre', []), ('cerradoPor', []), ('fechaPublicacion', []), ('idDocente', ['FK']),
], 'rec')
ent_grid(1, 2, 'ASISTENCIA', [
    ('_id', ['PK']), ('idEstudiante', ['FK']), ('idCurso', ['FK']), ('idDocente', ['FK']),
    ('fecha', []), ('estado', []), ('horaLlegada', []), ('observaciones', []),
    ('idJustificacion', ['FK']),
], 'rec')
ent_grid(2, 2, 'EVALUACION', [
    ('_id', ['PK']), ('idCurso', ['FK']), ('idDocente', ['FK']), ('nombre', []),
    ('tipo', []), ('bimestre', []), ('notaMax', []), ('peso', []), ('fecha', []),
    ('descripcion', []), ('anioAcad', []), ('activo', []), ('orden', []),
], 'rec')
ent_grid(3, 2, 'EVENTO', [
    ('_id', ['PK']), ('titulo', []), ('fecha', []), ('hora', []), ('tipo', []),
    ('descripcion', []), ('lugar', []), ('participantes', []), ('notifEst', []),
    ('notifPadres', []), ('notifDocentes', []), ('creadoPor', ['FK']), ('activo', []),
], 'rec')
ent_grid(4, 2, 'JUSTIFICACION', [
    ('_id', ['PK']), ('idEstudiante', ['FK']), ('idPadre', ['FK']), ('fechas', []),
    ('motivo', []), ('observaciones', []), ('documentos', []), ('cursosAfect', ['FK']),
    ('estado', []), ('revisadoPor', ['FK']), ('fechaRevision', []), ('notaRevision', []),
], 'ops')
ent_grid(5, 2, 'UBICACION', [
    ('_id', ['PK']), ('idUsuario', ['FK']), ('coordenadas', []), ('infoDispositivo', []),
    ('estadoSesion', []), ('tipoActualiz', []), ('direccion', []), ('nivelBateria', []),
    ('tipoRed', []), ('timestampCliente', []),
], 'ops')

# FILA 3: Comunicacion
ent_grid(0, 3, 'CONVERSACION', [
    ('_id', ['PK']), ('participantes', ['FK']), ('tipo', []), ('nombre', []),
    ('ultimoMensaje', []), ('sinLeer', []), ('activo', []), ('metadata', []),
], 'comm')
ent_grid(1, 3, 'MENSAJE', [
    ('_id', ['PK']), ('idConvers', ['FK']), ('idRemitente', ['FK']), ('contenido', []),
    ('tipo', []), ('adjuntos', []), ('leidoPor', []), ('eliminado', []),
], 'comm')
ent_grid(2, 3, 'NOTIFICACION', [
    ('_id', ['PK']), ('idDestinatario', ['FK']), ('titulo', []), ('mensaje', []),
    ('tipo', []), ('datos', []), ('leido', []), ('fechaLectura', []), ('fechaExpira', []),
], 'comm')

# Relaciones (solo las principales, limpias)
# Usuario -> perfiles
rel(ax, 2.0, 15.5, 8.5, 15.1, '1:1')
rel(ax, 2.0, 15.3, 12.5, 15.1, '1:1')
rel(ax, 2.0, 15.1, 16.0, 15.1, '1:1')
# Institucion -> Anio, Nivel, Asignatura
rel(ax, 5.5, 15.5, 2.0, 11.4, '1:N')
rel(ax, 5.5, 15.3, 5.5, 11.4, '1:N')
rel(ax, 5.5, 15.1, 9.0, 11.4, '1:N')
# Anio -> Aula, Seccion, Matricula
rel(ax, 2.0, 11.0, 12.5, 11.0, '1:N')
rel(ax, 2.0, 10.8, 19.0, 11.0, '1:N')
rel(ax, 2.0, 10.6, 23.5, 11.0, '1:N')
# Nivel -> Aula, Asignatura
rel(ax, 5.5, 11.0, 9.0, 11.0, '1:N')
# Aula -> Seccion, Matricula
rel(ax, 12.5, 11.0, 16.0, 11.0, '1:N')
rel(ax, 12.5, 10.8, 23.5, 10.8, '1:N')
# Estudiante -> Matricula, Nota, Asistencia, Justificacion
rel(ax, 8.5, 15.5, 23.5, 11.0, '1:N')
rel(ax, 8.5, 15.3, 2.0, 6.4, '1:N')
rel(ax, 8.5, 15.1, 5.5, 6.4, '1:N')
rel(ax, 8.5, 14.9, 16.0, 6.4, '1:N')
# Docente -> Curso, Nota, Asistencia, Evaluacion
rel(ax, 12.5, 15.5, 14.5, 11.4, '1:N')
rel(ax, 12.5, 15.3, 2.0, 6.2, '1:N')
rel(ax, 12.5, 15.1, 5.5, 6.2, '1:N')
rel(ax, 12.5, 14.9, 9.0, 6.4, '1:N')
# Padre -> Justificacion
rel(ax, 16.0, 15.5, 16.0, 6.4, '1:N')
# Curso -> Nota, Asistencia, Evaluacion
rel(ax, 14.5, 11.0, 2.0, 6.0, '1:N')
rel(ax, 14.5, 10.8, 5.5, 6.0, '1:N')
rel(ax, 14.5, 10.6, 9.0, 6.2, '1:N')
# Usuario -> Evento, Ubicacion, Conversacion, Mensaje, Notificacion
rel(ax, 2.0, 15.5, 11.5, 6.0, '1:N')
rel(ax, 2.0, 15.3, 20.0, 6.4, '1:N')
rel(ax, 2.0, 15.1, 2.0, 1.8, 'N:M')
rel(ax, 2.0, 14.9, 5.5, 1.8, '1:N')
rel(ax, 2.0, 14.7, 9.0, 1.8, '1:N')
# Conversacion -> Mensaje
rel(ax, 2.0, 1.8, 5.5, 1.8, '1:N')

# Leyenda
ax.text(0.8, 0.8, 'LEYENDA:', fontsize=10, fontweight='bold', color='#333')
ax.text(0.8, 0.5, 'PK = Clave Primaria (rojo)  |  FK = Clave Foranea (azul)  |  UK = Indice Unico (verde)', fontsize=9, color='#555')
ax.text(0.8, 0.2, '1:1 = Uno a Uno  |  1:N = Uno a Muchos  |  N:M = Muchos a Muchos', fontsize=9, color='#555')

# Dominios
ax.text(18.0, 0.5, 'AUTENTICACION', fontsize=8, color='#1565c0', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e3f2fd', edgecolor='#1565c0'))
ax.text(21.0, 0.5, 'PERSONAS', fontsize=8, color='#2e7d32', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e8f5e9', edgecolor='#2e7d32'))
ax.text(23.5, 0.5, 'ACADEMICO', fontsize=8, color='#e65100', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#fff3e0', edgecolor='#e65100'))
ax.text(18.0, 0.2, 'REGISTROS', fontsize=8, color='#c2185b', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#fce4ec', edgecolor='#c2185b'))
ax.text(21.0, 0.2, 'COMUNICACION', fontsize=8, color='#7b1fa2', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#f3e5f5', edgecolor='#7b1fa2'))
ax.text(23.5, 0.2, 'OPERACIONES', fontsize=8, color='#00695c', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e0f2f1', edgecolor='#00695c'))

plt.tight_layout()
plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico_sanmartin.png',
            dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
print('Logico OK')
plt.close()

# ============================================================
# 3. MODELO FISICO
# ============================================================
fig, ax = plt.subplots(figsize=(28, 20))
ax.set_xlim(0, 28)
ax.set_ylim(0, 20)
ax.axis('off')
ax.set_facecolor('#f8f9fa')
fig.patch.set_facecolor('#f8f9fa')

ax.text(14, 19.5, 'MODELO FISICO DE DATOS', ha='center', va='center', fontsize=22, fontweight='bold', color='#1a237e')
ax.text(14, 19.1, 'MongoDB Document Model | 21 Colecciones | Spring Boot 3.2.5 + Java 17', ha='center', va='center', fontsize=11, color='#666')

# Mismo layout que logico pero con nombres de colecciones y campos reales
ent_grid(0, 0, 'users', [
    ('_id', ['PK']), ('firstName', []), ('lastName', []), ('email', ['UK']),
    ('dni', ['UK']), ('password', []), ('phone', []), ('role', []),
    ('photo', []), ('isActive', []), ('isVerified', []), ('lastLogin', []),
], 'core')
ent_grid(1, 0, 'institutions', [
    ('_id', ['PK']), ('name', []), ('code', ['UK']), ('logo', []),
    ('address', ['EMB']), ('phone', []), ('email', []), ('website', []),
    ('director', ['FK']), ('evaluationSystem', ['EMB']), ('gradeScale', ['EMB']),
    ('shifts', []), ('academicLevels', ['EMB']), ('isActive', []),
], 'acad')
ent_grid(2, 0, 'students', [
    ('_id', ['PK']), ('firstName', []), ('lastName', []), ('dni', ['UK']),
    ('email', ['UK']), ('phone', []), ('birthDate', []), ('gender', []),
    ('gradeLevel', []), ('section', []), ('studentCode', ['UK']), ('enrollmentNumber', ['UK']),
    ('guardians', ['EMB']), ('medicalInfo', ['EMB']), ('documents', ['EMB']), ('status', []),
    ('isActive', []), ('userId', ['FK']),
], 'people')
ent_grid(3, 0, 'teachers', [
    ('_id', ['PK']), ('firstName', []), ('lastName', []), ('dni', ['UK']),
    ('email', ['UK']), ('phone', []), ('birthDate', []), ('gender', []),
    ('employeeCode', ['UK']), ('specialty', []), ('academicDegree', []), ('professionalTitle', []),
    ('certifications', ['EMB']), ('contractType', []), ('salary', []), ('bankAccount', ['EMB']),
    ('documents', ['EMB']), ('isActive', []), ('userId', ['FK']), ('institution', ['FK']),
], 'people')
ent_grid(4, 0, 'parents', [
    ('_id', ['PK']), ('firstName', []), ('lastName', []), ('dni', ['UK']),
    ('email', ['UK']), ('phone', []), ('secondaryPhone', []), ('birthDate', []),
    ('occupation', []), ('workplace', []), ('children', ['EMB']), ('notifications', ['EMB']),
    ('documents', ['EMB']), ('pushTokens', ['EMB']), ('isActive', []), ('isVerified', []),
    ('userId', ['FK']),
], 'people')

ent_grid(0, 1, 'academicyears', [
    ('_id', ['PK']), ('institution', ['FK']), ('year', []), ('name', []),
    ('startDate', []), ('endDate', []), ('periods', ['EMB']), ('importantDates', ['EMB']),
    ('isCurrent', []), ('status', []), ('stats', ['EMB']), ('isActive', []),
], 'acad')
ent_grid(1, 1, 'gradelevels', [
    ('_id', ['PK']), ('institution', ['FK']), ('name', []), ('shortName', []),
    ('level', []), ('type', []), ('order', []), ('description', []), ('isActive', []),
], 'acad')
ent_grid(2, 1, 'subjects', [
    ('_id', ['PK']), ('institution', ['FK']), ('name', []), ('code', ['UK']),
    ('description', []), ('gradeLevels', ['FK']), ('applicableTo', []), ('hoursPerWeek', []),
    ('isRequired', []), ('area', []), ('defaultWeights', ['EMB']), ('competencies', ['EMB']), ('isActive', []),
], 'acad')
ent_grid(3, 1, 'classrooms', [
    ('_id', ['PK']), ('gradeLevel', ['FK']), ('academicYear', ['FK']), ('section', []),
    ('shift', []), ('tutor', ['FK']), ('capacity', []), ('location', ['EMB']),
    ('stats', ['EMB']), ('isActive', []),
], 'acad')
ent_grid(4, 1, 'courses', [
    ('_id', ['PK']), ('name', []), ('code', ['UK']), ('description', []),
    ('gradeLevel', []), ('section', []), ('teacher', ['FK']), ('students', ['FK']),
    ('schedule', ['EMB']), ('evaluationWeights', ['EMB']), ('academicYear', []), ('period', []), ('isActive', []),
], 'acad')
ent_grid(5, 1, 'coursesections', [
    ('_id', ['PK']), ('subject', ['FK']), ('classroom', ['FK']), ('teacher', ['FK']),
    ('academicYear', ['FK']), ('schedule', ['EMB']), ('evaluationWeights', ['EMB']), ('periodEvaluations', ['EMB']),
    ('stats', ['EMB']), ('resources', ['EMB']), ('students', ['FK']), ('isActive', []),
], 'acad')
ent_grid(6, 1, 'enrollments', [
    ('_id', ['PK']), ('student', ['FK']), ('classroom', ['FK']), ('academicYear', ['FK']),
    ('enrollmentDate', []), ('enrollmentNumber', ['UK']), ('status', []), ('statusDate', []),
    ('statusReason', []), ('enrollmentType', []), ('statusHistory', ['EMB']), ('documents', ['EMB']),
    ('enrolledBy', ['FK']), ('isActive', []),
], 'acad')

ent_grid(0, 2, 'grades', [
    ('_id', ['PK']), ('student', ['FK']), ('course', ['FK']), ('bimester', []),
    ('academicYear', []), ('scores', ['EMB']), ('average', []), ('status', []),
    ('closedAt', []), ('closedBy', []), ('publishedAt', []), ('teacher', ['FK']),
], 'rec')
ent_grid(1, 2, 'attendances', [
    ('_id', ['PK']), ('student', ['FK']), ('course', ['FK']), ('teacher', ['FK']),
    ('date', []), ('status', []), ('arrivalTime', []), ('observations', []),
    ('justification', ['FK']),
], 'rec')
ent_grid(2, 2, 'evaluations', [
    ('_id', ['PK']), ('course', ['FK']), ('teacher', ['FK']), ('name', []),
    ('type', []), ('bimester', []), ('maxGrade', []), ('weight', []), ('date', []),
    ('description', []), ('academicYear', []), ('isActive', []), ('order', []),
], 'rec')
ent_grid(3, 2, 'events', [
    ('_id', ['PK']), ('title', []), ('date', []), ('time', []), ('type', []),
    ('description', []), ('location', []), ('participants', []), ('notifyStudents', []),
    ('notifyParents', []), ('notifyTeachers', []), ('createdBy', ['FK']), ('isActive', []),
], 'rec')
ent_grid(4, 2, 'justifications', [
    ('_id', ['PK']), ('student', ['FK']), ('parent', ['FK']), ('dates', []),
    ('reason', []), ('observations', []), ('documents', ['EMB']), ('coursesAffected', ['FK']),
    ('status', []), ('reviewedBy', ['FK']), ('reviewedAt', []), ('reviewNote', []),
], 'ops')
ent_grid(5, 2, 'locations', [
    ('_id', ['PK']), ('user', ['FK']), ('coordinates', ['EMB']), ('deviceInfo', ['EMB']),
    ('sessionStatus', []), ('updateType', []), ('address', ['EMB']), ('batteryLevel', []),
    ('networkType', []), ('clientTimestamp', []),
], 'ops')

ent_grid(0, 3, 'conversations', [
    ('_id', ['PK']), ('participants', ['FK']), ('type', []), ('name', []),
    ('lastMessage', ['EMB']), ('unreadCount', []), ('isActive', []), ('metadata', ['EMB']),
], 'comm')
ent_grid(1, 3, 'messages', [
    ('_id', ['PK']), ('conversation', ['FK']), ('sender', ['FK']), ('content', []),
    ('type', []), ('attachments', ['EMB']), ('readBy', ['EMB']), ('isDeleted', []),
], 'comm')
ent_grid(2, 3, 'notifications', [
    ('_id', ['PK']), ('recipient', ['FK']), ('title', []), ('message', []),
    ('type', []), ('data', ['EMB']), ('isRead', []), ('readAt', []), ('expiresAt', []),
], 'comm')

# Relaciones (mismas que logico)
rel(ax, 2.0, 15.5, 8.5, 15.1, '1:1')
rel(ax, 2.0, 15.3, 12.5, 15.1, '1:1')
rel(ax, 2.0, 15.1, 16.0, 15.1, '1:1')
rel(ax, 5.5, 15.5, 2.0, 11.4, '1:N')
rel(ax, 5.5, 15.3, 5.5, 11.4, '1:N')
rel(ax, 5.5, 15.1, 9.0, 11.4, '1:N')
rel(ax, 2.0, 11.0, 12.5, 11.0, '1:N')
rel(ax, 2.0, 10.8, 19.0, 11.0, '1:N')
rel(ax, 2.0, 10.6, 23.5, 11.0, '1:N')
rel(ax, 5.5, 11.0, 9.0, 11.0, '1:N')
rel(ax, 12.5, 11.0, 16.0, 11.0, '1:N')
rel(ax, 12.5, 10.8, 23.5, 10.8, '1:N')
rel(ax, 8.5, 15.5, 23.5, 11.0, '1:N')
rel(ax, 8.5, 15.3, 2.0, 6.4, '1:N')
rel(ax, 8.5, 15.1, 5.5, 6.4, '1:N')
rel(ax, 8.5, 14.9, 16.0, 6.4, '1:N')
rel(ax, 12.5, 15.5, 14.5, 11.4, '1:N')
rel(ax, 12.5, 15.3, 2.0, 6.2, '1:N')
rel(ax, 12.5, 15.1, 5.5, 6.2, '1:N')
rel(ax, 12.5, 14.9, 9.0, 6.4, '1:N')
rel(ax, 16.0, 15.5, 16.0, 6.4, '1:N')
rel(ax, 14.5, 11.0, 2.0, 6.0, '1:N')
rel(ax, 14.5, 10.8, 5.5, 6.0, '1:N')
rel(ax, 14.5, 10.6, 9.0, 6.2, '1:N')
rel(ax, 2.0, 15.5, 11.5, 6.0, '1:N')
rel(ax, 2.0, 15.3, 20.0, 6.4, '1:N')
rel(ax, 2.0, 15.1, 2.0, 1.8, 'N:M')
rel(ax, 2.0, 14.9, 5.5, 1.8, '1:N')
rel(ax, 2.0, 14.7, 9.0, 1.8, '1:N')
rel(ax, 2.0, 1.8, 5.5, 1.8, '1:N')

ax.text(0.8, 0.8, 'LEYENDA:', fontsize=10, fontweight='bold', color='#333')
ax.text(0.8, 0.5, 'PK = Primary Key (red)  |  FK = Foreign Key (blue)  |  UK = Unique Index (green)  |  EMB = Embedded Document (orange)', fontsize=9, color='#555')
ax.text(0.8, 0.2, '1:1 = One to One  |  1:N = One to Many  |  N:M = Many to Many', fontsize=9, color='#555')

ax.text(18.0, 0.5, 'AUTH', fontsize=8, color='#1565c0', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e3f2fd', edgecolor='#1565c0'))
ax.text(20.5, 0.5, 'PEOPLE', fontsize=8, color='#2e7d32', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e8f5e9', edgecolor='#2e7d32'))
ax.text(23.0, 0.5, 'ACADEMIC', fontsize=8, color='#e65100', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#fff3e0', edgecolor='#e65100'))
ax.text(18.0, 0.2, 'RECORDS', fontsize=8, color='#c2185b', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#fce4ec', edgecolor='#c2185b'))
ax.text(20.5, 0.2, 'COMM', fontsize=8, color='#7b1fa2', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#f3e5f5', edgecolor='#7b1fa2'))
ax.text(23.0, 0.2, 'OPS', fontsize=8, color='#00695c', fontweight='bold', bbox=dict(boxstyle='round,pad=0.3', facecolor='#e0f2f1', edgecolor='#00695c'))

plt.tight_layout()
plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico_sanmartin.png',
            dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
print('Fisico OK')
plt.close()

print('\n=== TODOS LOS DIAGRAMAS GENERADOS ===')
print('1. modelo_conceptual_sanmartin.png')
print('2. modelo_logico_sanmartin.png')
print('3. modelo_fisico_sanmartin.png')
