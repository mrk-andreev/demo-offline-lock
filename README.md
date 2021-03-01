# Offline lock

Operations

- `GET /` list all keys
- `GET /expire-at/{key}` get expire timestamp
- `POST /lock/{key}/{duration}` create lock
- `POST /refresh/{key}/{password}/{duration}` refresh lock
- `POST /unlock/{key}/{password}` unlock
