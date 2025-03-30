# Sử dụng OpenJDK 17 làm base image để build
FROM eclipse-temurin:17-jdk AS builder

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Cấp quyền và build file JAR bằng Maven
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Tạo stage chạy ứng dụng, sử dụng JRE thay vì JDK để giảm kích thước image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy file JAR từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Cấu hình cổng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "app.jar"]
