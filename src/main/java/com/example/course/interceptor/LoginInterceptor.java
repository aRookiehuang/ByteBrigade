package com.example.course.interceptor;

import com.example.course.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头里获取 Token
        String token = request.getHeader("Authorization");

        try {
            // 2. 尝试用我们的 "暗号" 检查通行证
            JwtUtil.parseToken(token);
            // 3. 检查通过，放行
            return true;
        } catch (Exception e) {
            // 4. 检查失败 (token是假的、过期了、或者根本没给)
            //   - 设置状态码 401 (Unauthorized)
            //   - 拦截，不放行
            response.setStatus(401);
            return false;
        }
    }
}
