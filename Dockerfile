# ============================================================
# [编写顺序 Docker-1] Dockerfile — 把 Spring Boot 应用打包成镜像
# [思维] Docker 镜像 = 一个轻量级的独立运行环境
#        包含 JDK + 你的 jar 包，不依赖宿主机的 Java 环境
# ============================================================

# 第一阶段：构建（用 Maven 编译打包）
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
# 先下载依赖（利用 Docker 缓存层，代码没变就不重新下载）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

# 第二阶段：运行（只放 JRE + jar 包，镜像更小）
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
