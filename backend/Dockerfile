# 1. Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
# Coppy mã nguồn từ thư mục src
COPY pom.xml .
COPY src ./src
# Build ứng dụng thành file jar (bỏ qua quá trình test cho nhanh)
RUN mvn clean package -DskipTests

# 2. Run stage (Chỉ lấy bản build, nhẹ hơn rất nhiều)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Sao chép file jar đã build từ container trước sang container này
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
# Lệnh chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]
