#!/bin/sh
exec java ${JAVA_OPTS:= } -cp app:app/lib/* ru.yakovlev.Application "${@}"