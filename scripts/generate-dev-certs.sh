#!/usr/bin/env bash
set -euo pipefail

#
# Dev-only mTLS cert generation for QTicket.
#
# Creates:
#   backend/certs/dev-ca.key
#   backend/certs/dev-ca.crt
#   backend/certs/mtls-truststore.jks
#
#   backend/certs/auth-service/auth-service.key
#   backend/certs/auth-service/auth-service.crt
#   backend/certs/auth-service/auth-service.p12
#
#   backend/certs/gateway/gateway-client.key
#   backend/certs/gateway/gateway-client.crt
#   backend/certs/gateway/gateway-client.p12
#
#   backend/certs/gateway/gateway-server.key
#   backend/certs/gateway/gateway-server.crt
#   backend/certs/gateway/gateway-server.p12
#
# DO NOT USE THIS IN PROD.
#

# ==== Config =================================================================
BASE_DIR="${BASE_DIR:-backend/certs}"

CA_SUBJECT="${CA_SUBJECT:-/CN=QTicket Dev CA}"
AUTH_SERVICE_CN="${AUTH_SERVICE_CN:-auth-service-dev}"
USER_SERVICE_CN="${AUTH_SERVICE_CN:-user-service-dev}"
GATEWAY_CLIENT_CN="${GATEWAY_CLIENT_CN:-gateway-client-dev}"
GATEWAY_SERVER_CN="${GATEWAY_SERVER_CN:-gateway-server-dev}"

KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:-changeit}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-changeit}"
TRUSTSTORE_ALIAS="${TRUSTSTORE_ALIAS:-qticket-dev-ca}"

DAYS="${DAYS:-365}"

# SANs for local dev. Adjust as needed.
AUTH_SERVICE_SAN="${AUTH_SERVICE_SAN:-DNS:localhost,DNS:auth-service-dev,IP:127.0.0.1}"
USER_SERVICE_SAN="${USER_SERVICE_SAN:-DNS:localhost,DNS:USER-service-dev,IP:127.0.0.1}"
GATEWAY_CLIENT_SAN="${GATEWAY_CLIENT_SAN:-DNS:localhost,IP:127.0.0.1}"
GATEWAY_SERVER_SAN="${GATEWAY_SERVER_SAN:-DNS:localhost,IP:127.0.0.1}"

CA_DIR="${BASE_DIR}"
AUTH_DIR="${BASE_DIR}/auth-service"
USER_DIR="${BASE_DIR}/user-service"
GATEWAY_DIR="${BASE_DIR}/gateway"

mkdir -p "${CA_DIR}" "${AUTH_DIR}" "${USER_DIR}" "${GATEWAY_DIR}"

echo "CA dir:       ${CA_DIR}"
echo "Auth dir:     ${AUTH_DIR}"
echo "Auth dir:     ${USER_DIR}"
echo "Gateway dir:  ${GATEWAY_DIR}"

# ==== Helper to write OpenSSL config with SAN ================================
create_openssl_cnf() {
  local cn="$1"
  local san="$2"
  local file="$3"

  cat > "${file}" <<EOF
[ req ]
default_bits       = 4096
distinguished_name = req_distinguished_name
req_extensions     = v3_req
prompt             = no

[ req_distinguished_name ]
CN = ${cn}

[ v3_req ]
basicConstraints = CA:FALSE
keyUsage         = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName   = ${san}
EOF
}

# ==== 1. Dev CA (shared) =====================================================
if [[ -f "${CA_DIR}/dev-ca.key" || -f "${CA_DIR}/dev-ca.crt" ]]; then
  echo "Dev CA already exists in ${CA_DIR}, skipping CA generation."
else
  echo "Generating dev CA in ${CA_DIR}..."
  openssl genrsa -out "${CA_DIR}/dev-ca.key" 4096
  openssl req -x509 -new -nodes -key "${CA_DIR}/dev-ca.key" -sha256 -days "${DAYS}" \
    -subj "${CA_SUBJECT}" \
    -out "${CA_DIR}/dev-ca.crt"
fi

# Convenience variables
CA_KEY="${CA_DIR}/dev-ca.key"
CA_CRT="${CA_DIR}/dev-ca.crt"

# ==== 2. Auth-service server certificate ====================================
if [[ -f "${AUTH_DIR}/auth-service.key" || -f "${AUTH_DIR}/auth-service.crt" ]]; then
  echo "Auth-service cert already exists in ${AUTH_DIR}, skipping generation."
else
  echo "Generating auth-service server certificate..."
  AUTH_CONF="$(mktemp)"
  create_openssl_cnf "${AUTH_SERVICE_CN}" "${AUTH_SERVICE_SAN}" "${AUTH_CONF}"

  openssl genrsa -out "${AUTH_DIR}/auth-service.key" 4096
  openssl req -new \
    -key "${AUTH_DIR}/auth-service.key" \
    -out "${AUTH_DIR}/auth-service.csr" \
    -config "${AUTH_CONF}"

  openssl x509 -req \
    -in "${AUTH_DIR}/auth-service.csr" \
    -CA "${CA_CRT}" -CAkey "${CA_KEY}" -CAcreateserial \
    -out "${AUTH_DIR}/auth-service.crt" \
    -days "${DAYS}" -sha256 \
    -extensions v3_req -extfile "${AUTH_CONF}"

  rm -f "${AUTH_CONF}" "${AUTH_DIR}/auth-service.csr"
fi

# ==== 2.1. User-service server certificate ====================================
if [[ -f "${USER_DIR}/user-service.key" || -f "${USER_DIR}/user-service.crt" ]]; then
  echo "User-service cert already exists in ${USER_DIR}, skipping generation."
else
  echo "Generating user-service server certificate..."
  USER_CONF="$(mktemp)"
  create_openssl_cnf "${USER_SERVICE_CN}" "${USER_SERVICE_SAN}" "${USER_CONF}"

  openssl genrsa -out "${USER_DIR}/user-service.key" 4096
  openssl req -new \
    -key "${USER_DIR}/user-service.key" \
    -out "${USER_DIR}/user-service.csr" \
    -config "${USER_CONF}"

  openssl x509 -req \
    -in "${USER_DIR}/user-service.csr" \
    -CA "${CA_CRT}" -CAkey "${CA_KEY}" -CAcreateserial \
    -out "${USER_DIR}/user-service.crt" \
    -days "${DAYS}" -sha256 \
    -extensions v3_req -extfile "${USER_CONF}"

  rm -f "${USER_CONF}" "${USER_DIR}/user-service.csr"
fi

# ==== 3. Gateway client certificate (outbound mTLS) ==========================
if [[ -f "${GATEWAY_DIR}/gateway-client.key" || -f "${GATEWAY_DIR}/gateway-client.crt" ]]; then
  echo "Gateway client cert already exists in ${GATEWAY_DIR}, skipping generation."
else
  echo "Generating gateway client certificate..."
  GATEWAY_CLIENT_CONF="$(mktemp)"
  create_openssl_cnf "${GATEWAY_CLIENT_CN}" "${GATEWAY_CLIENT_SAN}" "${GATEWAY_CLIENT_CONF}"

  openssl genrsa -out "${GATEWAY_DIR}/gateway-client.key" 4096
  openssl req -new \
    -key "${GATEWAY_DIR}/gateway-client.key" \
    -out "${GATEWAY_DIR}/gateway-client.csr" \
    -config "${GATEWAY_CLIENT_CONF}"

  openssl x509 -req \
    -in "${GATEWAY_DIR}/gateway-client.csr" \
    -CA "${CA_CRT}" -CAkey "${CA_KEY}" -CAcreateserial \
    -out "${GATEWAY_DIR}/gateway-client.crt" \
    -days "${DAYS}" -sha256 \
    -extensions v3_req -extfile "${GATEWAY_CLIENT_CONF}"

  rm -f "${GATEWAY_CLIENT_CONF}" "${GATEWAY_DIR}/gateway-client.csr"
fi

# ==== 4. Gateway server certificate (edge HTTPS) =============================
if [[ -f "${GATEWAY_DIR}/gateway-server.key" || -f "${GATEWAY_DIR}/gateway-server.crt" ]]; then
  echo "Gateway server cert already exists in ${GATEWAY_DIR}, skipping generation."
else
  echo "Generating gateway server certificate..."
  GATEWAY_SERVER_CONF="$(mktemp)"
  create_openssl_cnf "${GATEWAY_SERVER_CN}" "${GATEWAY_SERVER_SAN}" "${GATEWAY_SERVER_CONF}"

  openssl genrsa -out "${GATEWAY_DIR}/gateway-server.key" 4096
  openssl req -new \
    -key "${GATEWAY_DIR}/gateway-server.key" \
    -out "${GATEWAY_DIR}/gateway-server.csr" \
    -config "${GATEWAY_SERVER_CONF}"

  openssl x509 -req \
    -in "${GATEWAY_DIR}/gateway-server.csr" \
    -CA "${CA_CRT}" -CAkey "${CA_KEY}" -CAcreateserial \
    -out "${GATEWAY_DIR}/gateway-server.crt" \
    -days "${DAYS}" -sha256 \
    -extensions v3_req -extfile "${GATEWAY_SERVER_CONF}"

  rm -f "${GATEWAY_SERVER_CONF}" "${GATEWAY_DIR}/gateway-server.csr"
fi

# ==== 5. PKCS12 keystores ====================================================
echo "Creating PKCS12 keystore for auth-service..."
openssl pkcs12 -export \
  -inkey "${AUTH_DIR}/auth-service.key" \
  -in "${AUTH_DIR}/auth-service.crt" \
  -certfile "${CA_CRT}" \
  -name "auth-service" \
  -out "${AUTH_DIR}/auth-service.p12" \
  -password "pass:${KEYSTORE_PASSWORD}"

echo "Creating PKCS12 keystore for user-service..."
openssl pkcs12 -export \
  -inkey "${USER_DIR}/user-service.key" \
  -in "${USER_DIR}/user-service.crt" \
  -certfile "${CA_CRT}" \
  -name "user-service" \
  -out "${USER_DIR}/user-service.p12" \
  -password "pass:${KEYSTORE_PASSWORD}"

echo "Creating PKCS12 keystore for gateway client..."
openssl pkcs12 -export \
  -inkey "${GATEWAY_DIR}/gateway-client.key" \
  -in "${GATEWAY_DIR}/gateway-client.crt" \
  -certfile "${CA_CRT}" \
  -name "gateway-client" \
  -out "${GATEWAY_DIR}/gateway-client.p12" \
  -password "pass:${KEYSTORE_PASSWORD}"

echo "Creating PKCS12 keystore for gateway server..."
openssl pkcs12 -export \
  -inkey "${GATEWAY_DIR}/gateway-server.key" \
  -in "${GATEWAY_DIR}/gateway-server.crt" \
  -certfile "${CA_CRT}" \
  -name "gateway-server" \
  -out "${GATEWAY_DIR}/gateway-server.p12" \
  -password "pass:${KEYSTORE_PASSWORD}"

# ==== 6. JKS truststore with CA (shared) =====================================
echo "Creating JKS truststore with dev CA..."
if command -v keytool >/dev/null 2>&1; then
  rm -f "${CA_DIR}/mtls-truststore.jks"
  keytool -importcert \
    -noprompt \
    -alias "${TRUSTSTORE_ALIAS}" \
    -file "${CA_CRT}" \
    -keystore "${CA_DIR}/mtls-truststore.jks" \
    -storepass "${TRUSTSTORE_PASSWORD}"
else
  echo "WARNING: keytool not found in PATH. Skipping JKS truststore creation."
  echo "You can create it manually with keytool using ${CA_CRT}."
fi

echo
echo "Done."
echo "  CA & truststore:  ${CA_DIR}"
echo "  Auth-service:     ${AUTH_DIR}"
echo "  Gateway:          ${GATEWAY_DIR}"
echo
echo "Keystore password:    ${KEYSTORE_PASSWORD}"
echo "Truststore password:  ${TRUSTSTORE_PASSWORD}"
echo
echo "Remember: this is DEV ONLY. Do not use these artifacts in production."