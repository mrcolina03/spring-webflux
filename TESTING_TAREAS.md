# Guía rápida para probar las tareas reactivas

Todas las rutas usan el prefijo `/api/gptcodex/sensors` salvo donde se indique lo contrario. Ejecuta la aplicación con `./gradlew bootRun` y usa otro terminal para las pruebas.

## Datos iniciales
1) Inserta una lectura de ejemplo:
   ```bash
   curl -X POST http://localhost:8080/api/gptcodex/sensors \
     -H "Content-Type: application/json" \
     -d '{"id":"sensor-1","temperature":25,"timestamp":"2024-01-01T00:00:00Z"}'
   ```
2) Genera lecturas automáticas (TAREA 2) si necesitas más datos:
   ```bash
   curl http://localhost:8080/api/gptcodex/sensors/generate
   ```

## Endpoints REST principales
- **TAREA 1 – Promedio por sensor**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/sensor-1/average
  ```
- **TAREA 4 y 12 – Estadísticas globales y promedio global**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/stats
  curl http://localhost:8080/api/gptcodex/sensors/average
  ```
- **TAREA 8 y 19 – Streams SSE filtrados y alertas** (mantén la conexión abierta):
  ```bash
  curl -N http://localhost:8080/api/gptcodex/sensors/stream/hot?threshold=30
  curl -N http://localhost:8080/api/gptcodex/sensors/alerts?threshold=35
  ```
- **TAREA 9 – Últimos N valores**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/last/5
  ```
- **TAREA 10 – Temperaturas en Fahrenheit**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/fahrenheit
  ```
- **TAREA 11 – Lecturas agrupadas por sensor**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/grouped
  ```
- **TAREA 14 – Paginación simple**
  ```bash
  curl 'http://localhost:8080/api/gptcodex/sensors/page?page=0&size=3'
  ```
- **TAREA 15 – Sensores únicos**
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors/unique
  ```
- **TAREA 17 – Eliminar lecturas**
  ```bash
  curl -X DELETE http://localhost:8080/api/gptcodex/sensors
  ```
- **TAREA 18 – Histórico por rango**
  ```bash
  curl 'http://localhost:8080/api/gptcodex/sensors/history?from=2024-01-01T00:00:00&to=2024-12-31T23:59:59'
  ```

## Streaming SSE (TAREA 3)
- Escucha el stream principal con metadatos (hot stream). Usa `-N` para no cortar la conexión:
  ```bash
  curl -N http://localhost:8080/api/gptcodex/sensors/stream
  ```
  Cada evento incluye `id`, `event` y `data` con timestamp.

## Endpoints funcionales (TAREAS 6 y 20)
- Listar todas las lecturas con router function:
  ```bash
  curl http://localhost:8080/api/gptcodex/sensors-functional
  ```
- Crear lectura vía handler funcional:
  ```bash
  curl -X POST http://localhost:8080/api/gptcodex/sensors-functional \
    -H "Content-Type: application/json" \
    -d '{"id":"sensor-2","temperature":28}'
  ```

## Validaciones y errores (TAREA 5)
- Intenta crear una lectura inválida para ver la respuesta del manejador global:
  ```bash
  curl -X POST http://localhost:8080/api/gptcodex/sensors \
    -H "Content-Type: application/json" \
    -d '{"id":"","temperature":200}'
  ```
  Debe responder 400 con JSON de error.

## Filtro reactivo global (TAREA 7)
- Envía cualquier petición bajo `/api/gptcodex/sensors` y revisa el header `X-Elapsed-Time-Reactive` y los logs en consola:
  ```bash
  curl -i http://localhost:8080/api/gptcodex/sensors
  ```

## Streams calientes adicionales (TAREA 8 y 19)
- Los endpoints `stream/hot` y `alerts` reaccionan a nuevas lecturas en tiempo real. En otra terminal, ejecuta:
  ```bash
  curl -X POST http://localhost:8080/api/gptcodex/sensors \
    -H "Content-Type: application/json" \
    -d '{"id":"sensor-3","temperature":40}'
  ```
  Deberías ver la lectura aparecer en los streams abiertos.

## Pruebas automatizadas (TAREA 16)
- Ejecuta los tests reactivos con StepVerifier:
  ```bash
  ./gradlew test
  ```
  *Nota:* en entornos con proxy restringido puede fallar la descarga del wrapper; si sucede, usa un Gradle local.

