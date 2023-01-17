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

for i in ../../lib/*.jar;
do CLASSPATH="$CLASSPATH":$PWD/$i;
done

echo "CLASSPATH="$CLASSPATH

(${a_new}/bin/java -server \
                   -Dlog4j.configurationFile=../../config/log4j2-deploy.xml \
                   -Dplatform.log.dir=../../logs \
                   -Dlogfile.name=client.log \
                   -Dservice.id=client_1 \
                   -Dservice.type=client \
                   -Xmx1024m \
                   -Xms1024m \
                   -Xmn512m \
                   -classpath .:config:$CLASSPATH com.dfssi.dataplatform.datasync.service.client.ClientMainClass \
                   -conf ../../config/client.properties &)