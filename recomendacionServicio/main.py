# MICROSERVICIO DE RECOMENDACIONES PARA EVENTOS (FASTAPI)
#
# Usa dos modelos de Machine Learning:
#   1) SVD (Surprise) – Filtrado colaborativo basado en historial (tickets)
#   2) TF-IDF + Cosine Similarity – Recomendación por similitud de contenido
#
# También soporta auto-reentrenamiento automático cuando cambia el número
# de tickets en la base de datos (backend Spring).
#
# Se conecta al backend Spring Boot, que envía:
#   - usuarios (con lista de tickets)
#   - eventos
#
# El microservicio reconstruye una tabla PLANA de tickets para entrenar SVD.

from fastapi import FastAPI
import requests
import pandas as pd
from surprise import SVD, Dataset, Reader
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# URL del backend Spring Boot
SPRING_URL = "http://backend:8080/tfg/utilidades/data"

# Inicializa FastAPI
app = FastAPI(title="Microservicio de Recomendaciones")

# VARIABLES GLOBALES (MODELOS Y DATOS)
model_svd = None           # Modelo colaborativo SVD
matriz_similitud = None    # Matriz de similitud evento-evento
vectorizer = None          # Vectorizador TF-IDF global (antes faltaba y daba error)
usuarios = None            # DataFrame usuarios
eventos = None             # DataFrame eventos
tickets = None             # DataFrame plano de tickets reconstruido

# Para detectar si hay nuevos tickets y reentrenar
ultimo_num_tickets = 0

#  1) FUNCIÓN PARA OBTENER DATOS DESDE SPRING

def obtener_datos():
    """
    Llama al endpoint del backend Spring Boot y obtiene:
      - usuarios (cada uno con su lista de tickets)
      - eventos completos

    Reconstruye un DataFrame plano de tickets:
        usuario_id | evento_id
    """

    response = requests.get(SPRING_URL)
    data = response.json()

    usuarios_df = pd.DataFrame(data["usuarios"])
    eventos_df = pd.DataFrame(data["eventos"])

    tickets_list = []

    # Los tickets vienen dentro de cada usuario
    for u in data["usuarios"]:

        if "tickets" in u and u["tickets"]:  # si el usuario tiene tickets

            for t in u["tickets"]:
                tickets_list.append({
                    "usuario_id": t["usuarioId"],
                    "evento_id": t["eventoId"]
                })

    # Convertimos la lista a DataFrame obligatorio para SVD
    tickets_df = pd.DataFrame(tickets_list, columns=["usuario_id", "evento_id"])

    return usuarios_df, eventos_df, tickets_df

#  2) ENTRENAMIENTO DE LOS MODELOS (SVD y TF-IDF)

def entrenar_modelos():
    """
    Reentrena los dos modelos:
      - Filtrado colaborativo (SVD)
      - Similitud por contenido (TF-IDF + Coseno)
    """

    global model_svd, matriz_similitud, vectorizer, usuarios, eventos, tickets

    print("\n[IA] Cargando datos y entrenando modelos...")

    usuarios, eventos, tickets = obtener_datos()

    #  1) MODELO SVD
    if not tickets.empty:

        df = tickets.copy()
        df["rating"] = 1  # todas las compras son rating=1

        reader = Reader(rating_scale=(0, 1))
        dataset = Dataset.load_from_df(df[["usuario_id", "evento_id", "rating"]], reader)
        trainset = dataset.build_full_trainset()

        model_svd = SVD()
        model_svd.fit(trainset)

    else:
        model_svd = None  # No hay historial → no puede entrenar

    #  2) MODELO DE CONTENIDO (TF-IDF)

    eventos["texto"] = (
        eventos["nombre"].astype(str) + " " +  
        eventos["categoria"].astype(str) + " " +
        eventos["localizacion"].astype(str) + " " +
        eventos["descripcion"].astype(str)
    )

    # Guardamos TF-IDF global (corrección importante)
    vectorizer = TfidfVectorizer(stop_words=None)
    matriz = vectorizer.fit_transform(eventos["texto"])

    matriz_similitud = cosine_similarity(matriz)

    print("[IA] Modelos entrenados correctamente.")



#  3) AUTOENTRENAMIENTO SI CAMBIAN LOS TICKETS

def recargar_si_hay_cambios():
    """
    Comprueba si el número de tickets ha cambiado.
    Si cambió → reentrena los modelos.
    """

    global ultimo_num_tickets, usuarios, eventos, tickets, vectorizer

    usuarios_df, eventos_df, tickets_df = obtener_datos()

    # Detectamos cambios en el número de tickets
    if len(tickets_df) != ultimo_num_tickets:
        print(f"\n[IA] Cambio detectado en tickets ({ultimo_num_tickets} → {len(tickets_df)}). Reentrenando...")
        entrenar_modelos()
        ultimo_num_tickets = len(tickets_df)
    else:
        # Si no cambian, solo refrescamos datos
        usuarios = usuarios_df
        eventos = eventos_df
        tickets = tickets_df



# 4) FUNCIONES DE RECOMENDACIÓN

def recomendar_por_compras(usuario_id):
    """ Recomendación colaborativa (SVD). """

    if model_svd is None:
        return []

    ids_eventos = eventos["id"].tolist()

    predicciones = [
        (eid, model_svd.predict(usuario_id, eid).est)
        for eid in ids_eventos
    ]

    predicciones.sort(key=lambda x: x[1], reverse=True)

    return [eid for eid, _ in predicciones[:5]]


def recomendar_por_evento(evento_id):
    """ Similitud por contenido TF-IDF. """

    if evento_id not in eventos["id"].values:
        return []

    idx = eventos.index[eventos["id"] == evento_id][0]

    scores = list(enumerate(matriz_similitud[idx]))

    scores.sort(key=lambda x: x[1], reverse=True)

    return [int(eventos.iloc[i]["id"]) for i, _ in scores[1:6]]



def recomendar_hibrido(usuario_id):
    """
    Combina:
     - SVD (compras del usuario)
     - TF-IDF basado en eventos que ha comprado
    """

    if tickets is None or tickets.empty:
        return eventos.sample(min(5, len(eventos)))["id"].tolist()

    recs_colab = recomendar_por_compras(usuario_id)
    historial = tickets[tickets["usuario_id"] == usuario_id]
    eventos_usuario = historial["evento_id"].tolist()

    similares_totales = []

    for ev in eventos_usuario:
        similares_totales.extend(recomendar_por_evento(ev))

    from collections import Counter
    conteo = Counter(similares_totales)

    recs_contenido = [ev for ev, _ in conteo.most_common(5)]

    # fusiona ambas listas sin duplicados
    fusion = list(dict.fromkeys(recs_colab + recs_contenido))

    return fusion[:5]



#  5) ENDPOINTS PÚBLICOS

@app.get("/recommendations")
def get_recommendations(userId: int):
    recargar_si_hay_cambios()
    return {"eventos_recomendados": recomendar_hibrido(userId)}


@app.get("/recommendations/event")
def get_similares(eventoId: int):
    recargar_si_hay_cambios()
    return {"eventos_similares": recomendar_por_evento(eventoId)}


@app.get("/reload")
def reload_data():
    entrenar_modelos()
    return {"status": "retrained manually"}


@app.get("/prueba")
def prueba():
    return {"test": "hola desde el servicio de recomendación"}



# 6) BÚSQUEDA INTELIGENTE – TF-IDF + COSENO


@app.get("/search")
def search_events(nombre: str):
    """
    Búsqueda combinada:
    1) Buscar por nombre exacto/contiene
    2) Si no hay resultados → usar IA (TF-IDF) solo si hay similitud REAL (> umbral)
    3) Si no hay similitud → devolver vacío
    """

    global vectorizer, eventos

    UMBRAL_SIMILITUD = 0.05  # <--- SIMILITUD MÍNIMA PARA ACEPTAR RESULTADOS

    try:
        recargar_si_hay_cambios()

        #  1) Crear 'texto' si no existe 
        if "texto" not in eventos.columns:
            eventos["texto"] = (
                eventos["nombre"].astype(str) + " " +
                eventos["categoria"].astype(str) + " " +
                eventos["localizacion"].astype(str) + " " +
                eventos["descripcion"].astype(str)
            )

        #  2) PRIMERO BUSCAR POR NOMBRE 
        encontrados_nombre = eventos[eventos["nombre"]
                                    .str.contains(nombre, case=False, na=False)]

        ids_nombre = encontrados_nombre["id"].tolist()

        if ids_nombre:  # si hay coincidencias por nombre → devolverlas primero
            return {"eventos_encontrados": ids_nombre[:5]}

        #  3) SI NO HAY NOMBRE → USAR IA 
        query_vec = vectorizer.transform([nombre]) # vector de consulta
        matriz_eventos = vectorizer.transform(eventos["texto"]) # matriz de eventos
        scores = cosine_similarity(query_vec, matriz_eventos)[0] # scores de similitud(todos)

        # Encontrar eventos con similitud REAL (> umbral)
        indices_validos = [i for i, score in enumerate(scores) if score >= UMBRAL_SIMILITUD] #filtrado de los vectores mas parecidos

        # Si NO HAY ninguno con similitud → vacío
        if not indices_validos:
            return {"eventos_encontrados": []}

        # Ordenamos los válidos por score
        indices_validos.sort(key=lambda i: scores[i], reverse=True)

        ids_ia = [int(eventos.iloc[i]["id"]) for i in indices_validos[:5]]

        return {"eventos_encontrados": ids_ia}

    except Exception as e:
        print("ERROR EN /search:", e)
        return {"eventos_encontrados": []}

# 7) ENTRENAMIENTO AL INICIAR MICROSERVICIO
entrenar_modelos()
ultimo_num_tickets = len(tickets)