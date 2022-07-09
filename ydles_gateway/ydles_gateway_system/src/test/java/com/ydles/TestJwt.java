package com.ydles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;

/**
 * @author Scz
 * @date 2022/4/12 14:40
 */
public class TestJwt {
    @Test
    public void test(){
        JwtBuilder jwtBuilder = Jwts.builder()
                                    .setId("27")    // 设置令牌id
                                    .setSubject("subject")      //主题
                                    .setIssuedAt(new Date())
                                    .signWith(SignatureAlgorithm.HS256, "ydles"); // 设置签名和秘钥
        // 加密
        String jwt = jwtBuilder.compact();

        // 解密
        Claims payLoads = Jwts.parser().setSigningKey("ydles").parseClaimsJws(jwt).getBody();
        System.out.println(payLoads);


    }
}
