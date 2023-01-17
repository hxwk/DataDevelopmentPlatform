package com.dfssi.dataplatform.datasync.plugin.source.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by xnki on 2017/12/1.
 */
public class SocketClientDemo2 implements Runnable {
    private Socket socket;
    BufferedReader reader;
    private PrintWriter writer;
    public SocketClientDemo2(){
        try{
            //127.0.0.1表示本机IP，8080为服务器socket设置的端口
            socket = new Socket("127.0.0.1", 8080);
            try{
                reader  = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GB2312"));
                writer = new PrintWriter(socket.getOutputStream(),true);
                while(true){

                    writer.println("7E800100050159942557343A826102000200D77E");
                    writer.flush();
                    sleep(1000);
                    System.out.println("SocketClientDemo.SocketClientDemo");
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void run(){
        try{
            //这里就可以读取所有行string
            String line, buffer = "";
            while(!((line = reader.readLine()) == null ))
                buffer += line ;
            System.out.println(buffer);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("problem");
        }finally{
            //最后关闭socket
            try{
                if (socket != null )socket.close();
                if (reader  != null)reader.close();
                if (writer != null)writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        new Thread(new SocketClientDemo()).start();
    }
}
