# Multi-stage secure build for Telefam Ktor Backend
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /workspace
COPY . .
RUN gradle installDist --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for enterprise container security compliance
RUN addgroup -S telefam && adduser -S telefam -G telefam && \
    mkdir -p /var/telefam/sandbox/quarantine /var/telefam/storage/verified_avatars && \
    chown -R telefam:telefam /app /var/telefam

COPY --from=builder /workspace/build/install/telefam-backend /app

USER telefam
EXPOSE 8080
ENV PORT=8080 \
    SERVER_HOST=0.0.0.0 \
    SERVER_PORT=8080 \
    SANDBOX_WORK_DIR=/var/telefam/sandbox/quarantine \
    SECURE_STORAGE_DIR=/var/telefam/storage/verified_avatars \
    SUPABASE_URL=https://whiviqdxrjmvoyjxvxte.supabase.co \
    SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndoaXZpcWR4cmptdm95anh2eHRlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIwNjQzNzgsImV4cCI6MjEwNzY0MDM3OH0.1uP7e8gBS__zU6jzy4fbRmZN-mkcl7v7iXQb4k3Uo1Y

ENTRYPOINT ["/app/bin/telefam-backend"]
