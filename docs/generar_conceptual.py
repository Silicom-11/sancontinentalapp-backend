import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, Polygon, Circle, Rectangle
import numpy as np

# ============================================================
# MODELO CONCEPTUAL - Diagrama de Entidad-Relación (Chen)
# ============================================================

def draw_conceptual():
    fig, ax = plt.subplots(1, 1, figsize=(24, 16))
    ax.set_xlim(0, 24)
    ax.set_ylim(0, 16)
    ax.axis('off')
    ax.set_facecolor('#fafafa')
    fig.patch.set_facecolor('#fafafa')

    # Colores
    c_entity = '#e3f2fd'
    c_entity_border = '#1565c0'
    c_relation = '#fff3e0'
    c_relation_border = '#e65100'
    c_text = '#1a237e'
    c_line = '#607d8b'

    def entity(x, y, w, h, name):
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.15",
                               facecolor=c_entity, edgecolor=c_entity_border, linewidth=2, zorder=3)
        ax.add_patch(rect)
        ax.text(x + w/2, y + h/2, name, ha='center', va='center', fontsize=9,
                fontweight='bold', color=c_entity_border, zorder=4)

    def relation(x, y, size, name, cardinality=""):
        # Rombo
        diamond = Polygon([
            [x, y + size/2], [x + size/2, y + size],
            [x + size, y + size/2], [x + size/2, y]
        ], closed=True, facecolor=c_relation, edgecolor=c_relation_border, linewidth=2, zorder=3)
        ax.add_patch(diamond)
        ax.text(x + size/2, y + size/2, name, ha='center', va='center', fontsize=8,
                fontweight='bold', color=c_relation_border, zorder=4)
        if cardinality:
            ax.text(x + size/2, y + size/2 - 0.35, cardinality, ha='center', va='center',
                    fontsize=7, color='#666', zorder=4)

    def connect(x1, y1, x2, y2, label1="", label2=""):
        ax.plot([x1, x2], [y1, y2], color=c_line, linewidth=1.5, zorder=1)
        if label1:
            mid_x, mid_y = (x1+x2)/2, (y1+y2)/2
            ax.text(mid_x-0.15, mid_y+0.15, label1, fontsize=7, color='#555',
                    bbox=dict(boxstyle='round,pad=0.15', facecolor='white', edgecolor='none', alpha=0.8), zorder=2)

    # Título
    ax.text(12, 15.5, 'MODELO CONCEPTUAL DE DATOS', ha='center', va='center',
            fontsize=20, fontweight='bold', color=c_entity_border)
    ax.text(12, 15.1, 'IEP Continental Americano - Backend Java | Notación Entidad-Relación de Chen',
            ha='center', va='center', fontsize=12, color='#666')

    # --- ENTIDADES ---
    # Fila superior
    entity(1.0, 12.5, 2.2, 0.7, "Institución")
    entity(4.0, 12.5, 2.2, 0.7, "Año Académico")
    entity(7.0, 12.5, 2.2, 0.7, "Nivel de Grado")
    entity(10.0, 12.5, 2.0, 0.7, "Asignatura")
    entity(12.8, 12.5, 1.8, 0.7, "Aula")

    # Fila de personas
    entity(0.5, 9.5, 2.0, 0.7, "Usuario")
    entity(3.2, 9.5, 2.0, 0.7, "Estudiante")
    entity(5.9, 9.5, 2.0, 0.7, "Docente")
    entity(8.6, 9.5, 1.8, 0.7, "Padre")

    # Fila académica
    entity(11.0, 9.5, 2.0, 0.7, "Curso")
    entity(13.5, 9.5, 2.4, 0.7, "Sección Curso")
    entity(16.5, 9.5, 2.0, 0.7, "Matrícula")

    # Fila registros
    entity(1.0, 6.5, 2.0, 0.7, "Nota")
    entity(3.8, 6.5, 2.2, 0.7, "Asistencia")
    entity(6.6, 6.5, 2.2, 0.7, "Evaluación")
    entity(9.4, 6.5, 2.2, 0.7, "Justificación")
    entity(12.2, 6.5, 2.0, 0.7, "Evento")
    entity(14.8, 6.5, 2.2, 0.7, "Ubicación")

    # Fila comunicación
    entity(17.5, 12.5, 2.4, 0.7, "Conversación")
    entity(20.5, 12.5, 2.0, 0.7, "Mensaje")
    entity(18.5, 9.5, 2.4, 0.7, "Notificación")

    # --- RELACIONES ---
    relation(2.9, 13.0, 0.7, "tiene", "1:N")
    relation(6.0, 13.0, 0.7, "define", "1:N")
    relation(9.0, 13.0, 0.7, "imparte", "1:N")
    relation(11.6, 13.0, 0.7, "contiene", "1:N")
    relation(14.2, 13.0, 0.7, "agrupa", "1:N")

    relation(1.8, 10.8, 0.7, "es", "1:1")
    relation(4.5, 10.8, 0.7, "es", "1:1")
    relation(7.2, 10.8, 0.7, "es", "1:1")
    relation(4.0, 8.5, 0.7, "matricula", "1:N")
    relation(6.5, 8.5, 0.7, "dicta", "1:N")
    relation(9.0, 8.5, 0.7, "apoderado", "1:N")
    relation(12.0, 8.5, 0.7, "genera", "1:N")
    relation(14.5, 8.5, 0.7, "recibe", "1:N")
    relation(10.5, 10.8, 0.7, "compone", "1:N")
    relation(13.0, 10.8, 0.7, "aloja", "1:N")

    relation(1.8, 7.5, 0.7, "registra", "1:N")
    relation(4.6, 7.5, 0.7, "toma", "1:N")
    relation(7.4, 7.5, 0.7, "crea", "1:N")
    relation(10.2, 7.5, 0.7, "presenta", "1:N")
    relation(12.8, 7.5, 0.7, "organiza", "1:N")
    relation(15.4, 7.5, 0.7, "reporta", "1:N")

    relation(18.8, 13.0, 0.7, "participa", "N:M")
    relation(21.2, 13.0, 0.7, "contiene", "1:N")
    relation(19.2, 10.8, 0.7, "envía", "1:N")
    relation(17.0, 10.8, 0.7, "recibe", "1:N")

    # --- CONEXIONES ---
    # Institucion -> relaciones
    connect(3.2, 12.85, 2.9, 13.0)   # institucion -> tiene
    connect(3.6, 13.35, 4.0, 13.0)   # tiene -> anio academico
    connect(4.0, 12.85, 2.9, 13.7)   # institucion -> tiene (abajo)

    connect(6.2, 12.85, 6.0, 13.0)   # institucion -> define
    connect(7.0, 12.85, 6.7, 13.35)  # define -> nivel

    connect(9.0, 12.85, 9.0, 13.0)   # institucion -> imparte
    connect(10.0, 12.85, 9.7, 13.35) # imparte -> asignatura

    connect(11.8, 12.85, 11.6, 13.0) # anio -> contiene
    connect(12.8, 12.85, 12.5, 13.35)# contiene -> aula

    connect(12.8, 12.85, 12.8, 13.0)  # nivel -> agrupa (manual adjust)
    connect(13.8, 12.85, 14.2, 13.35) # agrupa -> aula

    # Usuario -> perfiles
    connect(2.5, 9.85, 2.2, 10.5)   # usuario -> es -> estudiante
    connect(3.2, 9.85, 3.5, 10.5)
    connect(4.9, 9.85, 4.8, 10.5)   # usuario -> es -> docente
    connect(5.9, 9.85, 5.6, 10.5)
    connect(7.2, 9.85, 7.4, 10.5)   # usuario -> es -> padre
    connect(8.6, 9.85, 8.1, 10.5)

    # Estudiante -> relaciones
    connect(4.2, 9.5, 4.0, 9.2)     # estudiante -> matricula
    connect(4.7, 9.2, 5.5, 9.5)     # matricula -> aula/curso area
    connect(4.2, 9.5, 4.35, 8.85)   # estudiante -> nota
    connect(3.2, 6.85, 3.2, 6.5)    # nota caida
    connect(4.2, 9.5, 4.9, 7.2)     # estudiante -> asistencia
    connect(4.2, 9.5, 4.9, 7.2)     # ajuste
    connect(4.2, 9.5, 7.7, 7.2)     # estudiante -> justificacion
    connect(4.2, 9.5, 15.7, 7.2)    # estudiante -> ubicacion

    # Docente -> relaciones
    connect(6.4, 9.5, 6.5, 9.2)     # docente -> dicta
    connect(6.5, 9.2, 7.5, 9.5)     # dicta -> curso
    connect(6.4, 9.5, 6.1, 7.2)     # docente -> evaluacion
    connect(6.4, 9.5, 4.1, 7.2)     # docente -> asistencia
    connect(6.4, 9.5, 2.3, 7.2)     # docente -> nota

    # Padre -> estudiante
    connect(8.6, 9.5, 8.7, 9.2)     # padre -> apoderado
    connect(8.7, 9.2, 7.5, 9.5)     # apoderado -> estudiante

    # Curso / Seccion
    connect(11.8, 9.5, 11.5, 9.2)   # curso -> compone
    connect(11.5, 9.2, 11.0, 9.5)
    connect(13.5, 9.5, 13.0, 9.2)   # seccion -> aloja
    connect(13.0, 9.2, 12.5, 9.5)

    # Matricula
    connect(17.5, 9.5, 16.2, 9.2)   # matricula conecta estudiante+aula

    # Conversacion / Mensaje
    connect(18.5, 12.5, 18.8, 12.35) # conversacion -> participa
    connect(20.5, 12.5, 20.9, 12.35) # conversacion -> contiene -> mensaje

    # Notificacion / Ubicacion / Evento / Usuario
    connect(19.7, 9.5, 19.2, 10.2)   # usuario -> envia notif
    connect(19.7, 9.5, 19.2, 10.2)   # ajuste
    connect(2.5, 9.5, 2.5, 7.2)      # usuario -> reporta ubicacion
    connect(2.5, 9.5, 13.2, 7.2)     # usuario -> organiza evento

    # Leyenda
    leg_x, leg_y = 0.5, 1.0
    ax.add_patch(FancyBboxPatch((leg_x, leg_y), 1.2, 0.5, boxstyle="round,pad=0.02",
                                 facecolor=c_entity, edgecolor=c_entity_border, linewidth=1.5))
    ax.text(leg_x + 1.4, leg_y + 0.25, "Entidad", va='center', fontsize=10, color='#333')

    ax.add_patch(Polygon([[leg_x+3.0, leg_y+0.25], [leg_x+3.4, leg_y+0.5],
                          [leg_x+3.8, leg_y+0.25], [leg_x+3.4, leg_y]],
                         closed=True, facecolor=c_relation, edgecolor=c_relation_border, linewidth=1.5))
    ax.text(leg_x + 4.0, leg_y + 0.25, "Relación", va='center', fontsize=10, color='#333')

    ax.plot([leg_x+5.5, leg_x+6.5], [leg_y+0.25, leg_y+0.25], color=c_line, linewidth=1.5)
    ax.text(leg_x + 6.7, leg_y + 0.25, "Asociación", va='center', fontsize=10, color='#333')

    ax.text(leg_x + 8.5, leg_y + 0.25, "1:1 = Uno a Uno    1:N = Uno a Muchos    N:M = Muchos a Muchos",
            va='center', fontsize=10, color='#555')

    plt.tight_layout()
    plt.savefig(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_conceptual_sanmartin.png',
                dpi=200, bbox_inches='tight', facecolor=fig.get_facecolor())
    print("Diagrama conceptual generado: modelo_conceptual_sanmartin.png")

draw_conceptual()
