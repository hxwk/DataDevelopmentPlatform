package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.constants.FTPServerInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanConstants.FTP2SIMID;

/**
 * @author KangJian
 * @create 2018-10-14 10:14
 */
public class DbcFileHandle {
    final static Logger logger = LoggerFactory.getLogger(DbcFileHandle.class);
    //private static ConcurrentHashMap<String, String> vid2fileid = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, String> sim2fileid = new ConcurrentHashMap();
    /**
     * 通过vid获取映射关系,获取fileId
     * read String from redis by vid
     * note: 可能会参考 SaveLatestGpsVo2Redis 序列化value
     * String: vid@dbcFileName@dbcFastDFSFileId
     * @param vid
     * @return vid@dbcFileName@dbcFastDFSFileId
     */
    /*public static String readObjectFromRedis(String vid){
        String value = null;
        if (null != vid) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolManager.getJedis();
                logger.debug("获取redis状态 jedis = " + jedis);
                //FASTID:VID:7c4c631ef0bc444684d4d406be17668e
                value = jedis.get(VID2FASTDFSFID + vid);
                vid2fileid.put(vid, value);
                logger.debug("将获取的vid：fastFileId映射放入内存, value = " + value);
            } catch (Exception e) {
                value = vid2fileid.get(vid);
                logger.debug("从内存在获取fastFileId = " + value, e);
            } finally {
                if (null != jedis) {
                    RedisPoolManager.returnResource(jedis);
                }
            }
        }
        return value;
    }*/

    /**
     * 通过sim获取映射关系,获取dbcName
     * read String from redis by sim
     * note: 可能会参考 SaveLatestGpsVo2Redis 序列化value
     * String: sim@dbcFileName@remotePath
     *
     * @param sim
     * @return 1234567890@spf.dbc@/var/ftp/pub/ndypt/application/roadVehicle/upload/spf.dbc
     */
    public static String readValueFromRedis(String sim) {
        String value = null;
        if (StringUtils.isNotEmpty(sim)) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolManager.getJedis();
                logger.debug("获取redis状态 jedis = ." + jedis);
                //FTPID:SIM:1234567890
                value = jedis.get(FTP2SIMID + sim);
                sim2fileid.put(sim, value);
                logger.debug("将获取的sim：dbcName映射放入内存, value = " + value);
            } catch (Exception e) {
                value = sim2fileid.get(sim);
                logger.debug("从内存在获取fastFileId = " + value, e);
            } finally {
                if (null != jedis) {
                    RedisPoolManager.returnResource(jedis);
                }
            }
        }
        return value;
    }

    /**
     * 将dbc文件下载到 user.home 目录下,通过dbcFastDFSFileId
     * @param dbcFastDFSFileId
     */
    /*public static void downloadDBCFile(String dbcFastDFSFileId){
        try {
            FastDFSHandler.download2LocalWithFileId(dbcFastDFSFileId);
        }catch (Exception ex){
            logger.error("CanInformationPH download DBC File error:{}",ex.getMessage());
        }
    }*/

    /**
     * 将dbc文件从FTP服务器下载到 user.home /dbcfiles目录下, 通过dbc文件名
     *
     * @param dbcName
     */
    public static void downloadDBCFilebyFtp(String dbcName, String remotePath) {
        if (StringUtils.isNotEmpty(dbcName)) {
            FTPServerInfo.fileName = dbcName;
        }
        if (StringUtils.isNotEmpty(remotePath)) {
            FTPServerInfo.remotePath = remotePath;
        }
        try {
            boolean flag = FtpApache.downFile(FTPServerInfo.url, FTPServerInfo.port, FTPServerInfo.userName, FTPServerInfo.password,
                    FTPServerInfo.remotePath, FTPServerInfo.fileName, FTPServerInfo.localPath);
            logger.info("将dbc文件从FTP服务器下载到:{}目录下,文件名:{}.",FTPServerInfo.localPath,FTPServerInfo.fileName);
            if (flag) {
                logger.info("dbc文件从url:{},port:{},remotePath:{},fileName:{}下载成功！", FTPServerInfo.url
                        , FTPServerInfo.port, FTPServerInfo.remotePath, FTPServerInfo.fileName);
            } else {
                logger.warn("download DBC File by Ftp error!");
            }
        } catch (Exception ex) {
            logger.error("CanInformationPH download DBC File error:{}", ex.getMessage());
        }
    }
}
