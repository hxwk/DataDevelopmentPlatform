@echo off
set currentdir=%cd%
cd ../..\lib
echo currentPath=%cd%
setlocal enabledelayedexpansion
for %%c in (%currentPath%\*.jar) do (
set CLASSPATH=!CLASSPATH!;%cd%\%%c;
echo CLASSPATH=%CLASSPATH%
echo current jar is %%c.
)

java -server -Dservice.id=client_1 -Dservice.type=client -Dlog4j.configurationFile=..\..\config\log4j2-deploy.xml -Dplatform.log.dir=..\..\logs -Dlogfile.name=client.log -Xmx1024m -Xms1024m -Xmn512m -classpath .:config:%CLASSPATH% com.dfssi.dataplatform.datasync.service.client.ClientMainClass -conf ..\..\config\client.properties
pause