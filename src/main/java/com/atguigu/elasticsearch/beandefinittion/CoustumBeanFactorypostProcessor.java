package com.atguigu.elasticsearch.beandefinittion;

import com.atguigu.elasticsearch.entities.Man;
import com.atguigu.elasticsearch.entities.Person;
import com.atguigu.elasticsearch.utils.XStreamUtils;
import com.thoughtworks.xstream.XStream;
import lombok.SneakyThrows;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Description：
 * @Date： 2022/5/8
 * @Author：小影
 */
@Component
public class CoustumBeanFactorypostProcessor implements BeanFactoryPostProcessor {

    @Autowired
    private ApplicationContext context;

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:protocol/**/*.xml");
//        Resource[] resources = context.getResources("classpath:protocol/**/*.xml");
        InputStream inputStream = null;
        XStream instance = XStreamUtils.getInstance();
        SAXReader saxReader = XStreamUtils.getSaxReader();
        instance.allowTypes(new Class[]{Person.class,Man.class});
        instance.processAnnotations(Person.class);
        instance.processAnnotations(Man.class);
        for (Resource resource : resources) {
            inputStream = resource.getInputStream();

            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            String id = rootElement.attributeValue("id");
            String namespace = rootElement.attributeValue("namespace");
            String type = rootElement.attributeValue("type");
            Class<?> aClass = Class.forName(type);
            Object newInstance = aClass.newInstance();
            instance.fromXML(rootElement.asXML(),newInstance);
            beanFactory.registerSingleton(id+":"+namespace,newInstance);
        }
    }
}
