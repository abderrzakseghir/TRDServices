#!/bin/bash

echo "========================================"
echo " Interface React - Mock Football API"
echo "========================================"
echo ""

echo "[1/4] Vérification de Node.js..."
if ! command -v node &> /dev/null; then
    echo "ERREUR: Node.js n'est pas installé"
    echo "Téléchargez-le depuis https://nodejs.org/"
    exit 1
fi
node --version
echo ""

echo "[2/4] Vérification de l'API..."
if ! curl -s http://localhost:5000/v4/matches > /dev/null 2>&1; then
    echo "ATTENTION: L'API ne répond pas sur http://localhost:5000"
    echo "Assurez-vous que docker-compose up -d est lancé"
    echo ""
    read -p "Continuer quand même? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi
echo "API accessible"
echo ""

echo "[3/4] Installation des dépendances..."
if [ ! -d "node_modules" ]; then
    echo "Installation en cours..."
    npm install
else
    echo "Dépendances déjà installées"
fi
echo ""

echo "[4/4] Démarrage du serveur de développement..."
echo ""
echo "L'interface sera accessible sur: http://localhost:3000"
echo "Appuyez sur Ctrl+C pour arrêter"
echo ""
npm run dev
