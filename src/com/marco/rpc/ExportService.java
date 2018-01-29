package com.marco.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by maom3 on 2018/1/29.
 */
public class ExportService {
    private static Map<String, Object> serviceMap = new HashMap<>();
    private static Executor service = Executors.newFixedThreadPool(1);

    public static void exportService() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 8888));
        while (true) {
            Socket socket = serverSocket.accept();
            service.execute(new RunTask(socket));
        }
    }

    public static void registService(String serviceName, Object service) {
        serviceMap.put(serviceName, service);
    }

    private static class RunTask implements Runnable {
        private Socket socket;

        public RunTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                String interfaceName = ois.readUTF();
                String methodName = ois.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                Object[] args = (Object[]) ois.readObject();
                Object obj = serviceMap.get(interfaceName);
                Class<?> clazz = Class.forName(interfaceName);
                Method method = clazz.getMethod(methodName, parameterTypes);
                Object ret = method.invoke(obj, args);
                oos.writeObject(ret);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
