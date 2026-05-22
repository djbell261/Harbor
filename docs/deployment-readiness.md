# Harbor Deployment Readiness

Harbor is still a lightweight MVP+ application, but it now has enough environment separation to deploy safely without changing product behavior.

## Spring Profiles

Use one of these profiles:

- `local`: local host development, local PostgreSQL on `localhost:5434`, local Vite CORS, OpenAPI enabled, seed data enabled.
- `docker`: Docker Compose development, PostgreSQL service hostname `postgres`, local Vite CORS, OpenAPI enabled, seed data enabled.
- `prod`: production runtime, externally supplied database settings, explicit CORS origins, OpenAPI disabled by default, seed data disabled.

Production must set:

```sh
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>
SPRING_DATASOURCE_USERNAME=<db-user>
SPRING_DATASOURCE_PASSWORD=<db-password>
HARBOR_CORS_ALLOWED_ORIGIN_PATTERNS=https://<frontend-domain>
HARBOR_SEED_DATA_ENABLED=false
HARBOR_OPENAPI_ENABLED=false
```

Optional deployment metadata:

```sh
HARBOR_APP_VERSION=0.1.0
HARBOR_BUILD_ID=<ci-run-id>
HARBOR_GIT_COMMIT=<git-sha>
```

## Backend Deployment

Build the backend image:

```sh
docker build -t harbor-resource-service:<version> resource-service
```

Run with the `prod` profile and production secrets injected by the runtime environment. Do not bake secrets into the image.

Health endpoints:

```sh
curl -sS https://<api-domain>/actuator/health
curl -sS https://<api-domain>/actuator/health/readiness
curl -sS https://<api-domain>/api/version
```

Expose only the application port to the private load balancer or public API gateway. Keep direct database access private.

## Frontend Deployment

Build with the production API base URL:

```sh
cd web-app
npm ci
VITE_API_BASE_URL=https://<api-domain> npm run build
```

Deploy `web-app/dist` to static hosting or a CDN-backed web host.

The frontend domain must be included in `HARBOR_CORS_ALLOWED_ORIGIN_PATTERNS` on the backend.

## Database Migration Strategy

Harbor uses Flyway. Production deployments should:

1. Back up the database before migration.
2. Deploy the backend image with `SPRING_PROFILES_ACTIVE=prod`.
3. Let Flyway apply schema migrations at startup, or run the same image once as a migration job if the hosting platform prefers separate release phases.
4. Verify `/actuator/health/readiness`.
5. Verify `/api/version` reports the expected build.

Seed data is guarded by `HARBOR_SEED_DATA_ENABLED`. Keep it `false` in production.

## Rollback Plan

For application-only rollback:

1. Repoint the runtime to the previous backend image tag.
2. Repoint the frontend host/CDN to the previous static build.
3. Verify `/actuator/health/readiness` and core resource APIs.

For database rollback:

1. Prefer forward-fix migrations when possible.
2. If a migration caused data loss or unrecoverable behavior, restore from the pre-deploy backup.
3. Redeploy the last known good backend image against the restored database.

Do not run destructive database commands during rollback without a backup and an explicit recovery target.

## Health Check Verification

After deploy:

```sh
curl -i https://<api-domain>/actuator/health
curl -i https://<api-domain>/actuator/health/readiness
curl -i https://<api-domain>/api/version
curl -i "https://<api-domain>/api/resources/search?page=0&size=1"
```

Expected:

- Health is `UP`.
- Readiness includes database connectivity.
- Version metadata matches the deployed build.
- Search returns a structured page response.

## OpenAPI in Production

OpenAPI and Swagger UI are disabled by default in `prod`.

Enable only for controlled troubleshooting:

```sh
HARBOR_OPENAPI_ENABLED=true
```

Then disable again after use.

## Local Docker Compose

The root `docker-compose.yml` is a local/dev stack only. It includes PostgreSQL, the resource service, Prometheus, and Grafana for development observability. It is not a production deployment manifest.
