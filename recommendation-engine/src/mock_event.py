import asyncio
import aio_pika
import json

async def send_mock_bet():
    connection = await aio_pika.connect_robust("amqp://guest:guest@localhost:5672/")
    channel = await connection.channel()

    
    event_payload = {
        "accountId": "user_test_01",
        "ticketId": "ticket_555",
        "selections": [
            {
                "selectionName": "Real Madrid",  
                "marketName": "Winner",
                "odds": 1.90
            },
            {
                "selectionName": "Barcelona",    
                "marketName": "Winner",
                "odds": 1.90
            }
        ]
    }


    exchange = await channel.get_exchange("betting.events.exchange")
    
    await exchange.publish(
        aio_pika.Message(body=json.dumps(event_payload).encode()),
        routing_key="betting.lifecycle.placed"
    )

    print(f"✅ Event Published: User {event_payload['accountId']} bet on Real Madrid/Barcelona")
    await connection.close()

if __name__ == "__main__":
    asyncio.run(send_mock_bet())