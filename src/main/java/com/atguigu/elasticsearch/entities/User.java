package com.atguigu.elasticsearch.entities;

import com.atguigu.elasticsearch.utils.XStreamUtils;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;

/**
 * @Description：
 * @Date： 2021/8/16
 * @Author：小影
 */
public class User {

    private String name;

    private String sex;

    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public static void main(String[] args) throws Exception{
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:protocol/**/*.xml");
//        Resource[] resources = context.getResources("classpath:protocol/**/*.xml");
        InputStream inputStream = null;
        XStream instance = XStreamUtils.getInstance();
        SAXReader saxReader = XStreamUtils.getSaxReader();

        for (Resource resource : resources) {
            inputStream = resource.getInputStream();

            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            String id = rootElement.attributeValue("id");
            String namespace = rootElement.attributeValue("namespace");
            String type = rootElement.attributeValue("type");
            Class<?> aClass = Class.forName(type);
            Object newInstance = aClass.newInstance();
            instance.allowTypes(new Class[]{aClass,Man.class,Person.class});
            instance.processAnnotations(aClass);
            instance.processAnnotations(Person.class);
            instance.processAnnotations(Man.class);
            Person person = new Person();
            instance.fromXML(rootElement.asXML(), person);

        }
    }
}
