# API BSD 24-26 — Deviner le Prix du Produit

## Stack
- Java 17 + Spring Boot 3
- Spring Security + JWT (jjwt 0.9.1)
- Spring Data JPA + MySQL
- Lombok

## Lancement rapide (Mac / VSCode)

### 1. Démarrer MySQL avec Docker
```bash
docker-compose up -d
```

### 2. Configurer `src/main/resources/application.properties`
Vérifiez les credentials MySQL (par défaut `root/root`).

### 3. Lancer l'API
```bash
./mvnw spring-boot:run
```
L'API démarre sur `http://localhost:8080`

---

## Endpoints

### Auth (public)
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/register` | Créer un compte |
| POST | `/api/auth/login` | Connexion → retourne JWT |

**Exemple register :**
```json
{ "email": "admin@test.com", "password": "1234", "admin": true }
```

**Exemple login :**
```json
{ "email": "admin@test.com", "password": "1234" }
```
→ Retourne `{ "token": "eyJ...", "email": "...", "admin": true }`

Ensuite, ajouter le header `Authorization: Bearer <token>` à toutes les requêtes.

---

### Produits (ADMIN uniquement)
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/produits` | Liste tous les produits |
| GET | `/api/produits/{id}` | Détail d'un produit |
| POST | `/api/produits` | Créer un produit |
| PUT | `/api/produits/{id}` | Modifier un produit |
| DELETE | `/api/produits/{id}` | Supprimer un produit |

**Exemple produit :**
```json
{ "nom": "PS5", "urlImage": "https://...", "prix": 499 }
```

---

### Sessions (ADMIN pour créer/modifier/supprimer)
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/sessions` | Liste toutes les sessions |
| GET | `/api/sessions/{id}` | Détail d'une session |
| POST | `/api/sessions` | Créer session (sélectionne 10 produits aléatoires) |
| PUT | `/api/sessions/{id}` | Modifier une session |
| DELETE | `/api/sessions/{id}` | Supprimer une session |
| POST | `/api/sessions/{id}/rejoindre` | Rejoindre une session (utilisateur connecté) |
| GET | `/api/sessions/{id}/questions` | Voir les 10 questions de la session |

**Exemple création session :**
```json
{ "nom": "Session 1", "nombreJoueur": 5 }
```

---

### Réponses
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/reponses/question/{questionId}` | Soumettre une réponse |
| GET | `/api/reponses/question/{questionId}` | Voir les réponses à une question |

**Exemple réponse :**
```json
{ "prix": 350 }
```

---

### Classement
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/classement/session/{sessionId}` | Classement des joueurs de la session |

**Logique de score :** chaque écart absolu entre le prix proposé et le prix réel est soustrait du score (départ à 0). Classement décroissant.

---

## Règles métier
- La création d'une session sélectionne **10 produits au hasard** parmi ceux en base (besoin d'au moins 10)
- Un utilisateur peut **rejoindre** une session
- Il peut **deviner le prix** de chacun des 10 produits (une réponse par question)
- Chaque écart coûte des points → classement décroissant
