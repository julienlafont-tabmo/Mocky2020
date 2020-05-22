#!/bin/sh

id="$1"

curl -X DELETE -H "X-Auth-Token: $SETTINGS_ADMIN_PASSWORD" "http://localhost:8080/api/admin/$id" -v