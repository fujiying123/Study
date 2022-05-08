package com.atguigu.elasticsearch.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.dom4j.io.SAXReader;

/**
 * @Description：
 * @Date： 2022/5/8
 * @Author：小影
 */
public class XStreamUtils {

    private static final XStream xStream;
    private static final SAXReader SAX_READER;
    static {
        xStream = new XStream(new DomDriver());
        xStream.autodetectAnnotations(true);
        SAX_READER = new SAXReader();
    }

    public static XStream getInstance() {
        return xStream;
    }

    public static void allowTypes(Class... args) {
        xStream.allowTypes(args);
        for (Class aClass : args) {
            xStream.processAnnotations(aClass);
        }
    }

    public static SAXReader getSaxReader() {
        return SAX_READER;
    }


}
