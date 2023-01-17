package com.dfssi.dataplatform.datasync.plugin.source.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xnki on 2017/11/24.
 */
public class SocketServerDemo {
    private ServerSocket serverSocket;
    public SocketServerDemo(){
        try{
            //设置服务器的监听端口威8080
            serverSocket = new ServerSocket(8080);

        }catch (IOException e){
            e.printStackTrace();
        }

        //创建新的监听主线程，创建ServerSocket监听
//        new Thread(new Runnable() {
//            public void run() {
                while(true){
                    Socket socket = null;
                    try{
                        socket = serverSocket.accept();
                        System.out.println("SocketServerDemo.run");
                        //当监听到客户端连接厚，创建新线程传输数据
                        new Thread(new SocketHandler(socket)).start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
//            }
//        }).start();
    }

    class SocketHandler implements Runnable{
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        SocketHandler(Socket socket){
            try{
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"GB2312"));
                writer = new PrintWriter(socket.getOutputStream(),true);
                writer.println("-------welcome-------");
                writer.println("-------welcome-------");
                writer.println("-------welcome-------");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        //覆盖实现接口Runnable里的run()
        @Override
        public void run(){
            try{
                //读取数据，这里只能读一行string
                String line = reader.readLine();

                while (line!=null){
                    System.out.println(line);
                    line = reader.readLine();
                }


            }catch (IOException e){
                e.printStackTrace();
            }finally {
                //最后关闭socket
                try{
                    if (socket != null)socket.close();
                    if (reader != null)reader.close();
                    if (writer != null)writer.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void  main(String[] args){
        new SocketServerDemo();
    }
}
