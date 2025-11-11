# Sử dụng base image có Java 17 (như đã khai báo trong pom.xml)
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Arg để Maven build file JAR từ module order-container
ARG JAR_FILE=order_container/target/order-container-0.0.1-SNAPSHOT.jar

# Copy file JAR đã được build (từ Bước 1) vào trong container
COPY ${JAR_FILE} app.jar

# Mở port 8081 (như trong application.yml của bạn)
EXPOSE 8081

# Lệnh để chạy ứng dụng khi container khởi động
# Cập nhật: Trỏ đến cấu hình bên trong container
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]