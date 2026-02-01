FROM python:3.11-slim

WORKDIR /app
ENV PYTHONUNBUFFERED=1

# Install build tools for numpy/scikit
RUN apt-get update && apt-get install -y build-essential

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src/

CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "8084"]
