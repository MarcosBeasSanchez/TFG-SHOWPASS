# MICROSERVICIO DE RECOMENDACIONES PARA EVENTOS (FASTAPI)
# 
# Usa dos modelos de Machine Learning:
#   1) SVD (Surprise) – Filtrado colaborativo basado en historial
#   2) TF-IDF + Cosine Similarity – Recomendación por contenido
# 
# También tiene auto-reentrenamiento cada ciertos minutos.
# Compatible con el backend Spring, donde los tickets están
# dentro de cada usuario en el JSON.

from fastapi import FastAPI
import requests
import pandas as pd
from surprise import SVD, Dataset, Reader
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import time

# URL del backend Spring Boot que envía usuarios, eventos y tickets
SPRING_URL = "http://localhost:8080/tfg/utilidades/data"

app = FastAPI(title="Microservicio de Recomendaciones")

# Variables globales de los modelos y datos cargados
model_svd = None           # IA colaborativa
matriz_similitud = None    # IA por contenido
usuarios = None
eventos = None
tickets = None

ultimo_reload = 0          # Última vez que se reentrenó
RELOAD_INTERVAL = 300      # Reentrenar cada 5 minutos


#  CARGA DE DATOS DESDE EL BACKEND SPRING BOOT

def obtener_datos():
    """
    Llama al endpoint /data del backend Spring Boot.
    El backend devuelve:
    {
      "usuarios": [...],
      "eventos": [...]
    }

    NOTA: los tickets vienen DENTRO de cada usuario,
    así que necesitamos reconstruir el DataFrame de tickets.
    """

    response = requests.get(SPRING_URL)
    data = response.json()

    # Convertir usuarios y eventos directamente a DataFrame
    usuarios_df = pd.DataFrame(data["usuarios"])
    eventos_df = pd.DataFrame(data["eventos"])

    #  Reconstruir la tabla tickets

    tickets_list = []

    for u in data["usuarios"]:
        if "tickets" in u and u["tickets"]:
            for t in u["tickets"]:
                tickets_list.append({
                    "usuario_id": t["usuarioId"],
                    "evento_id": t["eventoId"]
                })

    tickets_df = pd.DataFrame(tickets_list, columns=["usuario_id", "evento_id"])

    return usuarios_df, eventos_df, tickets_df


#  ENTRENAR LOS MODELOS DE RECOMENDACIÓN

def entrenar_modelos():
    """
    Carga datos y entrena:
    
     Modelo SVD → recomendaciones basadas en compras pasadas.
     Modelo TF-IDF → recomendaciones según texto y atributos.
    """
    global model_svd, matriz_similitud, usuarios, eventos, tickets, ultimo_reload

    print(" Cargando datos y entrenando modelos...")

    usuarios, eventos, tickets = obtener_datos()

    #  1) MODELO COLABORATIVO: Surprise SVD
    if not tickets.empty:
        df = tickets.copy()
        df["rating"] = 1    # Todas las compras valen "1" (implícito)

        reader = Reader(rating_scale=(0, 1))
        dataset = Dataset.load_from_df(df[["usuario_id", "evento_id", "rating"]], reader)
        trainset = dataset.build_full_trainset()

        model_svd = SVD()
        model_svd.fit(trainset)
    else:
        model_svd = None     # No hay datos → no se entrena

    # 2) MODELO DE CONTENIDO: TF-IDF + Cosine Similarity

    eventos["texto"] = (
        eventos["categoria"].astype(str) + " " +
        eventos["localizacion"].astype(str) + " " +
        eventos["descripcion"].astype(str)
    )

    vectorizer = TfidfVectorizer(stop_words=None)
    matriz = vectorizer.fit_transform(eventos["texto"])
    matriz_similitud = cosine_similarity(matriz)

    ultimo_reload = time.time()
    print(" Modelos entrenados correctamente.")



#  AUTO-REENTRENAMIENTO CADA X MINUTOS

def recargar_si_necesario():
    """
    Si han pasado más de RELOAD_INTERVAL segundos,
    se vuelve a entrenar automáticamente.
    """
    global ultimo_reload
    if time.time() - ultimo_reload > RELOAD_INTERVAL:
        entrenar_modelos()



#  FUNCIONES DE RECOMENDACIÓN

def recomendar_por_compras(usuario_id):
    """
    Devuelve los 5 eventos más recomendados mediante SVD.
    """
    if model_svd is None:
        return []
    ids_eventos = eventos["id"].tolist()
    predicciones = [(eid, model_svd.predict(usuario_id, eid).est) for eid in ids_eventos]
    predicciones.sort(key=lambda x: x[1], reverse=True)
    return [eid for eid, _ in predicciones[:5]]


def recomendar_por_evento(evento_id):
    """
    Devuelve los eventos más similares según contenido.
    """
    if evento_id not in eventos["id"].values:
        return []
    idx = eventos.index[eventos["id"] == evento_id][0]
    scores = list(enumerate(matriz_similitud[idx]))
    scores.sort(key=lambda x: x[1], reverse=True)
    return [int(eventos.iloc[i]["id"]) for i, _ in scores[1:6]]


def recomendar_hibrido(usuario_id):
    """
    Combina:
     recomendaciones por historial (SVD)
     recomendaciones por contenido (último evento comprado)
    """
    if tickets is None or tickets.empty:
        # No hay historial → recomendar eventos populares/aleatorios
        return eventos.sample(min(5, len(eventos)))["id"].tolist()


    recs_colab = recomendar_por_compras(usuario_id)

    # Obtener historial real del usuario
    historial = tickets[tickets["usuario_id"] == usuario_id]

    
    # Último evento comprado
    ultimo_evento = historial["evento_id"].iloc[-1]

    # Buscar eventos similares
    recs_contenido = recomendar_por_evento(ultimo_evento)

    # Fusionar sin repetir
    fusion = list(dict.fromkeys(recs_colab + recs_contenido))
    return fusion[:5]



#  ENDPOINTS PÚBLICOS

@app.get("/recommendations")
def get_recommendations(userId: int):
    """
    Recomendaciones híbridas para un usuario.
    Devuelve solo IDs para que el backend Spring haga findById().
    """
    recargar_si_necesario()
    ids = recomendar_hibrido(userId)
    return {"eventos_recomendados": ids}


@app.get("/recommendations/event")
def get_similares(eventoId: int):
    """
    Recomendaciones de eventos similares según contenido.
    """
    recargar_si_necesario()
    ids = recomendar_por_evento(eventoId)
    return {"eventos_similares": ids}


@app.get("/reload")
def reload_data():
    """
    Reentrenamiento manual.
    """
    entrenar_modelos()
    return {"status": "retrained manually"}


# Entrena automáticamente al iniciar
entrenar_modelos()