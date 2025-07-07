// src/main/java/com/example/bytebrigade/utils/JwtUtil.java
package com.example.course.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    // 密钥 (一个随便的字符串，课程项目够用了)
    private static final String SECRET_KEY = "ByteBrigadeProjectKey";

    // 过期时间: 12小时
    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000;

    /**
     * 生成 Token (签发通行证)
     * @param payload 想要放在通行证里的信息 (比如用户ID、用户名)
     * @return Token 字符串
     */
    public static String createToken(Map<String, Object> payload) {
        return JWT.create()
                .withClaim("payload", payload) // 存放你的数据
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 设置过期时间
                .sign(Algorithm.HMAC256(SECRET_KEY)); // 使用密钥签名
    }

    /**
     * 解析 Token (检查通行证)
     * @param token 前端传来的 Token
     * @return 通行证里存放的信息
     */
    public static Map<String, Object> parseToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token); // 如果 token 有问题(过期、伪造)，这里会直接报错
        return decodedJWT.getClaim("payload").asMap();
    }
}