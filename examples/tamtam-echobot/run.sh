#!/usr/bin/env bash
mvn -q exec:java -Dexec.args="--token $1"