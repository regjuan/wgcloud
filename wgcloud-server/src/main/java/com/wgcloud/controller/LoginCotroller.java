package com.wgcloud.controller;

import com.wgcloud.config.CommonConfig;
import com.wgcloud.entity.AccountInfo;
import com.wgcloud.util.JwtUtil;
import com.wgcloud.util.shorturl.MD5;
import com.wgcloud.util.staticvar.StaticKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:LoginCotroller.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: LoginCotroller.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@RestController
@RequestMapping(value = "/login")
public class LoginCotroller {

    private static final Logger logger = LoggerFactory.getLogger(LoginCotroller.class);

    @Resource
    private CommonConfig commonConfig;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String userName = loginRequest.get("account");
        String passwd = loginRequest.get("password");

        try {
            String a = MD5.GetMD5Code(commonConfig.getAdmindPwd());
            Boolean b = a.equals(passwd);
            if (MD5.GetMD5Code(commonConfig.getAdmindPwd()).equals(passwd) && StaticKeys.ADMIN_ACCOUNT.equals(userName)) {
                final String token = jwtUtil.generateToken(userName);
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login successful");
                logger.info("User {} logged in successfully.", userName);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for user {}. Invalid credentials.", userName);
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Authentication error for user {}:", userName, e);
            return ResponseEntity.status(500).body("Authentication error");
        }
    }
}
