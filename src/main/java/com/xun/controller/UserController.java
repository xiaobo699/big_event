package com.xun.controller;

import com.xun.pojo.Result;
import com.xun.pojo.User;
import com.xun.service.UserService;
import com.xun.utils.JwtUtil;
import com.xun.utils.Md5Util;
import com.xun.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$")String username,@Pattern(regexp = "^\\S{5,16}$") String password) {
        //@Pattern(regexp = "^\\S{5,16}$")
        //查询用户
        User user = userService.findByUsername(username);
        if (user == null) {
            //没有注册
            userService.register(username, password);
            return Result.success();
        } else {
            //用户名被占用
            return Result.error("用户名已经存在");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$")String username,@Pattern(regexp = "^\\S{5,16}$") String password){
        //根据用户名查询用户
        User user = userService.findByUsername(username);
        //判断用户是否存在
        if(user == null){
            //用户不存在
            Result.error("用户名错误");
        }
        if(Md5Util.getMD5String(password).equals(user.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",user.getId());
            claims.put("username",user.getUsername());
            String token = JwtUtil.genToken(claims);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result userInfo(/*@RequestHeader(name = "Authorization") String token*/){
//        Map<String, Object> map = JwtUtil.parseToken(token);
//        String username = (String) map.get("username");
        Map<String,Object> mp = ThreadLocalUtil.get();
        String username = (String) mp.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }
}
