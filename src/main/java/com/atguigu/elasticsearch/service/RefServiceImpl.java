package com.atguigu.elasticsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * @Description：
 * @Date： 2022/4/28
 * @Author：小影
 */
@Service
public class RefServiceImpl implements RefService{

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void ref() throws Exception {
        System.out.println("我来了");
        String name = "com.atguigu.elasticsearch.service.RefService";
        Class<?> aClass = Class.forName(name);

        Method test = aClass.getMethod("eat");
        test.invoke(applicationContext.getBean(aClass));
    }

    @Override
    public void eat() {
        System.out.println("我吃了");
    }
}
