# Harbor Reliability v1 Smoke Tests

These checks assume the resource service is running on `http://localhost:8081` with seed data loaded.

Set a known public resource ID:

```sh
RESOURCE_ID=11111111-1111-4111-8111-111111111111
```

Submit a report:

```sh
curl -sS -X POST "http://localhost:8081/api/resources/$RESOURCE_ID/verification-reports" \
  -H "Content-Type: application/json" \
  -d '{"reportType":"incorrect_hours","description":"Posted Friday hours changed."}'
```

List pending reports:

```sh
curl -sS "http://localhost:8081/api/admin/verification-reports?status=pending"
```

Get one report:

```sh
REPORT_ID=<id from pending list>
curl -sS "http://localhost:8081/api/admin/verification-reports/$REPORT_ID"
```

Accept a report:

```sh
curl -sS -X POST "http://localhost:8081/api/admin/verification-reports/$REPORT_ID/accept" \
  -H "Content-Type: application/json" \
  -d '{"reviewNotes":"Confirmed by provider phone call.","reviewedBy":"admin"}'
```

Reject a report:

```sh
curl -sS -X POST "http://localhost:8081/api/admin/verification-reports/$REPORT_ID/reject" \
  -H "Content-Type: application/json" \
  -d '{"reviewNotes":"Could not confirm the submitted change.","reviewedBy":"admin"}'
```

Check stale and needs-verification flags:

```sh
curl -sS "http://localhost:8081/api/resources/$RESOURCE_ID"
```

Expected detail fields include `stale`, `needsVerification`, `statusHistory`, `communityUpdates`, and `verification.pendingReportCount`.
