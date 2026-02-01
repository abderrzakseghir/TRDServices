from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    REDIS_URL: str
    RABBITMQ_URL: str
    
    EXCHANGE_NAME: str = "betting.events.exchange"
    QUEUE_NAME: str = "recommendation_ingestion_queue"
    ROUTING_KEY: str = "betting.lifecycle.placed"

    WEIGHT_CONTENT: float = 0.7  # 70% Personal
    WEIGHT_TREND: float = 0.3    # 30% Popularity

    class Config:
        env_file = ".env"

settings = Settings()