from urllib.request import urlopen
from bs4 import BeautifulSoup
import ssl
import json
import time

root = "https://ead.unisanta.br"

ssl_ctx = ssl.create_default_context()
ssl_ctx.check_hostname = False
ssl_ctx.verify_mode = ssl.CERT_NONE

#delay para nao sobrecarregar a unisanta - esquece mto lerdo
def get_html(url):
    # time.sleep(1)
    print("Abrindo " + root+url + "...")
    return str(BeautifulSoup(urlopen(root+url, context=ssl_ctx).read().decode("utf-8"), "html.parser"))

def get_value(label, html):
    idx = html.find(label)
    if idx != -1:
        start = html.find('value\">', idx)+len('value\">')
        end = html.find("</span>", idx)
        if end - start <= 100 and end - start > 1:
            return html[start:end]
    
    return "null"

#pega info da pagina de um curso especifico
def grab_course(url, type):
    html = get_html(url)

    dic = {}

    dic["name"] = html[(html.find("<title>")+len("<title>")):html.find(" - UNISANTA EAD")]
    dic["qualification"] = get_value(">Formação", html)
    dic["style"] = get_value(">Modalidade", html)
    dic["duration_months"] = get_value(">Duração", html)
    dic["hours"] = get_value(">Carga Horária", html)
    dic["url"] = root+url
    dic["is_available"] = True
    dic["degree"] = type

    # if dic["qualification"] != "A distância":
    #     dic["is_available"] = False
    
    return dic

#pega info da pagina de cursos
def grab_dashboard(url, type):
    html = get_html(url)

    courses = []

    start = html.find('<div class=\"cursos-cards__btnbox\">')
    while start != -1 and start:
        print("\nCurso encontrado.")
        status = html[(html.find('button\">', start)+len('button\">')):html.find('</a>', start)]
        if status == "INSCRIÇÕES ABERTAS":
            course_url = html[(html.find('href=\"', start)+len('href=\"')):html.find('\" role', start)]
            print("Curso " + course_url + " tem inscricoes abertas.")
            courses.append(grab_course(course_url, type))
        else:
            print("Curso nao tem inscricoes abertas.")
        start = html.find('<div class=\"cursos-cards__btnbox\">', start+1)
    return courses

def main():

    cursos = grab_dashboard("/cursos/graduacao", "Graduação")
    tmp = grab_dashboard("/cursos/posgraduacao", "Pós-Graduação")
    for curso in tmp:
        cursos.append(curso)


    with open("courses.json", "w", encoding="utf8") as outfile:
        json.dump(cursos, outfile, ensure_ascii=False, indent=4)

if __name__ == "__main__":
    main()

# informaçoes pertinentes
# nome, duraçao, estado inscriçoes, tipo do curso, preço, carga horaria