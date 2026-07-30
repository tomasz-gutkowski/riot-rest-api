# Riot REST API
Backend for **Paradox** - a League of Legends stats and match history lookup tool. This service wraps the [Riot Games API](https://developer.riotgames.com/), adding multi-region routing, response caching, and structured error handling behind a clean REST layer consumed by the [Paradox frontend](https://github.com/tomasz-gutkowski/paradox-web).
**Live API:** [riot-rest-api-backend.onrender.com](https://riot-rest-api-backend.onrender.com)
**Live demo:** [paradox-gg.vercel.app](https://paradox-gg.vercel.app)
**Frontend repo:** [paradox-web](https://github.com/tomasz-gutkowski/paradox-web)
> Hosted on Render's free tier - the first request after a period of inactivity may take 50+ seconds while the service wakes up.
## Features
- **Summoner lookup** by Riot ID across all available regions
- **Match history** retrieval with pagination
- **Match detail** aggregation, including mode-specific data (e.g. Arena vs. Summoner's Rift and other modes)
- **Data Dragon & Community Dragon** integration for champions, items, augments, and other static game assets, kept in sync with the latest game version
- **Rate limiting** (Bucket4j) applied both on the Server → Riot API side, to respect the provider's rate limits, and on the Client → Server side, to prevent abuse of the service
- **Response caching** (Caffeine) for immutable Riot API lookups like match details
- **Structured error handling** mapping Riot's HTTP error responses to a dedicated exception hierarchy (`4xx`/`5xx`), returned as consistent JSON error bodies
## Tech stack
- Java 21
- Spring Boot 4 (Spring MVC, Spring Cache)
- Gradle
- Bucket4j
- Caffeine (local in-memory caching)
- Docker
- Riot Games API + Data Dragon & Community Dragon
## API overview
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/profile/{serverId}/{gameName}/{tagLine}` | Returns summoner & ranked profile info |
| GET | `/api/matches/{serverId}/{puuid}/{endTime}?start=&count=` | Returns a paginated list of match summary data in a specified range |
| GET | `/api/match/{serverId}/{matchId}` | Returns detailed data for a given match |
| GET | `/api/ddragon/latest` | Returns the Data Dragon version |
## Demo
All of the API calls above can be tested against https://riot-rest-api-backend.onrender.com (as mentioned earlier, after an extended period of inactivity, the server takes 50+ seconds to wake up), but the recommended way of accessing the service is the [Paradox frontend](https://github.com/tomasz-gutkowski/paradox-web).
## Getting started
### Prerequisites
- A [Riot Games API key](https://developer.riotgames.com/)
- Either JDK 21+, **or** Docker
### Run with Docker (recommended)
```bash
git clone https://github.com/tomasz-gutkowski/riot-rest-api.git
cd riot-rest-api
cp .env.example .env   # add your RIOT_API_KEY
docker compose up --build
```
### Run with Gradle
#### Unix
```
./gradlew bootRun
```
#### Windows
```
gradlew.bat bootRun
```
### Environment variables
| Variable | Description | Default |
|---|---|---|
| `RIOT_API_KEY` | Your Riot Games API key | ~ (required) |
| `CORS_ALLOWED_ORIGIN` | Origin allowed to call this API | `http://localhost:5173` |
| `PORT` | Port the server listens on | `8080` |