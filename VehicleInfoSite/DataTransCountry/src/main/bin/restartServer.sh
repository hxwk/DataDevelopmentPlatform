#! /bin/sh
cd `dirname $0`
PWD=`pwd`

a=$JAVA_HOME

a_new=""

if [[ $a == *bin* ]]
then
    echo "contain bin"
echo ${a%/bin*}
a_new=${a%/bin*}
else
    echo "not contain bin"
	a_new=$a
fi

for i in ../lib/*.jar;
do CLASSPATH="$CLASSPATH":$PWD/$i;
done

echo "CLASSPATH="$CLASSPATH


server_name="NewEnergyServer"
server_name_cn="新能源模拟国家平台服务端"


#查看进程是否存在
RESULT=$(ps -ef | grep ${server_name} | grep -v "grep")
processId=$(ps -ef|grep ${server_name}|grep -v "grep"|awk '{print $2}')


#判断RESULT是否不为空，不为空则说明进程已经启动
if [ -n "$RESULT" ]; then
    echo [$server_name_cn]正在运行
    kill -9 $processId
    sleep 1
    echo [$server_name_cn]停止成功
fi
#-Dplatform.log.dir=../../logs \
#-Dlogfile.name=server.log \
(nohup ${a_new}/bin/java -server \
	           -Dname=${server_name} \
                   -Dlog4j.configurationFile=../config/log4j2-server.xml \
                   -Dservice.id=NewEnergyServer \
                   -Dservice.type=server \
                   -Xmx1024m \
                   -Xms1024m \
                   -Xmn512m \
                   -classpath .:config:$CLASSPATH com.dfssi.dataplatform.server.NettyServer \
                   -conf ../config/dataTrans.properties > ../logs/nohup 2>&1 &)
echo [$server_name_cn]正在运行
