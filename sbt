#!/bin/bash

sbtver=0.13.15
sbtjar=sbt-launch.jar
sbtsha128=61bfa3f5791325235f6d7cc37a7e7f6bfeb83531

sbtrepo="http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/$sbtver/$sbtjar"

if [ ! -f $sbtjar ]; then
  echo "downloading $PWD/$sbtjar from $sbtrepo" 1>&2
  if ! curl --location --silent --fail --remote-name $sbtrepo > $sbtjar; then
    exit 1
  fi
fi

checksum=`openssl dgst -sha1 $sbtjar | awk '{ print $2 }'`
if [ "$checksum" != $sbtsha128 ]; then
  echo "[error] Bad $PWD/$sbtjar. Delete $PWD/$sbtjar and run $0 again."
  exit 1
fi

javaVersion=`java -version 2>&1 | grep "java version" | awk '{print $3}' | tr -d \"`

[ -f ~/.sbtconfig ] && . ~/.sbtconfig

java -ea                          \
  $SBT_OPTS                       \
  $JAVA_OPTS                      \
  -XX:+AggressiveOpts             \
  -XX:+UseParNewGC                \
  -XX:+UseConcMarkSweepGC         \
  -XX:+CMSParallelRemarkEnabled   \
  -XX:+CMSClassUnloadingEnabled   \
  -XX:ReservedCodeCacheSize=128m  \
  -XX:SurvivorRatio=128           \
  -XX:MaxTenuringThreshold=0      \
  -XX:-EliminateAutoBox           \
  -Xms512M                        \
  -Xmx1280M                       \
  -server                         \
  -jar $sbtjar "$@"
