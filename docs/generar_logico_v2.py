import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import numpy as np

# ============================================================
# MODELO LOGICO LIMPIO - Grid Organizado por Dominios
# ============================================================

def draw_logical_clean():
    fig, ax = plt.subplots(1, 1, figsize=(32, 22))
    ax.set_xlim(0, 32)
    ax.set_ylim(0, 22)
    ax.axis('off')
    ax.set_facecolor('#f8f9fa')
    fig.patch.set_facecolor('#f8f9fa')

    # Colores
    c = {
        'core': '#1565c0', 'core_bg': '#e3f2fd', 'core_hdr': '#bbdefb',
        'people': '#2e7d32', 'people_bg': '#e8f5e9', 'people_hdr': '#c8e6c9',
        'academic': '#e65100', 'academic_bg': '#fff3e0', 'academic_hdr': '#ffe0b2',
        'records': '#c2185b', 'records_bg': '#fce4ec', 'records_hdr': '#f8bbd9',
        'comm': '#7b1fa2', 'comm_bg': '#f3e5f5', 'comm_hdr': '#e1bee7',
        'ops': '#00695c', 'ops_bg': '#e0f2f1', 'ops_hdr': '#b2dfdb',
    }

    def box(x, y, w, h, title, attrs, domain_key):
        dk = c[domain_key]
        bg = c[domain_key + '_bg']
        hdr = c[domain_key + '_hdr']
        # Header
        ax.add_patch(FancyBboxPatch((x, y+h-0.42), w, 0.42, boxstyle="round,pad=0.02,rounding_size=0.1",
                                     facecolor=hdr, edgecolor=dk, linewidth=2, zorder=3))
        ax.text(x+w/2, y+h-0.21, title, ha='center', va='center', fontsize=10,
                fontweight='bold', color=dk, zorder=4)
        # Body
        ax.add_patch(FancyBboxPatch((x, y), w, h-0.42, boxstyle="round,pad=0.02,rounding_size=0.1",
                                     facecolor=bg, edgecolor=dk, linewidth=1.5, zorder=2))
        line_h = 0.28
        sy = y + h - 0.42 - 0.28
        for i, (name, tags) in enumerate(attrs):
            fy = sy - i*line_h
            if fy < y + 0.06: break
            col = '#333'
            if 'PK' in tags: col = '#c62828'
            elif 'FK' in tags: col = '#1565c0'
            ax.text(x+0.14, fy, name, ha='left', va='center', fontsize=7.5,
                    color=col, fontweight='bold' if 'PK' in tags else 'normal', zorder=4)
            if tags:
                ax.text(x+w-0.12, fy, " ".join(tags), ha='right', va='center',
                        fontsize=6.5, color='#888', zorder=4)

    def line(x1, y1, x2, y2, lbl="", color='#90a4ae'):
        ax.plot([x1, x2], [y1, y2], color=color, linewidth=1.3, zorder=1)
        if lbl:
            mx, my = (x1+x2)/2, (y1+y2)/2
            ax.text(mx, my, lbl, fontsize=7, color='#546e7a', ha='center', va='center',
                    bbox=dict(boxstyle='round,pad=0.15', facecolor='white', edgecolor='#cfd8dc', alpha=0.9), zorder=2)

    # TITULO
    ax.text(16, 21.5, 'MODELO LOGICO DE DATOS', ha='center', va='center',
            fontsize=24, fontweight='bold', color='#1a237e')
    ax.text(16, 21.1, 'IEP Continental Americano - Backend Java | MongoDB Document Model | 21 Entidades',
            ha='center', va='center', fontsize=11, color='#666')

    # === COLUMNA 1: AUTENTICACION + INSTITUCION ===
    box(0.5, 17.0, 3.0, 3.8, "USUARIO", [
        ("_id", ["PK"]), ("nombre", []), ("apellido", []), ("email", ["UK"]),
        ("dni", ["UK"]), ("contrasena", []), ("telefono", []), ("rol", ["IX"]),
        ("foto", []), ("activo", []), ("verificado", []), ("ultimo_login", []),
    ], 'core')

    box(0.5, 13.0, 3.0, 3.6, "INSTITUCION", [
        ("_id", ["PK"]), ("nombre", []), ("codigo", ["UK"]), ("logo", []),
        ("direccion", []), ("telefono", []), ("email", []), ("sitio_web", []),
        ("director", ["FK"]), ("sistema_eval", []), ("escala_notas", []),
        ("turnos", []), ("niveles_acad", []), ("activo", []),
    ], 'academic')

    # === COLUMNA 2: PERSONAS ===
    box(4.0, 17.0, 3.0, 4.2, "ESTUDIANTE", [
        ("_id", ["PK"]), ("nombre", ["TI"]), ("apellido", ["TI"]), ("dni", ["UK"]),
        ("email", ["UK"]), ("telefono", []), ("fecha_nac", []), ("genero", []),
        ("grado", []), ("seccion", []), ("cod_estudiante", ["UK"]), ("num_matricula", ["UK"]),
        ("apoderados", []), ("info_medica", []), ("documentos", []), ("estado", []),
        ("activo", []), ("id_usuario", ["FK"]),
    ], 'people')

    box(4.0, 12.5, 3.0, 4.0, "DOCENTE", [
        ("_id", ["PK"]), ("nombre", ["TI"]), ("apellido", ["TI"]), ("dni", ["UK"]),
        ("email", ["UK"]), ("telefono", []), ("fecha_nac", []), ("cod_empleado", ["UK"]),
        ("especialidad", []), ("grado_acad", []), ("titulo_prof", []), ("certificaciones", []),
        ("tipo_contrato", []), ("salario", []), ("cuenta_banco", []), ("documentos", []),
        ("activo", []), ("id_usuario", ["FK"]), ("id_institucion", ["FK"]),
    ], 'people')

    box(4.0, 8.0, 3.0, 4.0, "PADRE", [
        ("_id", ["PK"]), ("nombre", ["TI"]), ("apellido", ["TI"]), ("dni", ["UK"]),
        ("email", ["UK"]), ("telefono", ["IX"]), ("telefono2", []), ("fecha_nac", []),
        ("ocupacion", []), ("lugar_trabajo", []), ("hijos", []), ("pref_notif", []),
        ("documentos", []), ("tokens_push", []), ("activo", []), ("verificado", []),
        ("id_usuario", ["FK"]),
    ], 'people')

    # === COLUMNA 3: ESTRUCTURA ACADEMICA ===
    box(7.5, 17.5, 3.0, 3.3, "ANIO ACADEMICO", [
        ("_id", ["PK"]), ("id_institucion", ["FK"]), ("anio", []), ("nombre", []),
        ("fecha_inicio", []), ("fecha_fin", []), ("periodos", []), ("fechas_imp", []),
        ("actual", ["IX"]), ("estado", []), ("estadisticas", []), ("activo", []),
    ], 'academic')

    box(7.5, 14.0, 3.0, 3.0, "NIVEL DE GRADO", [
        ("_id", ["PK"]), ("id_institucion", ["FK"]), ("nombre", []), ("nombre_corto", []),
        ("nivel", []), ("tipo", []), ("orden", []), ("descripcion", []), ("activo", []),
    ], 'academic')

    box(7.5, 10.5, 3.0, 3.1, "ASIGNATURA", [
        ("_id", ["PK"]), ("id_institucion", ["FK"]), ("nombre", []), ("codigo", ["UK"]),
        ("descripcion", []), ("niveles", ["FK"]), ("aplicable_a", []), ("horas_sem", []),
        ("obligatoria", []), ("area", []), ("pesos_default", []), ("competencias", []), ("activo", []),
    ], 'academic')

    box(7.5, 7.0, 3.0, 3.0, "AULA", [
        ("_id", ["PK"]), ("id_nivel", ["FK"]), ("id_anio", ["FK"]), ("seccion", []),
        ("turno", []), ("tutor", ["FK"]), ("capacidad", []), ("ubicacion", []),
        ("estadisticas", []), ("activo", []),
    ], 'academic')

    # === COLUMNA 4: CURSOS Y MATRICULAS ===
    box(11.0, 17.0, 3.0, 3.2, "CURSO", [
        ("_id", ["PK"]), ("nombre", []), ("codigo", ["UK"]), ("descripcion", []),
        ("grado", []), ("seccion", []), ("id_docente", ["FK"]), ("estudiantes", ["FK"]),
        ("horario", []), ("pesos_eval", []), ("anio_acad", []), ("periodo", []), ("activo", []),
    ], 'academic')

    box(11.0, 13.5, 3.2, 3.2, "SECCION CURSO", [
        ("_id", ["PK"]), ("id_asignatura", ["FK"]), ("id_aula", ["FK"]), ("id_docente", ["FK"]),
        ("id_anio", ["FK"]), ("horario", []), ("pesos_eval", []), ("eval_periodo", []),
        ("estadisticas", []), ("recursos", []), ("estudiantes", ["FK"]), ("activo", []),
    ], 'academic')

    box(11.0, 9.5, 3.0, 3.6, "MATRICULA", [
        ("_id", ["PK"]), ("id_estudiante", ["FK"]), ("id_aula", ["FK"]), ("id_anio", ["FK"]),
        ("fecha_matricula", []), ("num_matricula", ["UK"]), ("estado", []), ("fecha_estado", []),
        ("motivo_estado", []), ("tipo_matricula", []), ("historial_estado", []), ("documentos", []),
        ("id_matriculador", ["FK"]), ("activo", []),
    ], 'academic')

    # === COLUMNA 5: REGISTROS ACADEMICOS ===
    box(14.5, 17.0, 3.0, 3.5, "NOTA", [
        ("_id", ["PK"]), ("id_estudiante", ["FK"]), ("id_curso", ["FK"]), ("bimestre", []),
        ("anio_acad", []), ("puntajes", []), ("promedio", []), ("estado", []),
        ("fecha_cierre", []), ("cerrado_por", []), ("fecha_public", []), ("id_docente", ["FK"]),
    ], 'records')

    box(14.5, 13.3, 3.0, 3.3, "ASISTENCIA", [
        ("_id", ["PK"]), ("id_estudiante", ["FK"]), ("id_curso", ["FK"]), ("id_docente", ["FK"]),
        ("fecha", ["IX"]), ("estado", []), ("hora_llegada", []), ("observaciones", []),
        ("id_justificacion", ["FK"]),
    ], 'records')

    box(14.5, 9.5, 3.0, 3.4, "EVALUACION", [
        ("_id", ["PK"]), ("id_curso", ["FK"]), ("id_docente", ["FK"]), ("nombre", []),
        ("tipo", []), ("bimestre", []), ("nota_max", []), ("peso", []), ("fecha", []),
        ("descripcion", []), ("anio_acad", []), ("activo", []), ("orden", []),
    ], 'records')

    box(14.5, 5.8, 3.0, 3.3, "EVENTO", [
        ("_id", ["PK"]), ("titulo", []), ("fecha", ["IX"]), ("hora", []), ("tipo", ["IX"]),
        ("descripcion", []), ("lugar", []), ("participantes", []), ("notif_est", []),
        ("notif_padres", []), ("notif_docentes", []), ("creado_por", ["FK"]), ("activo", ["IX"]),
    ], 'records')

    # === COLUMNA 6: OPERACIONES ===
    box(18.0, 17.0, 3.0, 3.0, "JUSTIFICACION", [
        ("_id", ["PK"]), ("id_estudiante", ["FK"]), ("id_padre", ["FK"]), ("fechas", []),
        ("motivo", []), ("observaciones", []), ("documentos", []), ("cursos_afect", ["FK"]),
        ("estado", ["IX"]), ("revisado_por", ["FK"]), ("fecha_revision", []), ("nota_rev", []),
    ], 'ops')

    box(18.0, 13.5, 3.0, 3.2, "UBICACION", [
        ("_id", ["PK"]), ("id_usuario", ["FK", "IX"]), ("coordenadas", []), ("info_disp", []),
        ("estado_sesion", []), ("tipo_actualiz", []), ("direccion", []), ("nivel_bateria", []),
        ("tipo_red", []), ("timestamp_cliente", []),
    ], 'ops')

    # === COLUMNA 7: COMUNICACION ===
    box(21.5, 17.0, 3.0, 2.8, "CONVERSACION", [
        ("_id", ["PK"]), ("participantes", ["FK"]), ("tipo", []), ("nombre", []),
        ("ultimo_mensaje", []), ("sin_leer", []), ("activo", []), ("metadata", []),
    ], 'comm')

    box(21.5, 13.5, 3.0, 3.0, "MENSAJE", [
        ("_id", ["PK"]), ("id_convers", ["FK"]), ("id_remitente", ["FK"]), ("contenido", []),
        ("tipo", []), ("adjuntos", []), ("leido_por", []), ("eliminado", []),
    ], 'comm')

    box(21.5, 9.5, 3.0, 3.5, "NOTIFICACION", [
        ("_id", ["PK"]), ("id_destinatario", ["FK"]), ("titulo", []), ("mensaje", []),
        ("tipo", []), ("datos", []), ("leido", []), ("fecha_lectura", []), ("fecha_expira", []),
    ], 'comm')

    # === RELACIONES (solo las principales, sin cruzar) ===
    # Institucion -> Anio, Nivel, Asignatura, Docente
    line(3.5, 14.8, 7.5, 18.7, "1:N")
    line(3.5, 14.5, 7.5, 15.8, "1:N")
    line(3.5, 14.2, 7.5, 12.5, "1:N")
    line(3.5, 14.0, 4.0, 14.8, "1:N")  # Institucion -> Docente (misma col)

    # Usuario -> Estudiante, Docente, Padre
    line(3.5, 18.2, 4.0, 19.5, "1:1")
    line(3.5, 17.8, 4.0, 15.0, "1:1")
    line(3.5, 17.5, 4.0, 10.5, "1:1")

    # Usuario -> Ubicacion, Evento, Notificacion, Conversacion, Mensaje
    line(3.5, 18.5, 18.0, 16.5, "1:N")
    line(3.5, 18.0, 18.0, 14.5, "1:N")
    line(3.5, 17.5, 14.5, 8.0, "1:N")
    line(3.5, 17.0, 21.5, 17.8, "N:M")
    line(3.5, 16.5, 21.5, 15.5, "1:N")
    line(3.5, 16.0, 21.5, 12.0, "1:N")

    # Anio Academico -> Aula, Seccion Curso, Matricula
    line(10.5, 18.5, 11.0, 18.5, "1:N")
    line(10.5, 18.2, 11.0, 15.8, "1:N")
    line(10.5, 18.0, 11.0, 12.5, "1:N")

    # Nivel Grado -> Aula, Asignatura
    line(10.5, 15.5, 11.0, 16.2, "1:N")  # Nivel -> Curso
    line(10.5, 15.0, 10.5, 14.0, "1:N")  # Nivel -> Aula (vertical por columna)

    # Asignatura -> Seccion Curso
    line(10.5, 12.0, 11.0, 15.0, "1:N")

    # Aula -> Seccion Curso, Matricula
    line(10.5, 10.0, 11.0, 14.5, "1:N")
    line(10.5, 9.5, 11.0, 11.0, "1:N")

    # Estudiante -> Matricula, Nota, Asistencia, Justificacion, Ubicacion
    line(7.0, 19.0, 7.5, 12.5, "1:N")
    line(7.0, 18.5, 14.5, 19.0, "1:N")
    line(7.0, 18.0, 14.5, 15.2, "1:N")
    line(7.0, 17.5, 18.0, 18.5, "1:N")
    line(7.0, 17.0, 18.0, 16.5, "1:N")

    # Docente -> Curso, Seccion Curso, Nota, Asistencia, Evaluacion
    line(7.0, 14.5, 11.0, 18.2, "1:N")
    line(7.0, 14.0, 11.0, 15.0, "1:N")
    line(7.0, 13.5, 14.5, 18.5, "1:N")
    line(7.0, 13.0, 14.5, 15.0, "1:N")
    line(7.0, 12.5, 14.5, 11.5, "1:N")

    # Padre -> Estudiante, Justificacion
    line(7.0, 10.0, 5.5, 8.5, "1:N")
    line(7.0, 9.5, 18.0, 18.2, "1:N")

    # Curso -> Nota, Asistencia, Evaluacion
    line(14.0, 17.0, 14.5, 19.0, "1:N")
    line(14.0, 16.5, 14.5, 15.5, "1:N")
    line(14.0, 16.0, 14.5, 12.5, "1:N")

    # Conversacion -> Mensaje
    line(24.5, 17.5, 24.5, 16.0, "1:N")

    # === ZONAS DE DOMINIO (fondo sutil) ===
    zones = [
        (0.3, 16.7, 3.4, 4.5, "AUTENTICACION", c['core'], c['core_bg']),
        (0.3, 12.5, 3.4, 4.0, "", c['core'], c['core_bg']),
        (3.8, 7.5, 3.4, 14.0, "PERSONAS", c['people'], c['people_bg']),
        (7.3, 6.5, 3.4, 14.5, "ESTRUCTURA ACADEMICA", c['academic'], c['academic_bg']),
        (10.8, 9.0, 3.4, 11.5, "CURSOS Y MATRICULAS", c['academic'], c['academic_bg']),
        (14.3, 5.3, 3.4, 15.5, "REGISTROS", c['records'], c['records_bg']),
        (17.8, 13.0, 3.4, 7.5, "OPERACIONES", c['ops'], c['ops_bg']),
        (21.3, 9.0, 3.4, 11.0, "COMUNICACION", c['comm'], c['comm_bg']),
    ]

    for x, y, w, h, label, color, bg in zones:
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.25",
                               facecolor=bg, edgecolor=color, linewidth=1.2, linestyle='--', alpha=0.18, zorder=0)
        ax.add_patch(rect)
        if label:
            ax.text(x + w/2, y + h - 0.25, label, ha='center', va='center',
                    fontsize=9, fontweight='bold', color=color, alpha=0.55, zorder=1)

    # LEYENDA
    lx, ly = 0.8, 1.2
    ax.text(lx, ly + 1.3, "LEYENDA DE ATRIBUTOS", fontsize=11, fontweight='bold', color='#333')
    items = [("PK", "#c62828", "Clave Primaria (_id)"),
             ("FK", "#1565c0", "Clave Foranea (ObjectId ref)"),
             ("UK", "#388e3c", "Indice Unico"),
             ("IX", "#7b1fa2", "Indice Simple/Compuesto"),
             ("TI", "#f57c00", "Indice de Texto")]
    for i, (tag, col, desc) in enumerate(items):
        ax.text(lx + 0.3, ly + 0.8 - i*0.4, tag, fontsize=9, fontweight='bold', color=col,
                bbox=dict(boxstyle='round,pad=0.2', facecolor='white', edgecolor=col, alpha=0.2))
        ax.text(lx + 1.2, ly + 0.8 - i*0.4, desc, fontsize=9, color='#444', va='center')

    # Dominios
    dom_x = 10.0
    doms = [('Autenticacion', c['core'], c['core_bg']),
            ('Personas', c['people'], c['people_bg']),
            ('Academico', c['academic'], c['academic_bg']),
            ('Registros', c['records'], c['records_bg']),
            ('Comunicacion', c['comm'], c['comm_bg']),
            ('Operaciones', c['ops'], c['ops_bg'])]
    for i, (name, dc, dbg) in enumerate(doms):
        ax.add_patch(FancyBboxPatch((dom_x + i*2.8, ly-0.1), 2.5, 0.38, boxstyle="round,pad=0.02",
                                     facecolor=dbg, edgecolor=dc, linewidth=1.5))
        ax.text(dom_x + i*2.8 + 1.25, ly+0.09, name, ha='center', va='center', fontsize=8, color=dc)

    plt.tight_layout()
    plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico_sanmartin.png',
                dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
    print("Modelo logico limpio generado.")

draw_logical_clean()
