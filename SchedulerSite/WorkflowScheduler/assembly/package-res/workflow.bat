@echo off
SetLocal EnableDelayedExpansion    
@SET WD=%~d0%~p0
@SET APP_HOME=%WD%
@echo Application Root Path:%APP_HOME%
SET APP_CP=%APP_HOME%/lib/*
set JVM_OPTS= -Xms256m -Xmx512m
java -cp %APP_CP% %JVM_OPTS% com.dfssi.dataplatform.workflow.app.WorkflowApp
@pause