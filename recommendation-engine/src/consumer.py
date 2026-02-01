import json
import logging
import aio_pika
from src.config import settings
import redis.asyncio as redis

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler()  # Output to console
    ]
)

logger = logging.getLogger("reco-consumer")
# Separate Redis connection for the background worker
r = redis.from_url(settings.REDIS_URL, decode_responses=True)

async def process_message(message: aio_pika.IncomingMessage):
    async with message.process():
        try:
            # Payload from Java: {"accountId": "u1", "selections": [{"selectionName": "Lakers"}]}
            payload = json.loads(message.body)
            user_id = payload.get("accountId")
            selections = payload.get("selections", [])

            tags = []
            for sel in selections:
                if "selectionName" in sel: tags.append(sel["selectionName"])
                if "marketName" in sel: tags.append(sel["marketName"])

            if not tags: return

            # Atomic Update: User Profile + Global Trends
            async with r.pipeline() as pipe:
                # 1. Update User History
                user_key = f"user:{user_id}:tags"
                for tag in tags:
                    pipe.hincrby(user_key, tag, 1)

                # 2. Update Global Trends (Sorted Set)
                trend_key = "global:trends"
                for tag in tags:
                    pipe.zincrby(trend_key, 1, tag)
                
                await pipe.execute()
                
            logger.info(f"Updated User {user_id} preferences: {tags}")

        except Exception as e:
            logger.error(f"Error processing message: {e}")

async def start_consumer():
    connection = await aio_pika.connect_robust(settings.RABBITMQ_URL)
    channel = await connection.channel()
    
    # Declare Exchange (Passive=False ensures it exists if Java hasn't made it yet)
    exchange = await channel.declare_exchange(
        settings.EXCHANGE_NAME, 
        aio_pika.ExchangeType.TOPIC, 
        durable=True  # <--- This matches your Java config
    )
    queue = await channel.declare_queue(settings.QUEUE_NAME, auto_delete=True)
    
    await queue.bind(exchange, routing_key=settings.ROUTING_KEY)
    
    logger.info("🐰 Consumer Listening...")
    await queue.consume(process_message)
    return connection