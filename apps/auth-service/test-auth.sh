#!/bin/bash

#############################################
# Auth Service Quick Test Script
# Usage: ./test-auth.sh
#############################################

set -e

BASE_URL="${1:-http://localhost:8080/api/auth}"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

TESTS_PASSED=0
TESTS_FAILED=0

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Auth Service Test Suite              ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""
echo -e "Target: ${YELLOW}$BASE_URL${NC}"
echo ""

# Helper functions
test_case() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local expected_code="$4"
    local data="$5"
    local headers="${6:-Content-Type: application/json}"
    
    echo -n "Testing: $test_name ... "
    
    if [ -z "$data" ]; then
        response=$(curl -s -X "$method" "$BASE_URL$endpoint" \
            -H "$headers" \
            -w "\n%{http_code}" 2>/dev/null)
    else
        response=$(curl -s -X "$method" "$BASE_URL$endpoint" \
            -H "$headers" \
            -d "$data" \
            -w "\n%{http_code}" 2>/dev/null)
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        ((TESTS_PASSED++))
        echo "$body"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (Expected: $expected_code, Got: $http_code)"
        ((TESTS_FAILED++))
        return 1
    fi
}

# Test 1: Health Check
echo -e "${BLUE}=== Basic Health Check ===${NC}"
test_case "Health Check" "GET" "/health" "200" ""
echo ""

# Test 2: Register New User
echo -e "${BLUE}=== User Registration ===${NC}"
REGISTER_DATA='{
  "username": "testuser_'"$(date +%s)"'",
  "email": "test_'"$(date +%s)"'@example.com",
  "password": "password123"
}'

REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
    -H "Content-Type: application/json" \
    -d "$REGISTER_DATA")

echo "Response: $REGISTER_RESPONSE"

USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"userId":[0-9]*' | grep -o '[0-9]*' || echo "")
if [ ! -z "$USER_ID" ]; then
    echo -e "${GREEN}✓ Registration successful${NC} (User ID: $USER_ID)"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ Registration failed${NC}"
    ((TESTS_FAILED++))
fi
echo ""

# Extract credentials for next test
USERNAME=$(echo "$REGISTER_DATA" | grep -o '"username":"[^"]*' | cut -d'"' -f4)
PASSWORD=$(echo "$REGISTER_DATA" | grep -o '"password":"[^"]*' | cut -d'"' -f4)

# Test 3: Login User
echo -e "${BLUE}=== User Login ===${NC}"
LOGIN_DATA='{
  "username": "'"$USERNAME"'",
  "password": "'"$PASSWORD"'"
}'

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "$LOGIN_DATA")

echo "Response: $LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | head -n1 | cut -d'"' -f4 || echo "")
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"refreshToken":"[^"]*' | cut -d'"' -f4 || echo "")

if [ ! -z "$TOKEN" ]; then
    echo -e "${GREEN}✓ Login successful${NC}"
    ((TESTS_PASSED++))
    echo "Access Token: ${TOKEN:0:20}..."
else
    echo -e "${RED}✗ Login failed${NC}"
    ((TESTS_FAILED++))
    echo "No token received"
fi
echo ""

# Test 4: Get Current User (with token)
if [ ! -z "$TOKEN" ]; then
    echo -e "${BLUE}=== Get Current User (Authenticated) ===${NC}"
    ME_RESPONSE=$(curl -s -X GET "$BASE_URL/me" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "Response: $ME_RESPONSE"
    
    if echo "$ME_RESPONSE" | grep -q '"username"'; then
        echo -e "${GREEN}✓ Get user successful${NC}"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ Get user failed${NC}"
        ((TESTS_FAILED++))
    fi
    echo ""
    
    # Test 5: Invalid Token Test
    echo -e "${BLUE}=== Invalid Token Test ===${NC}"
    INVALID_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/me" \
        -H "Authorization: Bearer invalid_token_123")
    
    http_code=$(echo "$INVALID_RESPONSE" | tail -n1)
    if [ "$http_code" = "401" ]; then
        echo -e "${GREEN}✓ Invalid token correctly rejected${NC} (HTTP $http_code)"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ Invalid token not rejected${NC} (HTTP $http_code)"
        ((TESTS_FAILED++))
    fi
    echo ""
    
    # Test 6: Refresh Token
    if [ ! -z "$REFRESH_TOKEN" ]; then
        echo -e "${BLUE}=== Refresh Token ===${NC}"
        REFRESH_RESPONSE=$(curl -s -X POST "$BASE_URL/refresh" \
            -H "Authorization: Bearer $REFRESH_TOKEN")
        
        echo "Response: $REFRESH_RESPONSE"
        
        if echo "$REFRESH_RESPONSE" | grep -q '"token"'; then
            echo -e "${GREEN}✓ Token refresh successful${NC}"
            ((TESTS_PASSED++))
        else
            echo -e "${RED}✗ Token refresh failed${NC}"
            ((TESTS_FAILED++))
        fi
        echo ""
    fi
else
    echo -e "${YELLOW}⊘ Skipping authenticated tests (no token)${NC}"
    echo ""
fi

# Test 7: Duplicate Registration
echo -e "${BLUE}=== Duplicate User Test ===${NC}"
DUPLICATE_DATA='{
  "username": "'"$USERNAME"'",
  "email": "different@example.com",
  "password": "password123"
}'

DUPLICATE_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
    -H "Content-Type: application/json" \
    -d "$DUPLICATE_DATA")

if echo "$DUPLICATE_RESPONSE" | grep -q "already exists"; then
    echo -e "${GREEN}✓ Duplicate username correctly rejected${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ Duplicate username not rejected${NC}"
    ((TESTS_FAILED++))
fi
echo ""

# Test 8: Invalid Login
echo -e "${BLUE}=== Invalid Login Test ===${NC}"
INVALID_LOGIN='{
  "username": "'"$USERNAME"'",
  "password": "wrongpassword"
}'

INVALID_LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "$INVALID_LOGIN")

http_code=$(echo "$INVALID_LOGIN_RESPONSE" | tail -n1)
if [ "$http_code" = "401" ]; then
    echo -e "${GREEN}✓ Invalid credentials correctly rejected${NC} (HTTP $http_code)"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ Invalid credentials not rejected${NC} (HTTP $http_code)"
    ((TESTS_FAILED++))
fi
echo ""

# Summary
echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         Test Results Summary           ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo -e "Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Failed: ${RED}$TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}✗ Some tests failed${NC}"
    exit 1
fi
