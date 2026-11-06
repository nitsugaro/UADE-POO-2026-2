#!/bin/sh
# Ejecutar con: sh ejecutar-sqlite.sh [nombre dni]
set -eu
cd "$(dirname "$0")"
mkdir -p out
find src test -name '*.java' -print > out/fuentes.txt
javac -encoding UTF-8 -cp 'lib/*' -d out @out/fuentes.txt
java --enable-native-access=ALL-UNNAMED -cp 'out:lib/*' aplicacion.EjemploSQLite "$@"
