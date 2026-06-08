import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, Rectangle
import numpy as np

# ============================================================
# MODELO CONCEPTUAL LIMPIO - Layout Jerarquico por Dominios
# ============================================================

def draw_conceptual_clean():
    fig, ax = plt.subplots(1, 1, figsize=(28, 18))
    ax.set_xlim(0, 28)
    ax.set_ylim(0, 18)
    ax.axis('off')
    ax.set_facecolor('#fafbfc')
    fig.patch.set_facecolor('#fafbfc')

    # Paleta de colores por dominio
    domain_colors = {
        'core': '#1565c0',
        'people': '#2e7d32',
        'academic': '#e65100',
        'records': '#c2185b',
        'comm': '#7b1fa2',
        'ops': '#00695c',
    }

    domain_bg = {
        'core': '#e3f2fd',
        'people': '#e8f5e9',
        'academic': '#fff3e0',
        'records': '#fce4ec',
        'comm': '#f3e5f5',
        'ops': '#e0f2f1',
    }

    def entity_box(x, y, w, h, name, domain):
        c = domain_colors[domain]
        bg = domain_bg[domain]
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.12",
                               facecolor=bg, edgecolor=c, linewidth=2.2, zorder=3)
        ax.add_patch(rect)
        ax.text(x + w/2, y + h/2, name, ha='center', va='center', fontsize=9.5,
                fontweight='bold', color=c, zorder=4)

    def relation_label(x1, y1, x2, y2, label, color='#78909c'):
        mx, my = (x1+x2)/2, (y1+y2)/2
        ax.plot([x1, x2], [y1, y2], color=color, linewidth=1.4, zorder=1)
        # Pequeno fondo blanco para la etiqueta
        ax.text(mx, my, label, ha='center', va='center', fontsize=7.5, color='#455a64',
                bbox=dict(boxstyle='round,pad=0.18', facecolor='white', edgecolor='#cfd8dc', alpha=0.95), zorder=2)

    # Titulo
    ax.text(14, 17.4, 'MODELO CONCEPTUAL DE DATOS', ha='center', va='center',
            fontsize=22, fontweight='bold', color='#1a237e')
    ax.text(14, 17.0, 'IEP Continental Americano - Backend Java | Notacion Entidad-Relacion de Chen',
            ha='center', va='center', fontsize=11, color='#666')

    # === CAPA SUPERIOR: Estructura Institucional ===
    entity_box(1.0, 14.8, 2.6, 0.7, "Institucion", 'core')
    entity_box(4.5, 14.8, 2.6, 0.7, "Anio Academico", 'academic')
    entity_box(8.0, 14.8, 2.6, 0.7, "Nivel de Grado", 'academic')
    entity_box(11.5, 14.8, 2.4, 0.7, "Asignatura", 'academic')
    entity_box(14.9, 14.8, 2.0, 0.7, "Aula", 'academic')

    # === CAPA MEDIA: Personas y Cursos ===
    entity_box(1.0, 11.5, 2.4, 0.7, "Usuario", 'core')
    entity_box(4.2, 11.5, 2.4, 0.7, "Estudiante", 'people')
    entity_box(7.4, 11.5, 2.4, 0.7, "Docente", 'people')
    entity_box(10.6, 11.5, 2.2, 0.7, "Padre", 'people')
    entity_box(14.0, 11.5, 2.4, 0.7, "Curso", 'academic')
    entity_box(17.4, 11.5, 2.8, 0.7, "Seccion Curso", 'academic')
    entity_box(21.4, 11.5, 2.4, 0.7, "Matricula", 'academic')

    # === CAPA INFERIOR: Registros y Operaciones ===
    entity_box(1.0, 8.2, 2.2, 0.7, "Nota", 'records')
    entity_box(4.0, 8.2, 2.4, 0.7, "Asistencia", 'records')
    entity_box(7.2, 8.2, 2.4, 0.7, "Evaluacion", 'records')
    entity_box(10.4, 8.2, 2.4, 0.7, "Justificacion", 'ops')
    entity_box(13.6, 8.2, 2.2, 0.7, "Evento", 'records')
    entity_box(16.6, 8.2, 2.4, 0.7, "Ubicacion", 'ops')

    # === CAPA DERECHA: Comunicacion ===
    entity_box(21.4, 14.8, 2.6, 0.7, "Conversacion", 'comm')
    entity_box(24.6, 14.8, 2.4, 0.7, "Mensaje", 'comm')
    entity_box(21.4, 8.2, 2.6, 0.7, "Notificacion", 'comm')

    # === RELACIONES ESTRUCTURA SUPERIOR ===
    relation_label(3.6, 15.15, 4.5, 15.15, "1:N")
    relation_label(7.1, 15.15, 8.0, 15.15, "1:N")
    relation_label(10.6, 15.15, 11.5, 15.15, "1:N")
    relation_label(13.9, 15.15, 14.9, 15.15, "1:N")
    relation_label(9.3, 14.8, 9.3, 14.1, "1:N")  # Nivel -> Aula (vertical)
    relation_label(12.7, 14.8, 12.7, 14.1, "1:N")  # Asignatura -> Seccion Curso
    relation_label(15.9, 14.8, 15.9, 14.1, "1:N")  # Aula -> Seccion Curso
    relation_label(6.0, 14.8, 6.0, 14.1, "1:N")   # Anio -> Aula
    relation_label(6.0, 14.8, 19.0, 14.1, "1:N")  # Anio -> Matricula (larga)

    # === RELACIONES USUARIO -> PERFILES (1:1) ===
    relation_label(2.2, 11.5, 2.2, 10.8, "1:1")   # Usuario -> Estudiante
    relation_label(2.2, 11.2, 4.2, 11.5, "1:1")   # diagonal
    relation_label(2.2, 11.0, 7.4, 11.5, "1:1")
    relation_label(2.2, 10.8, 10.6, 11.5, "1:1")

    # === RELACIONES PERSONAS -> REGISTROS ===
    relation_label(5.4, 11.5, 5.4, 10.8, "1:N")   # Estudiante -> Nota (via)
    relation_label(5.4, 11.2, 2.1, 8.9, "1:N")    # Estudiante -> Nota
    relation_label(5.4, 11.2, 5.2, 8.9, "1:N")    # Estudiante -> Asistencia
    relation_label(5.4, 11.2, 11.6, 8.9, "1:N")   # Estudiante -> Justificacion
    relation_label(5.4, 11.2, 17.8, 8.9, "1:N")   # Estudiante -> Ubicacion
    relation_label(5.4, 11.2, 21.4, 11.9, "1:N")  # Estudiante -> Matricula

    relation_label(8.6, 11.5, 8.6, 10.8, "1:N")   # Docente ->
    relation_label(8.6, 11.2, 15.2, 11.5, "1:N")  # Docente -> Curso
    relation_label(8.6, 11.2, 18.8, 11.5, "1:N")  # Docente -> Seccion Curso
    relation_label(8.6, 11.2, 8.4, 8.9, "1:N")    # Docente -> Evaluacion
    relation_label(8.6, 11.2, 3.1, 8.9, "1:N")    # Docente -> Nota
    relation_label(8.6, 11.2, 5.2, 8.9, "1:N")    # Docente -> Asistencia

    relation_label(11.7, 11.5, 11.7, 10.8, "1:N") # Padre ->
    relation_label(11.7, 11.2, 10.4, 8.9, "1:N")  # Padre -> Justificacion
    relation_label(11.7, 11.2, 5.4, 10.8, "1:N")  # Padre -> Estudiante

    # === RELACIONES ACADEMICAS ===
    relation_label(15.2, 11.5, 15.2, 10.8, "1:N") # Curso ->
    relation_label(15.2, 11.2, 2.1, 8.9, "1:N")   # Curso -> Nota
    relation_label(15.2, 11.2, 5.2, 8.9, "1:N")   # Curso -> Asistencia
    relation_label(15.2, 11.2, 8.4, 8.9, "1:N")   # Curso -> Evaluacion

    relation_label(18.8, 11.5, 18.8, 10.8, "1:N") # Seccion Curso -> Matricula area
    relation_label(22.6, 11.5, 22.6, 10.8, "1:N") # Matricula -> Estudiante/Aula

    # === RELACIONES COMUNICACION ===
    relation_label(23.0, 14.8, 23.0, 14.1, "1:N") # Conversacion -> Mensaje
    relation_label(22.7, 14.8, 22.7, 12.2, "1:N") # Conversacion -> Notificacion (no directa, pero visual)
    relation_label(23.0, 14.8, 23.0, 13.5, "N:M") # Conversacion participantes

    # === RELACIONES USUARIO -> COMUNICACION/OPS ===
    relation_label(2.2, 11.5, 2.2, 8.9, "1:N")    # Usuario -> Ubicacion
    relation_label(2.2, 11.2, 14.7, 8.9, "1:N")   # Usuario -> Evento
    relation_label(2.2, 11.2, 22.7, 14.1, "1:N")  # Usuario -> Conversacion
    relation_label(2.2, 11.2, 25.8, 14.1, "1:N")  # Usuario -> Mensaje
    relation_label(2.2, 11.2, 22.7, 8.9, "1:N")   # Usuario -> Notificacion

    # === BORDES DE DOMINIO ===
    domains = [
        (0.5, 14.3, 17.0, 1.7, "ESTRUCTURA INSTITUCIONAL", domain_colors['academic'], domain_bg['academic']),
        (0.5, 11.0, 24.0, 1.7, "PERSONAS Y CURSOS", domain_colors['people'], domain_bg['people']),
        (0.5, 7.7, 19.5, 1.7, "REGISTROS Y OPERACIONES", domain_colors['records'], domain_bg['records']),
        (20.8, 7.7, 7.0, 1.7, "COMUNICACION", domain_colors['comm'], domain_bg['comm']),
        (20.8, 14.3, 7.0, 1.7, "", domain_colors['comm'], domain_bg['comm']),
    ]

    for x, y, w, h, label, c, bg in domains:
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.2",
                               facecolor=bg, edgecolor=c, linewidth=1.5, linestyle='--', alpha=0.25, zorder=0)
        ax.add_patch(rect)
        if label:
            ax.text(x + w/2, y + h - 0.2, label, ha='center', va='center',
                    fontsize=9, fontweight='bold', color=c, alpha=0.6, zorder=1)

    # Leyenda
    lx, ly = 0.8, 1.0
    ax.text(lx, ly + 0.9, "SIMBOLOGIA", fontsize=10, fontweight='bold', color='#333')
    ax.add_patch(FancyBboxPatch((lx, ly+0.35), 1.0, 0.35, boxstyle="round,pad=0.02",
                                 facecolor='#e3f2fd', edgecolor='#1565c0', linewidth=1.8))
    ax.text(lx+1.2, ly+0.52, "Entidad (Rectangulo)", va='center', fontsize=9, color='#333')
    ax.text(lx, ly-0.15, "1:1 = Uno a Uno    1:N = Uno a Muchos    N:M = Muchos a Muchos",
            fontsize=9, color='#555')

    plt.tight_layout()
    plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual_sanmartin.png',
                dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
    print("Modelo conceptual limpio generado.")

draw_conceptual_clean()
