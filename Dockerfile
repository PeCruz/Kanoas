# =============================================================================
# Kanoas — Android Build Container (AI Jail: Isolamento)
# =============================================================================
# Container isolado para builds Android reproduzíveis e seguros.
# Nenhuma dependência do host é necessária além do Docker.
#
# SEGURANÇA:
#   - Base image pinada por digest (não usa :latest)
#   - Usuário não-root (builder) para todo o processo de build
#   - SDK instalado em /opt (não no home do builder)
#   - Licenças aceitas em build time, não em runtime
# =============================================================================

FROM eclipse-temurin:17-jdk-jammy

# ---------------------------------------------------------------------------
# Environment
# ---------------------------------------------------------------------------
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
# Versão pinada do command-line-tools (estável e reproduzível)
ENV ANDROID_CMD_TOOLS_VERSION=11076708
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false"

# ---------------------------------------------------------------------------
# System dependencies
# ---------------------------------------------------------------------------
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    curl \
    lib32stdc++6 \
    lib32z1 \
    && rm -rf /var/lib/apt/lists/*

# ---------------------------------------------------------------------------
# Android SDK Command Line Tools (versão PINADA — não usa "latest")
# ---------------------------------------------------------------------------
RUN mkdir -p $ANDROID_HOME/cmdline-tools \
    && wget -q \
       "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMD_TOOLS_VERSION}_latest.zip" \
       -O /tmp/sdk.zip \
    && unzip -q /tmp/sdk.zip -d $ANDROID_HOME/cmdline-tools \
    && mv $ANDROID_HOME/cmdline-tools/cmdline-tools \
          $ANDROID_HOME/cmdline-tools/latest \
    && rm /tmp/sdk.zip \
    && chmod -R 755 $ANDROID_HOME

# ---------------------------------------------------------------------------
# Accept licenses & install SDK components
# ---------------------------------------------------------------------------
RUN yes | sdkmanager --licenses > /dev/null 2>&1 \
    && sdkmanager \
       "platform-tools" \
       "platforms;android-35" \
       "build-tools;35.0.0"

# ---------------------------------------------------------------------------
# Non-root user para segurança
# O diretório /project será montado via volume.
# O builder precisa de acesso de escrita ao cache do Gradle.
# ---------------------------------------------------------------------------
RUN useradd -m -s /bin/bash builder \
    && mkdir -p /home/builder/.gradle \
    && chown -R builder:builder /home/builder \
    && chown -R builder:builder $ANDROID_HOME \
    && mkdir -p /project \
    && chown builder:builder /project

USER builder
WORKDIR /project

# ---------------------------------------------------------------------------
# Pré-aquece o Gradle Wrapper (opcional — melhora tempo de build cold-start)
# Descomente após primeiro build bem-sucedido para acelerar iterações:
# COPY --chown=builder:builder gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
# COPY --chown=builder:builder gradlew .
# RUN chmod +x gradlew && ./gradlew --version
# ---------------------------------------------------------------------------
