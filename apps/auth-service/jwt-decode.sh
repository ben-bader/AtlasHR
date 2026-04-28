#!/bin/bash

# Usage:
# ./jwt-decode.sh <JWT_TOKEN>

TOKEN=$1

if [ -z "$TOKEN" ]; then
  echo "Usage: ./jwt-decode.sh <JWT_TOKEN>"
  exit 1
fi

# Split JWT into parts
HEADER=$(echo "$TOKEN" | cut -d "." -f1)
PAYLOAD=$(echo "$TOKEN" | cut -d "." -f2)

echo "================ HEADER ================"
echo "$HEADER" | base64 -d 2>/dev/null | jq .

echo ""
echo "=============== PAYLOAD ================"
echo "$PAYLOAD" | base64 -d 2>/dev/null | jq .
