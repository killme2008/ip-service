#!bin/bash
java=`which java`
jvm_opts="-Xmx512m -server"
$java $jvm_opts -Dip-service.db=$(dirname $0)/../resources/17monipdb.dat -jar $(dirname $0)/../target/ip-service-0.1.0-SNAPSHOT-standalone.jar $@
