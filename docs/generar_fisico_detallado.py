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

def f(name, typ, tag=''):
    tc = '#c62828' if tag=='PK' else ('#1565c0' if tag=='FK' else ('#2e7d32' if tag=='UK' else ('#f57c00' if tag in ('EMB','TI') else ('#7b1fa2' if tag=='IX' else '#888'))))
    # Escape HTML-unsafe characters in types
    typ = typ.replace('&','&amp;').replace('<','&lt;').replace('>','&gt;')
    return (name, typ, tag, tc)

nodes = {}

# ============ users ============
nodes['users'] = table('users', C_BLUE, H_BLUE, BG_BLUE, [
    f('_id','ObjectId','PK'), f('firstName','string',''), f('lastName','string',''),
    f('email','string','UK'), f('dni','string','UK'),
    f('password','string',''), f('phone','string',''), f('role','string','IX'),
    f('photo','string',''), f('address','emb<Address>','EMB'),
    f('isActive','boolean',''), f('isVerified','boolean',''),
    f('lastLogin','Instant',''), f('studentProfile','ref','FK'),
    f('children','emb[ChildRef]','EMB'), f('permissions','string[]',''),
    f('pushTokens','emb[PushToken]','EMB'),
    f('settings','emb<UserSettings>','EMB'),
    f('lastKnownLocation','emb<Coordinates>','EMB'),
    f('lastLocationUpdate','Instant',''), f('isOnline','boolean',''),
    f('lastActive','Instant',''), f('passwordResetToken','string',''),
    f('passwordResetExpires','Instant',''), f('loginAttempts','int',''),
    f('lockUntil','Instant',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ students ============
nodes['students'] = table('students', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('firstName','string','TI'), f('lastName','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('password','string',''), f('phone','string',''),
    f('birthDate','LocalDate',''), f('gender','string',''),
    f('photo','string',''), f('address','emb<Address>','EMB'),
    f('gradeLevel','string',''), f('section','string',''),
    f('studentCode','string','UK'), f('enrollmentNumber','string','UK'),
    f('guardians','emb[Guardian]','EMB'),
    f('medicalInfo','emb<MedicalInfo>','EMB'),
    f('documents','emb<StudentDocs>','EMB'),
    f('status','string',''), f('isActive','boolean',''),
    f('userId','ref','FK'), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ teachers ============
nodes['teachers'] = table('teachers', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('firstName','string','TI'), f('lastName','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('password','string',''), f('phone','string',''), f('secondaryPhone','string',''),
    f('birthDate','LocalDate',''), f('gender','string',''),
    f('photo','string',''), f('address','emb<Address>','EMB'),
    f('employeeCode','string','UK'), f('specialty','string',''),
    f('academicDegree','string',''), f('professionalTitle','string',''),
    f('certifications','emb[Cert]','EMB'),
    f('contractType','string',''), f('contractStartDate','LocalDate',''),
    f('contractEndDate','LocalDate',''), f('salary','double',''),
    f('bankAccount','emb<BankAccount>','EMB'),
    f('documents','emb<TeacherDocs>','EMB'),
    f('isActive','boolean',''), f('userId','ref','FK'),
    f('institution','ref','FK'), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ parents ============
nodes['parents'] = table('parents', C_GREEN, H_GREEN, BG_GREEN, [
    f('_id','ObjectId','PK'), f('firstName','string','TI'), f('lastName','string','TI'),
    f('dni','string','UK'), f('email','string','UK'),
    f('password','string',''), f('phone','string','IX'), f('secondaryPhone','string',''),
    f('birthDate','LocalDate',''), f('gender','string',''),
    f('photo','string',''), f('address','emb<Address>','EMB'),
    f('occupation','string',''), f('workplace','string',''), f('workPhone','string',''),
    f('children','emb[Guardian]','EMB'),
    f('notifications','emb<NotifPrefs>','EMB'),
    f('documents','emb<ParentDocs>','EMB'),
    f('pushTokens','emb[PushToken]','EMB'),
    f('isActive','boolean',''), f('isVerified','boolean',''),
    f('lastLogin','Instant',''), f('lastActive','Instant',''),
    f('isOnline','boolean',''), f('userId','ref','FK'),
    f('internalNotes','string',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ academicyears ============
nodes['academicyears'] = table('academicyears', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('institution','ref','FK'),
    f('year','int',''), f('name','string',''),
    f('startDate','Instant',''), f('endDate','Instant',''),
    f('periods','emb[AcadPeriod]','EMB'),
    f('importantDates','emb[ImpDate]','EMB'),
    f('isCurrent','boolean','IX'), f('status','string',''),
    f('stats','emb<YearStats>','EMB'),
    f('isActive','boolean',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ gradelevels ============
nodes['gradelevels'] = table('gradelevels', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('institution','ref','FK'),
    f('name','string',''), f('shortName','string',''),
    f('level','int',''), f('type','string',''),
    f('order','int',''), f('description','string',''),
    f('isActive','boolean',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ subjects ============
nodes['subjects'] = table('subjects', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('institution','ref','FK'),
    f('name','string',''), f('code','string','UK'),
    f('description','string',''), f('gradeLevels','ref[]','FK'),
    f('applicableTo','string',''), f('hoursPerWeek','int',''),
    f('isRequired','boolean',''), f('area','string',''),
    f('defaultWeights','emb<EvalWeights>','EMB'),
    f('competencies','emb[Competency]','EMB'),
    f('color','string',''), f('icon','string',''),
    f('order','int',''), f('isActive','boolean',''),
    f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ classrooms ============
nodes['classrooms'] = table('classrooms', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('gradeLevel','ref','FK'),
    f('academicYear','ref','FK'), f('section','string',''),
    f('shift','string',''), f('tutor','ref','FK'),
    f('capacity','int',''), f('location','emb<ClassLoc>','EMB'),
    f('stats','emb<ClassStats>','EMB'),
    f('isActive','boolean',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ courses ============
nodes['courses'] = table('courses', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('name','string',''),
    f('code','string','UK'), f('description','string',''),
    f('gradeLevel','string',''), f('section','string',''),
    f('teacher','ref','FK'), f('students','ref[]','FK'),
    f('schedule','emb[Schedule]','EMB'),
    f('evaluationWeights','emb<EvalWeights>','EMB'),
    f('academicYear','int',''), f('period','string',''),
    f('isActive','boolean',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ coursesections ============
nodes['coursesections'] = table('coursesections', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('subject','ref','FK'),
    f('classroom','ref','FK'), f('teacher','ref','FK'),
    f('academicYear','ref','FK'), f('schedule','emb[Schedule]','EMB'),
    f('evaluationWeights','emb<EvalWeights>','EMB'),
    f('periodEvaluations','emb[PeriodEval]','EMB'),
    f('stats','emb<SectionStats>','EMB'),
    f('resources','emb[Resource]','EMB'),
    f('students','ref[]','FK'), f('isActive','boolean',''),
    f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ enrollments ============
nodes['enrollments'] = table('enrollments', C_ORANGE, H_ORANGE, BG_ORANGE, [
    f('_id','ObjectId','PK'), f('student','ref','FK'),
    f('classroom','ref','FK'), f('academicYear','ref','FK'),
    f('enrollmentDate','Instant',''), f('enrollmentNumber','string','UK'),
    f('status','string',''), f('statusDate','Instant',''),
    f('statusReason','string',''), f('enrollmentType','string',''),
    f('statusHistory','emb[StatusHist]','EMB'),
    f('documents','emb<EnrollDocs>','EMB'),
    f('previousClassroom','ref','FK'), f('previousSchool','string',''),
    f('observations','string',''), f('enrolledBy','ref','FK'),
    f('isActive','boolean',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ grades ============
nodes['grades'] = table('grades', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('student','ref','FK'),
    f('course','ref','FK'), f('bimester','int',''),
    f('academicYear','int',''), f('scores','emb[ScoreEntry]','EMB'),
    f('average','double',''), f('status','string',''),
    f('closedAt','Instant',''), f('closedBy','string',''),
    f('publishedAt','Instant',''), f('teacher','ref','FK'),
    f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ attendances ============
nodes['attendances'] = table('attendances', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('student','ref','FK'),
    f('course','ref','FK'), f('teacher','ref','FK'),
    f('date','Instant','IX'), f('status','string',''),
    f('arrivalTime','string',''), f('observations','string',''),
    f('justification','ref','FK'),
    f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ evaluations ============
nodes['evaluations'] = table('evaluations', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('course','ref','FK'),
    f('teacher','ref','FK'), f('name','string',''),
    f('type','string',''), f('bimester','int',''),
    f('maxGrade','double',''), f('weight','double',''),
    f('date','Instant',''), f('description','string',''),
    f('academicYear','int',''), f('isActive','boolean',''),
    f('order','int',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ events ============
nodes['events'] = table('events', C_PINK, H_PINK, BG_PINK, [
    f('_id','ObjectId','PK'), f('title','string',''),
    f('date','string','IX'), f('time','string',''),
    f('type','string','IX'), f('description','string',''),
    f('location','string',''), f('participants','string',''),
    f('notifyStudents','boolean',''), f('notifyParents','boolean',''),
    f('notifyTeachers','boolean',''), f('createdBy','ref','FK'),
    f('isActive','boolean','IX'), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ justifications ============
nodes['justifications'] = table('justifications', C_TEAL, H_TEAL, BG_TEAL, [
    f('_id','ObjectId','PK'), f('student','ref','FK'),
    f('parent','ref','FK'), f('dates','Instant[]',''),
    f('reason','string',''), f('observations','string',''),
    f('documents','emb[DocFile]','EMB'),
    f('coursesAffected','ref[]','FK'), f('status','string','IX'),
    f('reviewedBy','ref','FK'), f('reviewedAt','Instant',''),
    f('reviewNote','string',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# ============ notifications ============
nodes['notifications'] = table('notifications', C_PURPLE, H_PURPLE, BG_PURPLE, [
    f('_id','ObjectId','PK'), f('recipient','ref','FK'),
    f('title','string',''), f('message','string',''),
    f('type','string',''), f('data','emb<NotifData>','EMB'),
    f('isRead','boolean',''), f('readAt','Instant',''),
    f('expiresAt','Instant',''), f('createdAt','Instant',''), f('updatedAt','Instant',''),
])

# Build DOT
dot = 'digraph ModeloFisico {\n'
dot += 'rankdir=LR  dpi=250  fontname="Segoe UI"  fontsize=7\n'
dot += 'node [fontname="Segoe UI", fontsize=7, shape=plaintext, margin=0]\n'
dot += 'edge [fontname="Segoe UI", fontsize=7, arrowsize=0.6, penwidth=0.9, color="#90a4ae"]\n'
dot += 'graph [bgcolor="#f8f9fa", pad=1.0, nodesep=0.3, ranksep=0.7]\n'
dot += 'splines=ortho\n\n'

for name, label in nodes.items():
    dot += f'{name} [label=<{label}>]\n'

# Relaciones
dot += '\n// === REFERENCIAS ===\n'
refs = [
    ('users','students','userId'),
    ('users','teachers','userId'),
    ('users','parents','userId'),
    ('academicyears','classrooms','academicYear'),
    ('academicyears','coursesections','academicYear'),
    ('academicyears','enrollments','academicYear'),
    ('gradelevels','classrooms','gradeLevel'),
    ('gradelevels','subjects','gradeLevels'),
    ('classrooms','coursesections','classroom'),
    ('classrooms','enrollments','classroom'),
    ('subjects','coursesections','subject'),
    ('students','enrollments','student'),
    ('students','grades','student'),
    ('students','attendances','student'),
    ('students','justifications','student'),
    ('teachers','courses','teacher'),
    ('teachers','grades','teacher'),
    ('teachers','attendances','teacher'),
    ('teachers','evaluations','teacher'),
    ('teachers','coursesections','teacher'),
    ('courses','grades','course'),
    ('courses','attendances','course'),
    ('courses','evaluations','course'),
    ('parents','justifications','parent'),
    ('users','events','createdBy'),
    ('users','notifications','recipient'),
]
for a, b, fld in refs:
    dot += f'{a} -> {b} [xlabel="{fld}"]\n'

dot += '\nlabelloc="t"\n'
dot += 'label="MODELO FISICO DE DATOS - IEP Continental Americano - Backend Java\\nMongoDB Document Model | 17 Colecciones | Spring Boot 3.2.5 + Java 17\\nAtributos completos con tipos de datos, PK, FK, UK, IX, TI, EMB"\n'
dot += 'fontsize=18  fontname="Segoe UI Bold"  fontcolor="#1a237e"\n}\n'

with open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico.dot', 'w', encoding='utf-8') as fout:
    fout.write(dot)

subprocess.run([dot_path, '-Kdot', '-Tpng',
    r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico.dot',
    '-o', r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_fisico_sanmartin.png',
    '-Gdpi=250'], check=True)
print('[OK] Modelo Fisico con atributos completos generado')
