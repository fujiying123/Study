package com.atguigu.elasticsearch.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @Description：
 * @Date： 2021/4/21
 * @Author：小影
 */
@XStreamAlias("person")
public class Person {

    @XStreamAsAttribute
    @XStreamAlias("name")
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("age")
    private Integer age;

    @XStreamAlias("man")
    private Man man;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
        System.out.println("有参构造");
    }

    protected String getNa() {
        return this.name;
    }

    public Person() {
        System.out.println("无参构造");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Man getMan() {
        return man;
    }

    public void setMan(Man man) {
        this.man = man;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
