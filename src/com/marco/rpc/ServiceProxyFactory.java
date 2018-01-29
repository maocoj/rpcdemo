package com.marco.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by maom3 on 2018/1/29.
 */
public class ServiceProxyFactory {
    public static <T> T getService(Class<T> clazz) {
        if (clazz.isInterface()) {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    ObjectOutput out = null;
                    ObjectInputStream in = null;
                    Socket socket = null;
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress("localhost", 8888));
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());
                        out.writeUTF(clazz.getName());
                        out.writeUTF(method.getName());
                        out.writeObject(method.getParameterTypes());
                        out.writeObject(args);
                        return in.readObject();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
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
            });
        } else {
            return null;
        }

    }
}
