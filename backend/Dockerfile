# Sử dụng OpenJDK 17 làm base image
FROM eclipse-temurin:17-jdk AS builder

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build file JAR bằng Maven
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Tạo stage chạy ứng dụng
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file JAR từ stage build
COPY --from=builder /app/target/apartment-managing-0.0.1-SNAPSHOT.jar app.jar

# Cấu hình cổng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "app.jar"]
