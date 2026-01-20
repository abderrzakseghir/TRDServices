# ?? MockScoreService - Interface React Moderne

Interface web React avec Material-UI pour le simulateur d'API Football.

## ? Caractéristiques

- **React 18** avec TypeScript
- **Material-UI (MUI)** - Design system moderne
- **Vite** - Build ultra-rapide
- **React Router** - Navigation fluide
- **Axios** - Appels API
- **Dark Theme** par défaut
- **Responsive Design** - Mobile-first

---

## ?? URLs

- **Interface React** : http://localhost:3000
- **API Backend** : http://localhost:5000

---

## ?? Pages Disponibles

### 1. Dashboard (/)
- Statistiques en temps réel
- Cartes avec compteurs
- Actions rapides

### 2. Simulation (/simulation)
- Vue en temps réel des matchs
- Contrôles de scores avec boutons +/-
- Boutons d'action (Démarrer, Terminer, Reset, Rejouer)
- Auto-refresh toutes les 2 secondes
- Design avec cards Material-UI

### 3. Matchs (/matches)
- En construction
- Création de matchs
- Liste des matchs

### 4. Équipes (/teams)
- En construction
- Gestion des équipes

---

## ??? Développement Local

### Prérequis
- Node.js 18+
- npm ou yarn

### Installation
```bash
cd MockScoreService.React
npm install
```

### Démarrage
```bash
npm run dev
```

L'application démarre sur http://localhost:3000

### Build Production
```bash
npm run build
```

---

## ?? Docker

### Build
```bash
docker-compose build mock-football-react
```

### Lancer
```bash
docker-compose up -d mock-football-react
```

---

## ?? Stack Technique

### Core
- **React 18.2** - UI Library
- **TypeScript 5.3** - Type Safety
- **Vite 5** - Build Tool

### UI
- **@mui/material 5.15** - Component Library
- **@mui/icons-material** - Icons
- **@emotion** - CSS-in-JS

### Router & HTTP
- **react-router-dom 6** - Routing
- **axios** - HTTP Client

### Date/Time
- **dayjs** - Date manipulation
- **@mui/x-date-pickers** - Date pickers

---

## ?? Structure

```
MockScoreService.React/
??? src/
?   ??? components/
?   ?   ??? Layout.tsx          # Layout principal avec drawer
?   ??? pages/
?   ?   ??? Dashboard.tsx       # Page d'accueil
?   ?   ??? Simulation.tsx      # Simulation temps réel ?
?   ?   ??? Matches.tsx         # Gestion matchs
?   ?   ??? Teams.tsx           # Gestion équipes
?   ??? services/
?   ?   ??? api.ts              # API client
?   ??? types/
?   ?   ??? football.ts         # TypeScript types
?   ??? App.tsx                 # Routes
?   ??? main.tsx                # Entry point
??? public/
??? Dockerfile
??? nginx.conf
??? package.json
??? vite.config.ts
```

---

## ?? Fonctionnalités Clés

### Simulation Temps Réel
- ? Affichage des matchs en cards
- ? Badges de statut colorés (Programmé, En cours, Terminé)
- ? Contrôles +/- pour modifier les scores
- ? Boutons d'action contextuels selon le statut
- ? Auto-refresh automatique (2s)
- ? Tri intelligent (En cours ? Programmé ? Terminé)

### Dashboard
- ? 4 cartes statistiques avec icônes
- ? Compteurs animés
- ? Actions rapides
- ? Design moderne avec couleurs

### Layout
- ? Drawer permanent sur desktop
- ? Drawer temporaire sur mobile
- ? Navigation avec icônes
- ? Item actif surligné
- ? AppBar responsive

---

## ?? Thème

### Dark Theme
Le thème sombre est activé par défaut pour un confort visuel optimal.

### Couleurs
- **Primary** : Blue (#1976d2)
- **Secondary** : Pink (#dc004e)
- **Success** : Green (#4caf50)
- **Info** : Light Blue
- **Warning** : Orange
- **Error** : Red

---

## ?? API Integration

### Endpoints Utilisés
```typescript
GET /v4/matches              // Liste tous les matchs
GET /v4/matches?status=...   // Filtrer par statut
GET /v4/matches/{id}         // Détail d'un match
```

### Types TypeScript
Tous les types sont définis dans `src/types/football.ts` :
- `Team`
- `Match`
- `Score`
- `Competition`
- `Season`
- `MatchesResponse`

---

## ?? À Développer

### Pages Manquantes
- [ ] Formulaire création de match
- [ ] Formulaire création d'équipe
- [ ] Liste complète des matchs avec filtres
- [ ] Détail d'un match
- [ ] Edition des équipes

### Fonctionnalités
- [ ] WebSocket pour le temps réel (au lieu du polling)
- [ ] Notifications toast
- [ ] Confirmation dialogs
- [ ] Loading skeletons
- [ ] Error boundaries
- [ ] Tests unitaires

---

## ?? Composants Material-UI Utilisés

- `AppBar` - Barre d'application
- `Drawer` - Menu latéral
- `Card` - Cartes de contenu
- `Grid` - Système de grille responsive
- `Button` - Boutons
- `IconButton` - Boutons avec icônes
- `Chip` - Badges
- `Typography` - Textes stylisés
- `CircularProgress` - Loading spinner
- `Alert` - Messages d'alerte

---

## ?? Avantages vs Blazor

### Performance
? **Plus rapide** - Vite build en millisecondes  
? **Hot Module Replacement** - Changements instantanés  
? **Bundle size optimisé** - Tree shaking automatique  

### Développement
? **Écosystème plus riche** - NPM packages illimités  
? **Developer Experience** - Meilleurs outils de debug  
? **Community** - Plus grande communauté React  

### UI/UX
? **Material-UI** - Design system professionnel  
? **Animations fluides** - Transitions CSS optimisées  
? **Responsive** - Mobile-first par défaut  

---

## ?? Documentation

### React
- https://react.dev/
- https://reactrouter.com/

### Material-UI
- https://mui.com/material-ui/
- https://mui.com/x/react-date-pickers/

### Vite
- https://vitejs.dev/

---

## ?? Dépannage

### Port 3000 déjà utilisé
```bash
# Changer le port dans vite.config.ts
server: {
  port: 3001
}
```

### Erreur CORS
L'API doit avoir CORS activé. Vérifier `MockScoreService.API/Program.cs`

### Hot reload ne fonctionne pas
```bash
# Restart du dev server
npm run dev
```

---

## ?? Prêt à Utiliser !

```bash
# Installation
npm install

# Démarrage
npm run dev

# Ouvrir
http://localhost:3000
```

**L'interface React est maintenant opérationnelle ! ??**
