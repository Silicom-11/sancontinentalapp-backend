import re

html = open(r'D:\projects\SanMartinDigital\sanmartin-newbackend\docs\modelo_datos_sanmartin.html','r',encoding='utf-8').read()
blocks = re.findall(r'class="mermaid">(.*?)</div>', html, re.DOTALL)
found = False
for b in blocks:
    for l in b.split('\n'):
        if '{' in l and any(x in l for x in ['UK_SPARSE','FK_IX']):
            print('INVALID:', l.strip())
            found = True
if not found:
    print('All mermaid blocks look clean (no UK_SPARSE or FK_IX tags)')
