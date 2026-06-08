import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, ConnectionPatch
import numpy as np

# ============================================================
# MODELO LÓGICO - Diagrama de Entidad-Relación Extendido
# ============================================================

def draw_logical():
    fig, ax = plt.subplots(1, 1, figsize=(30, 22))
    ax.set_xlim(0, 30)
    ax.set_ylim(0, 22)
    ax.axis('off')
    ax.set_facecolor('#f8f9fa')
    fig.patch.set_facecolor('#f8f9fa')

    # Colores por dominio
    colors = {
        'auth': {'bg': '#e3f2fd', 'border': '#1565c0', 'header': '#bbdefb'},
        'people': {'bg': '#e8f5e9', 'border': '#2e7d32', 'header': '#c8e6c9'},
        'academic': {'bg': '#fff3e0', 'border': '#e65100', 'header': '#ffe0b2'},
        'records': {'bg': '#fce4ec', 'border': '#c2185b', 'header': '#f8bbd9'},
        'comm': {'bg': '#f3e5f5', 'border': '#7b1fa2', 'header': '#e1bee7'},
        'ops': {'bg': '#e0f2f1', 'border': '#00695c', 'header': '#b2dfdb'},
    }

    def draw_box(ax, x, y, w, h, title, attrs, color_set):
        header_h = 0.45
        # Header
        hdr = FancyBboxPatch((x, y + h - header_h), w, header_h,
                              boxstyle="round,pad=0.02,rounding_size=0.1",
                              facecolor=color_set['header'], edgecolor=color_set['border'],
                              linewidth=2, zorder=3)
        ax.add_patch(hdr)
        ax.text(x + w/2, y + h - header_h/2, title, ha='center', va='center',
                fontsize=10, fontweight='bold', color=color_set['border'], zorder=4)

        # Body
        body = FancyBboxPatch((x, y), w, h - header_h,
                               boxstyle="round,pad=0.02,rounding_size=0.1",
                               facecolor=color_set['bg'], edgecolor=color_set['border'],
                               linewidth=1.5, zorder=2)
        ax.add_patch(body)

        line_h = 0.30
        start_y = y + h - header_h - 0.30
        for i, (name, tags) in enumerate(attrs):
            fy = start_y - i * line_h
            if fy < y + 0.08:
                break
            color = '#333'
            if 'PK' in tags:
                color = '#d32f2f'
            elif 'FK' in tags:
                color = '#1976d2'
            ax.text(x + 0.12, fy, name, ha='left', va='center', fontsize=7.5,
                    color=color, fontweight='bold' if 'PK' in tags else 'normal', zorder=4)
            tag_text = " ".join(tags)
            if tag_text:
                ax.text(x + w - 0.12, fy, tag_text, ha='right', va='center', fontsize=6.5,
                        color='#777', zorder=4)

    # --- ENTIDADES Y ATRIBUTOS ---
    # Columna 1: Autenticación
    draw_box(ax, 0.4, 17.5, 3.0, 3.8, "USUARIO", [
        ("id_usuario", ["PK"]),
        ("nombre", []),
        ("apellido", []),
        ("email", ["UK"]),
        ("dni", ["UK"]),
        ("contraseña", []),
        ("telefono", []),
        ("rol", ["IX"]),
        ("foto", []),
        ("direccion", []),
        ("activo", []),
        ("verificado", []),
        ("ultimo_login", []),
    ], colors['auth'])

    draw_box(ax, 0.4, 14.0, 3.0, 3.0, "INSTITUCIÓN", [
        ("id_institucion", ["PK"]),
        ("nombre", []),
        ("codigo", ["UK"]),
        ("logo", []),
        ("direccion", []),
        ("telefono", []),
        ("email", []),
        ("sitio_web", []),
        ("director", ["FK"]),
        ("sistema_eval", []),
    ], colors['academic'])

    # Columna 2: Personas
    draw_box(ax, 4.0, 17.5, 3.0, 4.0, "ESTUDIANTE", [
        ("id_estudiante", ["PK"]),
        ("nombre", ["TI"]),
        ("apellido", ["TI"]),
        ("dni", ["UK"]),
        ("email", ["UK"]),
        ("telefono", []),
        ("fecha_nac", []),
        ("genero", []),
        ("grado", []),
        ("seccion", []),
        ("cod_estudiante", ["UK"]),
        ("num_matricula", ["UK"]),
        ("apoderados", []),
        ("info_medica", []),
        ("documentos", []),
        ("estado", []),
        ("activo", []),
        ("id_usuario", ["FK"]),
    ], colors['people'])

    draw_box(ax, 4.0, 13.5, 3.0, 3.5, "DOCENTE", [
        ("id_docente", ["PK"]),
        ("nombre", ["TI"]),
        ("apellido", ["TI"]),
        ("dni", ["UK"]),
        ("email", ["UK"]),
        ("telefono", []),
        ("fecha_nac", []),
        ("cod_empleado", ["UK"]),
        ("especialidad", []),
        ("grado_academico", []),
        ("titulo_prof", []),
        ("certificaciones", []),
        ("tipo_contrato", []),
        ("salario", []),
        ("cuenta_banco", []),
        ("documentos", []),
        ("activo", []),
        ("id_usuario", ["FK"]),
        ("id_institucion", ["FK"]),
    ], colors['people'])

    draw_box(ax, 4.0, 9.8, 3.0, 3.3, "PADRE", [
        ("id_padre", ["PK"]),
        ("nombre", ["TI"]),
        ("apellido", ["TI"]),
        ("dni", ["UK"]),
        ("email", ["UK"]),
        ("telefono", ["IX"]),
        ("telefono2", []),
        ("fecha_nac", []),
        ("ocupacion", []),
        ("lugar_trabajo", []),
        ("hijos", []),
        ("preferencias_notif", []),
        ("documentos", []),
        ("tokens_push", []),
        ("activo", []),
        ("verificado", []),
        ("id_usuario", ["FK"]),
    ], colors['people'])

    # Columna 3: Académico
    draw_box(ax, 7.6, 17.8, 3.0, 3.2, "AÑO ACADÉMICO", [
        ("id_anio", ["PK"]),
        ("id_institucion", ["FK"]),
        ("anio", []),
        ("nombre", []),
        ("fecha_inicio", []),
        ("fecha_fin", []),
        ("periodos", []),
        ("fechas_importantes", []),
        ("actual", ["IX"]),
        ("estado", []),
        ("estadisticas", []),
        ("activo", []),
    ], colors['academic'])

    draw_box(ax, 7.6, 14.5, 3.0, 2.8, "NIVEL DE GRADO", [
        ("id_nivel", ["PK"]),
        ("id_institucion", ["FK"]),
        ("nombre", []),
        ("nombre_corto", []),
        ("nivel", []),
        ("tipo", []),
        ("orden", []),
        ("descripcion", []),
        ("activo", []),
    ], colors['academic'])

    draw_box(ax, 7.6, 11.2, 3.0, 2.9, "ASIGNATURA", [
        ("id_asignatura", ["PK"]),
        ("id_institucion", ["FK"]),
        ("nombre", []),
        ("codigo", ["UK"]),
        ("descripcion", []),
        ("niveles", ["FK"]),
        ("aplicable_a", []),
        ("horas_semana", []),
        ("obligatoria", []),
        ("area", []),
        ("pesos_default", []),
        ("competencias", []),
        ("activo", []),
    ], colors['academic'])

    draw_box(ax, 7.6, 7.8, 3.0, 3.0, "AULA", [
        ("id_aula", ["PK"]),
        ("id_nivel", ["FK"]),
        ("id_anio", ["FK"]),
        ("seccion", []),
        ("turno", []),
        ("tutor", ["FK"]),
        ("capacidad", []),
        ("ubicacion", []),
        ("estadisticas", []),
        ("activo", []),
    ], colors['academic'])

    # Columna 4: Cursos y Matrículas
    draw_box(ax, 11.2, 17.5, 3.0, 3.2, "CURSO", [
        ("id_curso", ["PK"]),
        ("nombre", []),
        ("codigo", ["UK"]),
        ("descripcion", []),
        ("grado", []),
        ("seccion", []),
        ("id_docente", ["FK"]),
        ("estudiantes", ["FK"]),
        ("horario", []),
        ("pesos_eval", []),
        ("anio_academico", []),
        ("periodo", []),
        ("activo", []),
    ], colors['academic'])

    draw_box(ax, 11.2, 13.8, 3.2, 3.2, "SECCIÓN CURSO", [
        ("id_sec_curso", ["PK"]),
        ("id_asignatura", ["FK"]),
        ("id_aula", ["FK"]),
        ("id_docente", ["FK"]),
        ("id_anio", ["FK"]),
        ("horario", []),
        ("pesos_eval", []),
        ("eval_periodo", []),
        ("estadisticas", []),
        ("recursos", []),
        ("estudiantes", ["FK"]),
        ("activo", []),
    ], colors['academic'])

    draw_box(ax, 11.2, 10.0, 3.0, 3.3, "MATRÍCULA", [
        ("id_matricula", ["PK"]),
        ("id_estudiante", ["FK"]),
        ("id_aula", ["FK"]),
        ("id_anio", ["FK"]),
        ("fecha_matricula", []),
        ("num_matricula", ["UK"]),
        ("estado", []),
        ("fecha_estado", []),
        ("motivo_estado", []),
        ("tipo_matricula", []),
        ("historial_estado", []),
        ("documentos", []),
        ("id_matriculador", ["FK"]),
        ("activo", []),
    ], colors['academic'])

    # Columna 5: Registros Académicos
    draw_box(ax, 14.8, 17.5, 3.0, 3.5, "NOTA", [
        ("id_nota", ["PK"]),
        ("id_estudiante", ["FK"]),
        ("id_curso", ["FK"]),
        ("bimestre", []),
        ("anio_academico", []),
        ("puntajes", []),
        ("promedio", []),
        ("estado", []),
        ("fecha_cierre", []),
        ("cerrado_por", []),
        ("fecha_publicacion", []),
        ("id_docente", ["FK"]),
    ], colors['records'])

    draw_box(ax, 14.8, 13.5, 3.0, 3.5, "ASISTENCIA", [
        ("id_asistencia", ["PK"]),
        ("id_estudiante", ["FK"]),
        ("id_curso", ["FK"]),
        ("id_docente", ["FK"]),
        ("fecha", ["IX"]),
        ("estado", []),
        ("hora_llegada", []),
        ("observaciones", []),
        ("id_justificacion", ["FK"]),
    ], colors['records'])

    draw_box(ax, 14.8, 9.8, 3.0, 3.2, "EVALUACIÓN", [
        ("id_evaluacion", ["PK"]),
        ("id_curso", ["FK"]),
        ("id_docente", ["FK"]),
        ("nombre", []),
        ("tipo", []),
        ("bimestre", []),
        ("nota_max", []),
        ("peso", []),
        ("fecha", []),
        ("descripcion", []),
        ("anio_academico", []),
        ("activo", []),
        ("orden", []),
    ], colors['records'])

    draw_box(ax, 14.8, 6.2, 3.0, 3.2, "EVENTO", [
        ("id_evento", ["PK"]),
        ("titulo", []),
        ("fecha", ["IX"]),
        ("hora", []),
        ("tipo", ["IX"]),
        ("descripcion", []),
        ("lugar", []),
        ("participantes", []),
        ("notif_estudiantes", []),
        ("notif_padres", []),
        ("notif_docentes", []),
        ("creado_por", ["FK"]),
        ("activo", ["IX"]),
    ], colors['records'])

    # Columna 6: Operaciones y Comunicación
    draw_box(ax, 18.4, 17.5, 3.0, 3.0, "JUSTIFICACIÓN", [
        ("id_justif", ["PK"]),
        ("id_estudiante", ["FK"]),
        ("id_padre", ["FK"]),
        ("fechas", []),
        ("motivo", []),
        ("observaciones", []),
        ("documentos", []),
        ("cursos_afectados", ["FK"]),
        ("estado", ["IX"]),
        ("revisado_por", ["FK"]),
        ("fecha_revision", []),
        ("nota_revision", []),
    ], colors['ops'])

    draw_box(ax, 18.4, 13.8, 3.0, 3.2, "UBICACIÓN", [
        ("id_ubicacion", ["PK"]),
        ("id_usuario", ["FK", "IX"]),
        ("coordenadas", []),
        ("info_dispositivo", []),
        ("estado_sesion", []),
        ("tipo_actualiz", []),
        ("direccion", []),
        ("nivel_bateria", []),
        ("tipo_red", []),
        ("timestamp_cliente", []),
    ], colors['ops'])

    draw_box(ax, 22.0, 17.5, 3.0, 3.0, "CONVERSACIÓN", [
        ("id_convers", ["PK"]),
        ("participantes", ["FK"]),
        ("tipo", []),
        ("nombre", []),
        ("ultimo_mensaje", []),
        ("sin_leer", []),
        ("activo", []),
        ("metadata", []),
    ], colors['comm'])

    draw_box(ax, 22.0, 13.8, 3.0, 3.0, "MENSAJE", [
        ("id_mensaje", ["PK"]),
        ("id_convers", ["FK"]),
        ("id_remitente", ["FK"]),
        ("contenido", []),
        ("tipo", []),
        ("adjuntos", []),
        ("leido_por", []),
        ("eliminado", []),
    ], colors['comm'])

    draw_box(ax, 22.0, 10.2, 3.0, 3.2, "NOTIFICACIÓN", [
        ("id_notif", ["PK"]),
        ("id_destinatario", ["FK"]),
        ("titulo", []),
        ("mensaje", []),
        ("tipo", []),
        ("datos", []),
        ("leido", []),
        ("fecha_lectura", []),
        ("fecha_expira", []),
    ], colors['comm'])

    # --- LÍNEAS DE RELACIÓN ---
    def line(x1, y1, x2, y2, color='#90a4ae'):
        ax.plot([x1, x2], [y1, y2], color=color, linewidth=1.3, zorder=1)

    # Institucion -> Año Académico, Nivel, Asignatura, Docente
    line(3.4, 15.5, 7.6, 18.5)
    line(3.4, 15.2, 7.6, 15.5)
    line(3.4, 14.9, 7.6, 12.5)
    line(3.4, 14.6, 4.0, 15.0)  # a docente

    # Usuario -> Estudiante, Docente, Padre
    line(3.4, 19.4, 4.0, 19.5)
    line(3.4, 19.1, 4.0, 16.2)
    line(3.4, 18.8, 4.0, 12.5)

    # Año Académico -> Aula, Seccion Curso, Matricula
    line(10.6, 18.5, 11.2, 18.8)
    line(10.6, 18.2, 11.2, 16.0)
    line(10.6, 17.9, 11.2, 12.5)

    # Nivel -> Aula, Asignatura
    line(10.6, 15.5, 11.2, 16.2)
    line(10.6, 15.2, 11.2, 12.5)

    # Aula -> Seccion Curso, Matricula
    line(10.6, 9.5, 11.2, 10.5)
    line(10.6, 9.2, 11.2, 12.0)

    # Asignatura -> Seccion Curso
    line(10.6, 12.5, 11.2, 14.0)

    # Estudiante -> Matricula, Nota, Asistencia, Justificacion, Ubicacion
    line(7.0, 17.5, 7.6, 12.0)
    line(7.0, 17.2, 11.2, 19.0)
    line(7.0, 16.9, 11.2, 15.5)
    line(7.0, 16.6, 14.8, 18.5)
    line(7.0, 16.3, 14.8, 15.0)
    line(7.0, 16.0, 18.4, 18.5)
    line(7.0, 15.7, 18.4, 15.0)

    # Docente -> Curso, Seccion Curso, Nota, Asistencia, Evaluacion
    line(7.0, 15.0, 11.2, 18.5)
    line(7.0, 14.7, 11.2, 15.0)
    line(7.0, 14.4, 14.8, 19.0)
    line(7.0, 14.1, 14.8, 15.5)
    line(7.0, 13.8, 14.8, 11.5)

    # Padre -> Estudiante, Justificacion
    line(7.0, 11.5, 7.0, 11.0)
    line(7.0, 11.2, 7.6, 11.0)
    line(7.0, 10.9, 18.4, 18.5)

    # Curso -> Nota, Asistencia, Evaluacion
    line(14.2, 17.0, 14.8, 19.0)
    line(14.2, 16.7, 14.8, 16.5)
    line(14.2, 16.4, 14.8, 12.5)

    # Usuario -> Evento, Ubicacion, Notificacion, Conversacion, Mensaje
    line(3.4, 18.5, 14.8, 7.5)
    line(3.4, 18.2, 18.4, 15.0)
    line(3.4, 17.9, 22.0, 11.5)
    line(3.4, 17.6, 22.0, 18.5)
    line(3.4, 17.3, 22.0, 15.0)

    # Conversacion -> Mensaje
    line(25.0, 16.5, 25.0, 15.5)

    # Leyenda
    lx, ly = 0.5, 1.0
    legend_items = [
        ("PK", "#d32f2f", "Clave Primaria (_id)"),
        ("FK", "#1976d2", "Clave Foránea (ObjectId ref)"),
        ("UK", "#388e3c", "Índice Único"),
        ("IX", "#7b1fa2", "Índice Simple/Compuesto"),
        ("TI", "#f57c00", "Índice de Texto"),
    ]
    ax.text(lx, ly + 1.0, "LEYENDA DE ATRIBUTOS", fontsize=11, fontweight='bold', color='#333')
    for i, (tag, color, desc) in enumerate(legend_items):
        ax.text(lx + 0.3, ly + 0.55 - i*0.35, tag, fontsize=9, fontweight='bold', color=color,
                bbox=dict(boxstyle='round,pad=0.2', facecolor='white', edgecolor=color, alpha=0.15))
        ax.text(lx + 1.2, ly + 0.55 - i*0.35, desc, fontsize=9, color='#444', va='center')

    # Dominios
    dom_x = 9.0
    ax.add_patch(FancyBboxPatch((dom_x, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['auth']['bg'], edgecolor=colors['auth']['border'], linewidth=1.5))
    ax.text(dom_x + 0.5, ly+0.075, "Autenticación", ha='center', va='center', fontsize=8, color=colors['auth']['border'])

    ax.add_patch(FancyBboxPatch((dom_x+1.3, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['people']['bg'], edgecolor=colors['people']['border'], linewidth=1.5))
    ax.text(dom_x + 1.8, ly+0.075, "Personas", ha='center', va='center', fontsize=8, color=colors['people']['border'])

    ax.add_patch(FancyBboxPatch((dom_x+2.6, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['academic']['bg'], edgecolor=colors['academic']['border'], linewidth=1.5))
    ax.text(dom_x + 3.1, ly+0.075, "Académico", ha='center', va='center', fontsize=8, color=colors['academic']['border'])

    ax.add_patch(FancyBboxPatch((dom_x+3.9, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['records']['bg'], edgecolor=colors['records']['border'], linewidth=1.5))
    ax.text(dom_x + 4.4, ly+0.075, "Registros", ha='center', va='center', fontsize=8, color=colors['records']['border'])

    ax.add_patch(FancyBboxPatch((dom_x+5.2, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['comm']['bg'], edgecolor=colors['comm']['border'], linewidth=1.5))
    ax.text(dom_x + 5.7, ly+0.075, "Comunicación", ha='center', va='center', fontsize=8, color=colors['comm']['border'])

    ax.add_patch(FancyBboxPatch((dom_x+6.5, ly-0.1), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor=colors['ops']['bg'], edgecolor=colors['ops']['border'], linewidth=1.5))
    ax.text(dom_x + 7.0, ly+0.075, "Operaciones", ha='center', va='center', fontsize=8, color=colors['ops']['border'])

    # Título
    ax.text(15, 21.5, 'MODELO LÓGICO DE DATOS', ha='center', va='center',
            fontsize=22, fontweight='bold', color='#1a237e')
    ax.text(15, 21.1, 'IEP Continental Americano - Backend Java | MongoDB Document Model | 21 Entidades',
            ha='center', va='center', fontsize=12, color='#666')

    plt.tight_layout()
    plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_logico_sanmartin.png',
                dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
    print("Diagrama logico generado: modelo_logico_sanmartin.png")

draw_logical()
