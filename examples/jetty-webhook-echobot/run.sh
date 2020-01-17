#!/usr/bin/env bash
echo "Starting bot…"
exec mvn -q exec:java -Dexec.args=\"$@"