# RuoYi 镜像构建 Dockerfile
# 用途：在构建阶段从 CODE_URL 下载项目 ZIP 并使用 Maven 打包 ruoyi-admin
# 使用方法（可覆盖 CODE_URL）:
#   构建镜像：
#     docker build --build-arg CODE_URL=<zip-url> -t ruoyi-admin:tag .
#   运行容器：
#     docker run -d -p 80:80 --name ruoyi-admin ruoyi-admin:tag

# ---------------------------
# 单阶段开发镜像（保留源码，包含开发工具）
# ---------------------------
FROM eclipse-temurin:8-jdk

# 代码包下载地址（可在构建时通过 --build-arg 覆盖）
ARG CODE_URL=https://deep-evaluation-set.tos-cn-beijing.volces.com/RuoYi-6b9b545.zip

# 安装系统依赖和开发工具
RUN apt-get update && apt-get install --no-install-recommends -y \
    maven \
    wget \
    unzip \
    vim \
    less \
    htop \
    procps \
    net-tools \
    ca-certificates \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 创建并切换到工作目录
WORKDIR /workspace

# 下载并解压代码到 /workspace（保留源码）
RUN set -eux; \
    wget -O /tmp/code.zip "${CODE_URL}"; \
    mkdir -p /tmp/extract; \
    unzip /tmp/code.zip -d /tmp/extract; \
    # 若解压后只有一个顶层目录，则将其内容移入 /workspace；否则移动所有条目
    EXTRACT_COUNT=$(ls -1 /tmp/extract | wc -l); \
    if [ "${EXTRACT_COUNT}" -eq 1 ] && [ -d "/tmp/extract/$(ls -1 /tmp/extract | head -n1)" ]; then \
       TOPDIR=$(ls -1 /tmp/extract | head -n1); \
       mv /tmp/extract/${TOPDIR}/* /workspace/ || true; \
       rm -rf /tmp/extract/${TOPDIR}; \
    else \
       mv /tmp/extract/* /workspace/ || true; \
    fi; \
    rm -rf /tmp/extract /tmp/code.zip

# 切换到源码目录
WORKDIR /workspace

# 预下载 Maven 依赖（可选，失败不影响）
RUN mvn -f ruoyi-admin/pom.xml dependency:go-offline || true

# 不设置 ENTRYPOINT/EXPOSE/生产 JVM 参数，保持开发环境灵活性
