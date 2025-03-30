# Sử dụng OpenJDK 17 làm base image
FROM eclipse-temurin:17-jdk

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file JAR vào container
COPY target/apartment-managing-0.0.1-SNAPSHOT.jar app.jar

# Cấu hình cổng chạy ứng dụng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "app.jar"]
