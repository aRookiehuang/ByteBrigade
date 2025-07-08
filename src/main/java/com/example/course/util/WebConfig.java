package com.example.course.util;


import com.example.course.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口路径生效
                .allowedOrigins("*") // 允许所有来源的请求，为了方便测试。生产环境应该指定前端的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(false); // 是否允许携带凭证，如果不需要cookie/session可以设为false
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /avatars/** 的请求映射到 E:/avator/ 目录
        // 使用配置化的路径映射
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:./uploads/avatars/");
    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 添加拦截器，并配置规则
//        //只放行登录注册，其他全部拦截
//        registry.addInterceptor(loginInterceptor)
//                // 拦截所有 /user/ 下的路径
//                .addPathPatterns("/user/**","/course/**")
//                // 但是，放行登录和注册接口
//                .excludePathPatterns("/login", "/login/register");
//    }
}
