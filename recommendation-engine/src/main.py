from contextlib import asynccontextmanager
from fastapi import FastAPI
from src.consumer import start_consumer
from src.engine import HybridEngine


engine = HybridEngine()

@asynccontextmanager
async def lifespan(app: FastAPI):

    mq_connection = await start_consumer()
    print("System Online.")
    yield
    await mq_connection.close()
    await engine.close()
    print("System Offline.")

app = FastAPI(title="Recommendation Engine", lifespan=lifespan)

@app.get("/recommendations/{user_id}")
async def get_recommendations(user_id: str):

    results = await engine.recommend(user_id)
    return {
        "user_id": user_id,
        "recommendations": results
    }