package com.marco.rpc;

import java.io.IOException;

/**
 * Created by maom3 on 2018/1/29.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String interfaceName = UserService.class.getName();
                System.out.println("registered service: " + interfaceName);
                ExportService.registService(interfaceName, new UserServiceImpl());
                try {
                    ExportService.exportService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(2000L);

        UserService service = ServiceProxyFactory.getService(UserService.class);
        User u = service.getUser(100001L);

        System.out.println(u);
    }
}
