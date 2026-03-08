#!/bin/bash
# Wait for ZooKeeper to be ready
until zkCli.sh -server zookeeper:2181 ls / > /dev/null 2>&1; do
  echo "Waiting for ZooKeeper..."
  sleep 2
done
echo "ZooKeeper is ready. Initializing config..."

# Batch all create commands in a single zkCli.sh session
zkCli.sh -server zookeeper:2181 <<'EOF'

create /config ""
create /config/application ""
create /config/application/spring.datasource.username "bankuser"
create /config/application/spring.datasource.password "bankpass"

create /config/accounts-service ""
create /config/accounts-service/spring.datasource.url "jdbc:postgresql://postgres:5432/bankdb?currentSchema=accounts"
create /config/accounts-service/spring.security.oauth2.resourceserver.jwt.jwk-set-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/certs"

create /config/notification-service ""
create /config/notification-service/spring.datasource.url "jdbc:postgresql://postgres:5432/bankdb?currentSchema=notifications"
create /config/notification-service/spring.security.oauth2.resourceserver.jwt.jwk-set-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/certs"

create /config/cash-service ""
create /config/cash-service/spring.security.oauth2.resourceserver.jwt.jwk-set-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/certs"
create /config/cash-service/spring.security.oauth2.client.registration.cash-service-client.client-secret "cash-secret"
create /config/cash-service/spring.security.oauth2.client.provider.cash-service-client.token-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/token"

create /config/transfer-service ""
create /config/transfer-service/spring.security.oauth2.resourceserver.jwt.jwk-set-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/certs"
create /config/transfer-service/spring.security.oauth2.client.registration.transfer-service-client.client-secret "transfer-secret"
create /config/transfer-service/spring.security.oauth2.client.provider.transfer-service-client.token-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/token"

create /config/my-bank-front-app ""
create /config/my-bank-front-app/spring.security.oauth2.client.registration.keycloak.client-secret "front-ui-secret"
create /config/my-bank-front-app/spring.security.oauth2.client.provider.keycloak.token-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/token"
create /config/my-bank-front-app/spring.security.oauth2.client.provider.keycloak.jwk-set-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/certs"
create /config/my-bank-front-app/spring.security.oauth2.client.provider.keycloak.user-info-uri "http://keycloak:8180/realms/my-bank/protocol/openid-connect/userinfo"
create /config/my-bank-front-app/gateway.url "http://gateway-service:8081"

EOF

echo "ZooKeeper config initialized."
