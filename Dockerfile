ARG GIT_REPO=https://github.com/xdcxiang-cell/RuoYi.git
ARG GIT_COMMIT=67f7f8bf97711d0c0f64346db9dbefd8394c6580
# ---------------------------
# Builder: clone repo and build with Maven (uses OpenJDK 8)
# ---------------------------
FROM eclipse-temurin:8-jdk AS builder
ARG GIT_REPO
ARG GIT_COMMIT
WORKDIR /build

# Install maven and git
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven git ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Clone the repository
RUN git clone ${GIT_REPO} . && \
    git checkout ${GIT_COMMIT}

# Build ruoyi-admin (skip tests)
RUN mvn -T1C -DskipTests package -pl ruoyi-admin -am

# ---------------------------
# Runtime: lightweight OpenJDK image
# ---------------------------
FROM eclipse-temurin:8-jdk
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /build/ruoyi-admin/target/ruoyi-admin.jar app.jar

# Create logs dir
RUN mkdir -p /app/logs

# 暴露端口80
EXPOSE 80

# 设置JVM参数优化内存使用
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
