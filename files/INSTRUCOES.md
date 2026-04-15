# Kanoas — Instruções de Aplicação dos Fixes

## O que foi corrigido

### 1. `Dockerfile` → **CRÍTICO**
- **Problema**: Usava `commandlinetools-linux-latest.zip` (URL dinâmica — quebra builds futuros)
- **Fix**: Versão pinada `11076708` — build 100% reproduzível
- **Problema**: `/project` não tinha `chown` para o user `builder` → erro de permissão ao montar o volume
- **Fix**: `RUN chown builder:builder /project` adicionado antes de `USER builder`
- **Melhoria**: `GRADLE_OPTS` configurado para desativar o daemon (correto para CI/Docker)

### 2. `docker-compose.yml` → **CRÍTICO**
- **Problema**: Sem `user: builder` → container rodava como root
- **Problema**: Sem `security_opt: no-new-privileges` → processo podia escalar privilégios
- **Problema**: Sem `chmod +x gradlew` → `./gradlew` falha no Linux se veio do Windows
- **Fix**: Todos os serviços agora têm `user`, `security_opt`, e `chmod +x` automático
- **Melhoria**: `android-shell` adicionado para troubleshooting interativo
- **Nota**: `network_mode: none` está comentado — ative após o primeiro build (que precisa de rede para baixar deps)

### 3. `composeApp/src/androidMain/AndroidManifest.xml` → **BUILD BREAKER**
- **Problema**: Referenciava `@mipmap/ic_launcher` e `@mipmap/ic_launcher_round` que NÃO existem no projeto
- **Fix**: Atributos `android:icon` e `android:roundIcon` removidos — o app builda normalmente
- **Próximo passo**: Use o Android Studio (Tools → Image Asset) para gerar os ícones e restaurar os atributos

### 4. `shared/build.gradle.kts` → **DEPRECATION WARNING**
- **Problema**: `kotlinOptions { jvmTarget = "17" }` é deprecated no Kotlin 2.x
- **Fix**: Migrado para `compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }` (igual ao composeApp)

### 5. `HomeScreen.kt` → **LINT WARNING**
- **Problema**: `import androidx.compose.material3.Icon` presente mas `Icon` nunca usado
- **Fix**: Import removido (lint emitia `UnusedImport`)

---

## Como aplicar

Copie cada arquivo para o caminho correto dentro do seu projeto:

```
Dockerfile               → Kanoas/Dockerfile
docker-compose.yml       → Kanoas/docker-compose.yml
AndroidManifest.xml      → Kanoas/composeApp/src/androidMain/AndroidManifest.xml
shared-build.gradle.kts  → Kanoas/shared/build.gradle.kts
HomeScreen.kt            → Kanoas/composeApp/src/commonMain/kotlin/br/com/kanoas/presentation/home/HomeScreen.kt
```

---

## Como fazer o build

```bash
# 1. Construir a imagem (só na primeira vez ou quando o Dockerfile mudar)
docker compose build

# 2. Build do APK (precisa de rede para baixar dependências Gradle)
docker compose run --rm android-build

# 3. Após o primeiro build (deps já em cache), ative o isolamento de rede:
#    Descomente a linha `network_mode: "none"` no docker-compose.yml
#    e execute novamente — o build deve funcionar SEM internet.

# 4. Testes unitários
docker compose run --rm android-test

# 5. Lint
docker compose run --rm android-lint
```

---

## APK gerado

Após o build, o APK estará em:
```
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```
