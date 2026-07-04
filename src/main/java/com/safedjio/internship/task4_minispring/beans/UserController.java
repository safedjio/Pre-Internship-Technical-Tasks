package com.safedjio.internship.task4_minispring.beans;

import com.safedjio.internship.task4_minispring.annotation.Autowired;
import com.safedjio.internship.task4_minispring.annotation.Component;
import com.safedjio.internship.task4_minispring.lifecycle.InitializingBean;

@Component
public class UserController implements InitializingBean {

     @Autowired
    private UserService userService;

     private String initMessage;
     public  String getInitMessage() {
         return initMessage;
     }

    public UserService getUserService() {
        return userService;
    }

    @Override
    public void afterPropertiesSet() {
         if(userService == null) {
             throw new RuntimeException("DI failed! UserService is null");
         }
         this.initMessage = "Controller Initialized with user: " + userService.getUserName();
    }
}
