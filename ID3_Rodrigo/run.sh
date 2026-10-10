#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p bin
if command -v javac >/dev/null 2>&1; then
  javac --release 17 -encoding UTF-8 -d bin src/id3/*.java test/id3/*.java
elif [ -n "${ID3_ECJ_JAR:-}" ] && [ -f "$ID3_ECJ_JAR" ]; then
  java -jar "$ID3_ECJ_JAR" -17 -encoding UTF-8 -d bin src/id3/*.java test/id3/*.java
else
  echo 'Se necesita un JDK 17 o superior (javac), o ID3_ECJ_JAR con el compilador Eclipse.' >&2
  exit 1
fi
if [ "${1:-}" = '--test' ]; then
  shift
  java -Dfile.encoding=UTF-8 -cp bin id3.Id3Test "$@"
else
  java -Dfile.encoding=UTF-8 -cp bin id3.Main "$@"
fi
