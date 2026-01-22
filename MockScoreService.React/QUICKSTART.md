# ?? Guide de Démarrage Rapide - Interface React

## Installation et Test Local

### 1. Vérifier que l'API tourne
```bash
# Depuis la racine du projet
docker-compose ps

# Vérifier l'API
curl http://localhost:5000/v4/matches
```

### 2. Installer les dépendances
```bash
cd MockScoreService.React
npm install
```

### 3. Lancer en mode développement
```bash
npm run dev
```

L'application démarre sur **http://localhost:3000**

### 4. Tester l'interface
- Dashboard : http://localhost:3000
- Simulation : http://localhost:3000/simulation
- Matchs : http://localhost:3000/matches
- Équipes : http://localhost:3000/teams

---

## Commandes Disponibles

```bash
# Développement avec hot reload
npm run dev

# Build production
npm run build

# Preview du build
npm run preview

# Vérifier le build
npm run build && npm run preview
```

---

## Dépannage

### Erreur "Cannot find module"
```bash
rm -rf node_modules package-lock.json
npm install
```

### Port 3000 déjà utilisé
Modifier dans `vite.config.ts` :
```typescript
server: {
  port: 3001
}
```

### Erreur CORS
Vérifier que l'API a CORS activé dans `MockScoreService.API/Program.cs`

### Build échoue
```bash
# Nettoyer et rebuild
rm -rf dist node_modules
npm install
npm run build
```

---

## Structure des Fichiers

```
MockScoreService.React/
??? src/
?   ??? components/       # Composants réutilisables
?   ??? pages/           # Pages de l'application
?   ??? services/        # API client
?   ??? types/           # Types TypeScript
?   ??? index.css        # Styles globaux
?   ??? main.tsx         # Point d'entrée
?   ??? App.tsx          # Routes
??? public/              # Assets statiques
??? .env                 # Variables d'environnement (local)
??? .env.production      # Variables d'environnement (prod)
??? Dockerfile           # Docker build
??? nginx.conf           # Configuration Nginx
??? package.json         # Dépendances
```

---

## Vérification de la Configuration

### 1. Variables d'environnement
```bash
# Vérifier .env
cat .env

# Devrait contenir :
# VITE_API_URL=http://localhost:5000
# VITE_WEB_API_URL=http://localhost:5001
```

### 2. API accessible
```bash
# Test endpoint
curl http://localhost:5000/v4/matches
```

### 3. Dépendances installées
```bash
npm list --depth=0
```

---

## Docker Build et Test

### Build l'image
```bash
# Depuis la racine du projet
docker-compose build mock-football-react
```

### Lancer le conteneur
```bash
docker-compose up -d mock-football-react
```

### Voir les logs
```bash
docker-compose logs -f mock-football-react
```

### Tester dans le navigateur
```
http://localhost:3000
```

---

## Checklist Avant Démarrage

- [ ] Node.js 18+ installé
- [ ] npm ou yarn installé
- [ ] API Backend tourne sur port 5000
- [ ] Port 3000 disponible
- [ ] Dépendances installées (`npm install`)

---

## Premier Test

```bash
# 1. Aller dans le dossier
cd MockScoreService.React

# 2. Installer
npm install

# 3. Lancer
npm run dev

# 4. Ouvrir
start http://localhost:3000
```

**? Si vous voyez le Dashboard, tout fonctionne !**

---

## Prochaines Étapes

1. Tester la page Simulation
2. Créer un match via l'API ou Blazor
3. Voir le match apparaître dans React
4. Tester les contrôles de scores
5. Vérifier l'auto-refresh

---

**Besoin d'aide ? Consultez README.md ou REACT-INTERFACE-READY.md**
