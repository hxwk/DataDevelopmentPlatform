#!/bin/bash

if [ $# != 1 ]
then
    echo "ERROR:Please input parameter!"
	echo "e.g.:$0 start or stop"
	exit 1
fi

if [ $1 != "start" ] && [ $1 != "stop" ]
then
    echo "ERROR:$1 is invalid!Please input parameter!"
	echo "e.g.:$0 start or stop"
	exit 1
fi

APP_HOME=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
APP_OUT=logs/console.out
# start command
if [ "$1" == "start" ]
then
    if [ -f "pid" ]
    then
        PID=$(cat pid)
        if ps -p $PID > /dev/null
        then
          echo "Workflow is running, stop it first"
          exit 1
        fi
    fi
     
    mkdir -p logs    
    touch "${APP_OUT}"    
  
    APP_CP=${APP_HOME}/lib/*
    nohup java -cp .:${APP_CP} -Xms256m -Xmx512m com.dfssi.dataplatform.workflow.app.WorkflowApp >> "${APP_OUT}" 2>&1 < /dev/null &
 		pid=$!
 		if [[ -z "${pid}" ]]; then 		
 			echo "Fail to start Workflow instance."
 			return 1;
 		else 		  
      echo ${pid} > pid
    fi
   
    echo "A new Workflow instance is started by $USER, stop it using \"workflow.sh stop\""
    echo "Please visit http://<ip>:8024/"
    echo "You can check the log at logs/*.log"
    exit 0

# stop command
elif [ "$1" == "stop" ]
then
    if [ -f "pid" ]
    then
        PID=$(cat pid)
        if ps -p $PID > /dev/null
        then
           echo "stopping Workflow:$PID"
           kill $PID
           rm -rf pid
           exit 0
        else
           echo "Workflow is not running, please check"
           exit 1
        fi
        
    else
        echo "Workflow is not running, please check"
        exit 1    
    fi
fi