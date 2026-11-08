#!/bin/sh
# Launcher compatível com Linux/macOS. Primeiro uso obtém e verifica o wrapper oficial.
set -eu
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  URL="https://services.gradle.org/distributions/gradle-8.14-wrapper.jar"
  echo "Obtendo Gradle Wrapper 8.14 oficial..."
  if command -v curl >/dev/null 2>&1; then
    curl -fLsS "$URL" -o "$JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$JAR"
  else
    echo 'Instale curl ou wget para baixar o Gradle Wrapper.' >&2
    exit 1
  fi
  EXPECTED='7d3a4ac4de1c32b59bc6a4eb8ecb8e612ccd0cf1ae1e99f66902da64df296172'
  if command -v sha256sum >/dev/null 2>&1; then
    ACTUAL=$(sha256sum "$JAR" | cut -d' ' -f1)
  elif command -v shasum >/dev/null 2>&1; then
    ACTUAL=$(shasum -a 256 "$JAR" | cut -d' ' -f1)
  else
    echo 'SHA-256 indisponivel; nao executaremos o wrapper nao verificado.' >&2
    rm -f "$JAR"
    exit 1
  fi
  if [ "$ACTUAL" != "$EXPECTED" ]; then
    echo 'Falha na verificacao SHA-256 do wrapper.' >&2
    rm -f "$JAR"
    exit 1
  fi
fi
if [ -n "${JAVA_HOME:-}" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="java"
fi
exec "$JAVACMD" -Xmx64m -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
