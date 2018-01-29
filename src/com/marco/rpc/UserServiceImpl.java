package com.marco.rpc;

/**
 * Created by maom3 on 2018/1/29.
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        if (id == 100001L) {
            User user = new User();
            user.setId(id);
            user.setName("marco");
            user.setAge(18);
            return user;
        } else {
            return null;
        }
    }
}
