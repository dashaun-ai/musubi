# Musubi

Kansei's Cloud Foundry Open Service Broker. It exposes distinct plans and credentials for PostgreSQL,
RabbitMQ, Redis, Grafana LGTM, and Garage buckets. Extracted with history preserved.

Build with JDK 25 using `./mvnw verify`. Rollback re-registers the previous broker release and catalog;
existing service instances and credentials remain authoritative and are never regenerated as part of
an application rollback.
