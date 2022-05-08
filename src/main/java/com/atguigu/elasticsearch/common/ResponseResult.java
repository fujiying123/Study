package com.atguigu.elasticsearch.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description：
 * @Date： 2021/6/8
 * @Author：小影
 */
public class ResponseResult {

    public static ResponseEntity<Map<String,Object>> createResult() {
        Map map = new HashMap();
        map.put("success",true);
        map.put("code", HttpStatus.OK);
        map.put("data",null);
        map.put("errMsg",null);
        return ResponseEntity.ok(map);
    }

    public static ResponseEntity<Map<String,Object>> createResult(Object obj) {
        Map map = new HashMap();
        map.put("success",true);
        map.put("code", HttpStatus.OK);
        map.put("data",obj);
        map.put("errMsg",null);
        return ResponseEntity.ok(map);
    }
    public static ResponseEntity<Map<String,Object>> createResult(Object obj,int status) {
        Map map = new HashMap();
        map.put("success",true);
        map.put("code", status);
        map.put("data",obj);
        map.put("errMsg",null);
        return ResponseEntity.ok(map);
    }
}
