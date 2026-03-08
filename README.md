
# Traffic Controller Data Integration

This is a Spring Boot 3.5 service that integrates with remote traffic controllers via a
protocol adapter, deserialize and transform device data into a dedicated model, and
exposes REST APIs for monitoring and control.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Technologies](#technologies)
3. [API Reference](#api-reference)
4. [Local Deployment with Docker Compose](#local-deployment-with-docker-compose)

---

## Architecture Overview

```
RestContollers
   │
   ▼
TrafficControllerManager ───▶ redis
IngestionScheduler ───▶ ProtocolAdapter
   │
   ▼
JPAService 
   │
   ▼
Repository ───▶ postgress
```

**Flow summary:**
1. `IngestionScheduler` fires every 15 seconds.
2.  For each registered controller, fetch status and detector readings.
3. `TrafficControllerMapper` converts raw protocol payloads into JPA entities.
4. Entities are persisted to **PostgreSQL** with full history.
5. **Redis** caches the current-status and latest-detector-reading and delete them after each ingest.
6. REST API clients get cached responses from redis if exists, DB otherwise

---

## Technologies

### PostgreSQL

The data is inherently relational:

- Due to the fact that we don't actually own the key `controllerId` we are using in each table a surrogate ID as the PK

##### Schema

**`controllers`** — represents a physical traffic controller device. The source of truth for all registered controllers. All other tables reference it via `controller_id`.

**`controller_status`** — append-only history of status snapshots reported by each controller. Each row captures the controller state (e.g. `OPERATIONAL`, `SIGNAL_FAILURE`), the active signal program, any active error codes (stored as JSONB), and the timestamp reported by the device itself (`device_timestamp`) vs when our system recorded it (`created_at`). The gap between these two timestamps reflects the processing latency between
when the device reported the data and when our system persisted it to the database.

**`detector_readings`** — append-only history of detector readings received from each controller. Each row represents a single detector's vehicle count and occupancy at a point in time. A controller may report multiple detectors per ingestion cycle.

**`commands`** — audit trail of every command dispatched to a controller. Records whether the command succeeded, the result value returned by the device, and the full timestamp history. Commands are never cached since they are write operations that must always reach the physical device.

##### Indexes

Each table is indexed on `controller_id` for fast lookup by controller, and on `created_at DESC` / `device_timestamp DESC` for efficient time-ordered queries and pagination.
### Redis

Due to high intensity, we would like to reduce connections to the DB as much as we can.
TThe blow describes the flow for `controllerStatus` and `detectorReadings`:

- Calls the mock adapter → gets fresh data periodically
- Writes new rows to **PostgreSQL**
- `@CacheEvict` deletes the Redis entry for the specific key: `fd11.z1.downtown.loc`
- Dashboard calls `GET /status/fd11.z1.downtown.loc`
- If Redis contains data, it retrieves it; otherwise it goes to the DB and caches the entry by controllerId in Redis for next time.

Commands are not applicable for caching since they are write operations.

### Flyway for DB migrations

Schema changes are versioned and applied automatically on startup.
- Schema Location: `src/main/resources/db/migration/V1__initial_schema.sql`

---

## API Reference

All endpoints are exposed and can be viewed in Swagger at `http://localhost:8080/swagger-ui/index.html`.

---

## Local Deployment with Docker Compose

**Prerequisites:** Docker Desktop / Docker Engine + Compose plugin

```bash
# 1. Clone / unzip the project
cd traffic-controller
cd .docker-compose

# 2. Build and start everything
$ docker-compose up -d

# 3. Verify it's running
curl http://localhost:8080/actuator/health

# 4. Access Swagger / Open API speck 
hit the browser with http://localhost:8080/swagger-ui/index.html
```
**Stop everything:**
```bash
$ docker-compose down
```