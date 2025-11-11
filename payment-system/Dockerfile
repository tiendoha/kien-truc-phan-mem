# Stage 1: Build
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# 1. Copy file POM cha (payment-system/pom.xml)
COPY pom.xml .

# 2. Copy 4 module con
COPY payment_container ./payment_container
COPY payment_domain ./payment_domain
COPY payment_dataaccess ./payment_dataaccess
COPY payment_messaging ./payment_messaging

# 3. Chạy Maven package từ thư mục gốc /app
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17-jdk-slim
WORKDIR /app

# 4. Copy file JAR (ĐÃ SỬA LỖI GẠCH NGANG/GẠCH DƯỚI)
COPY --from=build /app/payment_container/target/payment_container-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

# 5. Quan trọng: Thêm profile "docker"
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]