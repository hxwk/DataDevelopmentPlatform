package com.dfssi.dataplatform.datasync.plugin.source.tcp;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * simulation car machine terminal
 * Created by xnki on 2017/11/24.
 */
public class SocketClientDemo implements Runnable {
    static final Logger logger= LoggerFactory.getLogger(SocketClientDemo.class);
    private Socket socket;
    BufferedReader reader;
    private PrintWriter writer;

    public SocketClientDemo() {
        try {
            //127.0.0.1表示本机IP，8080为服务器socket设置的端口
            socket = new Socket("127.0.0.1", 10001);
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gbk"));
                writer = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
/*
* # 测试数据

## 心跳包

```shell
7e000200000200000000150003327e
7e # 标识位
000200000200000000150003 # 消息头
    0002 # 消息ID
    0000 # 消息体属性，消息体属性每个位都为零,也即第12-15位的消息包封装项不存在,消息体也为空
    020000000015 # 终端手机号
    0003 # 流水号
32 # 校验码
7e # 标识位
```

## 鉴权包

```shell
7e010200060200000000150026313639333434397e

7e # 标识位
010200060200000000150026 # 消息头
    0102 # 消息ID
    0006 # 消息体属性
        0x0006=0b(0000,0000,0000,0110)
        消息体长度[0-9]==0b(00,0000,0110)==6字节
        数据加密方式[10-12]==0b(000)
        分包[13]==0,也即第12-15位的消息包封装项不存在
        保留位[14-15]==00
    020000000015 # 终端手机号
    0026 # 流水号
313639333434 # 消息体 6个字节
39 # 校验码
7e # 标识位
```

## 注册包

```shell
7e0100002c0200000000150025002c0133373039363054372d54383038000000000000000000000000003033323931373001d4c142383838387b7e

7e # 标识位
010200060200000000150026 # 消息头
    0100 # 消息ID
    002c # 消息体属性
        0x002c=0b(0000,0000,0010,1100)
        消息体长度[0-9]==0b(00,0010,1100)==44字节
        数据加密方式[10-12]==0b(000)
        分包[13]==0,也即第12-15位的消息包封装项不存在
        保留位[14-15]==00
    020000000015 # 终端手机号
    0025 # 流水号
# 消息体 44个字节
002c0133373039363054372d54383038000000000000000000000000003033323931373001d4c14238383838
	002c #省域 ID
	0133 #市县域 ID
	3730393630 #制造商 ID
	54372d5438303800000000000000000000000000 #终端型号
	30333239313730 #终端 ID
	……………………
	……………………
7b # 校验码
7e # 标识位
* */
             List<String> protocalList = Lists.newArrayList();
             protocalList.add("7E 00 02 00 00 01 45 71 16 49 53 28 4D 5E 7E");//心跳包
             protocalList.add("7E 01 02 00 08 01 45 71 16 49 53 00 05 38 33 79 32 47 67 6E 5A 63 7E");//鉴权包
             protocalList.add("7E 01 00 00 2D 01 31 47 19 81 00 14 6D 00 2A 00 64 37 30 33 31 35 53 53 49 42 44 53 2D 32 44 30 31 00 00 00 00 00 00 00 00 00 45 34 30 36 36 37 30 02 B6 F5 41 4C 47 33 30 30 FC 7E");//注册包
             protocalList.add("7E 02 00 00 44 01 59 07 15 71 13 06 7B 00 00 00 00 00 0C 00 02 88 73 1E F0 8D DA 05 10 00 0B 09 10 18 F0 17 12 13 16 40 35 01 04 00 40 00 02 02 02 00 00 03 02 00 00 04 02 00 00 25 04 00 00 00 00 2B 04 00 00 00 00 30 01 16 31 01 00 E1 02 01 17 69 7E "); //0200位置上报
             protocalList.add("7E 07 04 01 61 01 37 20 01 31 74 15 10 00 05 01 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 16 00 00 00 00 17 08 13 07 20 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 15 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 21 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 16 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 12 00 00 00 00 17 08 13 07 22 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 16 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 24 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 17 31 01 14 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 23 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C E0 30 01 15 31 01 13 E1 02 00 FE DA 7E");//终端批量位置上传
             //can报文上传
             protocalList.add("7E 07 05 23 C0 01 59 07 15 71 13 02 81 00 03 00 02 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 0C F0 04 00 00 7D 01 7D 01 00 00 21 F0 7D 01 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE BF 0B 00 00 7D 01 7D 01 7D 01 7D 01 FF FF 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 18 FE F1 31 F7 00 00 00 00 00 00 00 0C FF 03 31 C6 3F A0 1F C7 03 FF 22 0C FF 03 31 C6 3F A0 1F C7 03 FF 33 0C FF 03 31 C6 3F A0 1F C7 03 FF 54 0C FF 03 31 C6 3F A0 1F C7 03 FF 65 0C FF 03 31 C6 3F A0 1F C7 03 FF 76 0C FF 03 31 C6 3F A0 1F C7 03 FF 07 0C FF 03 31 C6 3F A0 1F C7 03 FF 00 0C FF 03 31 C6 3F A0 1F C7 03 FF 11 0C FF 03 31 C6 3F A0 1F C7 03 FF 22 0C FF 03 31 C6 3F A0 1F C7 03 FF 33 0C FF 03 31 C6 3F A0 1F C7 03 FF 54 0C FF 03 31 C6 3F A0 1F C7 03 FF 65 0C FF 03 31 C6 3F A0 1F C7 03 FF 76 0C FF 03 31 C6 3F A0 1F 00 20 30 BD CB 7E");
             protocalList.add("7E 01 00 00 2D 01 34 76 86 05 50 00 52 00 2A 00 00 37 30 33 31 35 53 53 49 42 44 53 2D 32 44 30 31 00 00 00 00 00 00 00 00 00 45 34 31 33 33 32 32 02 B6 F5 43 39 32 35 39 37 C0 7E");//测试数据
             for (int i = 0; i < protocalList.size(); i++) {
                 writer.println(protocalList.get(i));
                 logger.info("write idx:{},content:{}",i,protocalList.get(i));
                 writer.flush();
                 sleep(500);
             }
             System.out.println("SocketClientDemo.SocketClientDemo");
             sleep(3600000*24);
         }
     } catch (InterruptedException e) {
         e.printStackTrace();
     }

      } catch (IOException e) {
          e.printStackTrace();
      }
    }

    public void run() {
        try {
            //这里就可以读取所有行string
            String line, buffer = "";
            while (!((line = reader.readLine()) == null)) {
                buffer += line;
                System.out.println("read buf " + buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("problem");
        } finally {
            //最后关闭socket
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new SocketClientDemo()).start();
    }
}
