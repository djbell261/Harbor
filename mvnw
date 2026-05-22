#!/bin/sh
cd "$(dirname "$0")/resource-service" || exit 1
exec ./mvnw "$@"
