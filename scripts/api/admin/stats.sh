#!/bin/sh

curl -H "X-Auth-Token: $SETTINGS_ADMIN_PASSWORD" "http://localhost:8080/api/admin/stats" -v