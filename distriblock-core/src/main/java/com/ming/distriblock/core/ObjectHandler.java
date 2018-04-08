package com.ming.distriblock.core;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by xueming on 2018/4/6.
 * 对象处理器
 */
public class ObjectHandler {

    /**
     * 对象反序列化
     * @param buf
     * @param <T>
     * @return
     * @throws Exception
     */
    public static   <T> T readObject(byte[] buf) throws Exception{
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        ObjectInputStream ois = new ObjectInputStream(is);
        T object = (T) ois.readObject();
        return object;
    }

    /**
     * 将对象进行序列化
     * @param obj
     * @return
     * @throws Exception
     */
    public static  byte[] writeObject(Object obj) throws Exception{
        ByteOutputStream bos = new ByteOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.getBytes();
    }
}
