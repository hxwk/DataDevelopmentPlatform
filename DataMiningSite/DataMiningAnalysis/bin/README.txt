打包步骤：
1. 建议项目的绝对路径中不包含中文，否则可能会因为中文乱码而报找不到类文件的错误
2. 建议打包过程中跳过测试，因为部分测试仅针对开发环境，其他环境不一定能通过。
3  先打包顶级项目DataDevelopmentPlatform, 若成功则可跳过 4，5，6
4. 由于本项目依赖了DataCommonSite下的一些共用模块，因此需打包DataCommonSite
5. 由于本项目属于DataMiningSite的子项目，最好直接打包DataMiningSite。
6. 若执行步骤四因其他子模块的错误而失败，则尝试直接打包本项目

部署步骤：

  一、开发/生产环境
     1. 创建fuel目录，并进入
        mkdir /usr/fuel
        cd /usr/fuel
     2. 上传DataMiningAnalysis-0.1-SNAPSHOT.tar.gz 到目录 /usr/fuel下
     3. 解压DataMiningAnalysis-0.1-SNAPSHOT.tar.gz
       tar -zxf DataMiningAnalysis-0.1-SNAPSHOT.tar.gz
     4. 进入DataMiningAnalysis-0.1-SNAPSHOT
        cd DataMiningAnalysis-0.1-SNAPSHOT/target
     5. 获取DataMiningAnalysis-0.1-SNAPSHOT.jar并修改配置
         DataMiningAnalysis-0.1-SNAPSHOT.jar\fuel\fuel.properties
         修改内容有下：
          greenplum连接相关，修改成集群中对应的 ip、端口、数据库、连接用户名和密码：
             fuel.out.database.url
             fuel.out.database.user
             fuel.out.database.password
          驾驶异常行为告警报告地址，没有可不填
          fuel.out.database.abnormaldriving.report.url=
     6. 修改完成后，覆盖原来的DataMiningAnalysis-0.1-SNAPSHOT.jar
     7. 进入bin目录
        cd ../bin
     8. 修改0200数据入ElasticSearch 的任务命令
        vim start_0200ToEs.sh
        修改参数有：
            --topics
            --brokerList
            --esNodes
            --esClusterName
            --interval




