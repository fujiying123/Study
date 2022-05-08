package com.atguigu.elasticsearch.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @Description：
 * @Date： 2022/5/7
 * @Author：小影
 */
@XStreamAlias("man")
public class Man{

    @XStreamAlias("name")
    private String name;
    @XStreamAlias("age")
    private Integer age;

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
}
