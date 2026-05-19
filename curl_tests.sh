#!/bin/bash
# Script de tests fonctionnels - à exécuter après migration identreprise -> entreprise_id
# Usage: bash curl_tests.sh

BASE="http://localhost:8085"
USER="admin@default.com"
PASS="admin123"
TOKEN=""

echo "=== 1. AUTHENTIFICATION ==="
RESP=$(curl -s -X POST "$BASE/v1/auth/authenticate" \
  -H "Content-Type: application/json" \
  -d "{\"login\":\"$USER\",\"password\":\"$PASS\"}")
echo "$RESP" | jq .
TOKEN=$(echo "$RESP" | jq -r '.accessToken')
if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ LOGIN ÉCHOUÉ - Arrêt des tests"
  exit 1
fi
echo "✅ Token obtenu"

AUTH="Authorization: Bearer $TOKEN"

echo ""
echo "=== 2. CATEGORY CRUD ==="
# Create
CAT=$(curl -s -X POST "$BASE/v1/category/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '{"codeCategory":"TEST001","designation":"Categorie Test"}')
echo "Create: $(echo $CAT | jq '{id, codeCategory, designation}')"
CAT_ID=$(echo $CAT | jq -r '.id')

# Read
echo "Get by id: $(curl -s "$BASE/v1/category/$CAT_ID" -H "$AUTH" | jq '{id, codeCategory}')"

# Update
echo "Update: $(curl -s -X POST "$BASE/v1/category/save" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d "{\"id\":$CAT_ID,\"codeCategory\":\"TEST002\",\"designation\":\"Modifiée\"}" | jq '{id, codeCategory}')"

# Delete
echo "Delete: $(curl -s -X DELETE "$BASE/v1/category/delete/$CAT_ID" -H "$AUTH" -w "%{http_code}")"

echo ""
echo "=== 3. ARTICLE CRUD ==="
# Create category first
CAT=$(curl -s -X POST "$BASE/v1/category/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '{"codeCategory":"ARTTEST","designation":"Categorie Article"}')
CAT_ID=$(echo $CAT | jq -r '.id')

ART=$(curl -s -X POST "$BASE/v1/articles/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d "{\"codeArticle\":\"ART001\",\"designation\":\"Article Test\",\"category\":{\"id\":$CAT_ID}}")
echo "Create: $(echo $ART | jq '{id, codeArticle, designation}')"
ART_ID=$(echo $ART | jq -r '.id')

# Read
echo "Get by id: $(curl -s "$BASE/v1/articles/$ART_ID" -H "$AUTH" | jq '{id, codeArticle}')"

# Update
echo "Update: $(curl -s -X POST "$BASE/v1/articles/save" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d "{\"id\":$ART_ID,\"codeArticle\":\"ART002\",\"designation\":\"Modifié\",\"category\":{\"id\":$CAT_ID}}" | jq '{id, codeArticle}')"

# Delete
echo "Delete: $(curl -s -X DELETE "$BASE/v1/articles/delete/$ART_ID" -H "$AUTH" -w "%{http_code}")"

echo ""
echo "=== 4. CLIENT CRUD ==="
CLI=$(curl -s -X POST "$BASE/v1/client/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '{"nom":"Client Test","prenom":"Test","email":"client@test.com","numTel":"0123456789"}')
echo "Create: $(echo $CLI | jq '{id, nom, email}')"
CLI_ID=$(echo $CLI | jq -r '.id')

echo "Delete: $(curl -s -X DELETE "$BASE/v1/client/delete/$CLI_ID" -H "$AUTH" -w "%{http_code}")"

echo ""
echo "=== 5. FOURNISSEUR CRUD ==="
FOUR=$(curl -s -X POST "$BASE/v1/fournisseur/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '{"nom":"Fournisseur Test","prenom":"Test","email":"four@test.com","numTel":"0123456789"}')
echo "Create: $(echo $FOUR | jq '{id, nom, email}')"
FOUR_ID=$(echo $FOUR | jq -r '.id')

echo "Delete: $(curl -s -X DELETE "$BASE/v1/fournisseur/delete/$FOUR_ID" -H "$AUTH" -w "%{http_code}")"

echo ""
echo "=== TESTS TERMINÉS ==="
