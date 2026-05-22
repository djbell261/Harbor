# Harbor Observability Runbook

Harbor keeps local operations intentionally lightweight: Spring Actuator, Micrometer, Prometheus, Grafana, structured request logs, and Docker Compose.

## Local Observability Setup

Start the stack:

```sh
docker compose up --build
```

Useful endpoints:

```sh
curl -sS http://localhost:8081/actuator/health
curl -sS http://localhost:8081/actuator/health/readiness
curl -sS http://localhost:8081/actuator/prometheus
```

Prometheus is available at `http://localhost:9090`.

Grafana is available at `http://localhost:3000`.

Default local Grafana credentials:

```text
username: admin
password: harbor
```

The `Harbor Overview` dashboard is provisioned automatically from `observability/grafana/dashboards/harbor-overview.json`.

## Key Prometheus Queries

Request rate:

```promql
sum(rate(http_server_requests_seconds_count{application="resource-service"}[5m]))
```

4xx and 5xx error rate:

```promql
sum(rate(http_server_requests_seconds_count{application="resource-service",status=~"4..|5.."}[5m]))
```

P95 latency:

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application="resource-service"}[5m])) by (le))
```

Verification submissions:

```promql
sum(rate(harbor_verification_reports_total[5m])) by (report_type)
```

Rate-limit rejections:

```promql
rate(harbor_verification_rate_limit_rejections_total[5m])
```

Admin reviews:

```promql
sum(rate(harbor_admin_reviews_total[5m])) by (decision)
```

Stale resources:

```promql
harbor_resources_stale_total
```

Database connections:

```promql
hikaricp_connections_active{application="resource-service"}
```

## Structured Logs

Backend logs include a correlation ID, request path, status, duration, and severity category. Client-provided `X-Correlation-Id` is propagated to responses and logs; otherwise Harbor creates one per request.

Example:

```text
event=http_request_completed method=GET path=/api/resources/search status=200 durationMs=34 correlationId=...
```

Use the correlation ID to connect a frontend or curl failure to backend logs:

```sh
curl -i -H "X-Correlation-Id: smoke-test-1" http://localhost:8081/api/resources/search
docker logs harbor-resource-service | grep smoke-test-1
```

## Troubleshooting

Check container state:

```sh
docker compose ps
```

Check backend logs:

```sh
docker logs harbor-resource-service --tail=200
```

Check database logs:

```sh
docker logs harbor-postgres --tail=200
```

Check Prometheus target health:

```sh
open http://localhost:9090/targets
```

If Grafana has no data, confirm Prometheus can scrape `resource-service:8081` and that `http://localhost:8081/actuator/prometheus` returns metrics.

## Local Recovery

Restart only the backend:

```sh
docker compose restart resource-service
```

Restart observability services:

```sh
docker compose restart prometheus grafana
```

Rebuild the backend image:

```sh
docker compose build resource-service
docker compose up -d resource-service
```

Reset local observability data:

```sh
docker compose down
docker volume rm harbor_harbor_prometheus_data harbor_harbor_grafana_data
docker compose up -d prometheus grafana
```

Only reset Postgres when you are comfortable losing local data:

```sh
docker compose down
docker volume rm harbor_harbor_postgres_data
docker compose up --build
```

## DB Outage Simulation

Stop Postgres:

```sh
docker compose stop postgres
```

Expected behavior:

- `/actuator/health/readiness` reports unhealthy.
- API calls that need the database return structured `503 Service Unavailable`.
- Logs include an error categorized as a database operation failure.

Restore Postgres:

```sh
docker compose start postgres
```

Then verify:

```sh
curl -sS http://localhost:8081/actuator/health/readiness
curl -sS http://localhost:8081/api/resources/search
```
