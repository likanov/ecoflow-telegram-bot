#!/bin/bash
dt=$(date '+%d_%m_%Y_%H_%M_%S')
logname="ecoflow-bot.log"
move_old=`mv ${logname} "ecoflow-bot_"$dt".log"`
echo ""
echo "###############################"
echo " ecoflow bot Run"
echo "###############################"

JAVA_OPTS="-Xms1G -Xmx1G -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./ecoflow-bot_gc.log"

java ${JAVA_OPTS} \
    -DchartId=-779351830 \
    -jar /home/antonv_likanov/ecoflow-telegram-bot/target/ecoflow-bot-0.0.1-SNAPSHOT.jar >> ${logname} 2>&1