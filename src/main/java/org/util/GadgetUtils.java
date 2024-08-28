package org.util;

import org.gadget.inter.Gadget;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class GadgetUtils {
    public static byte[] getBytes(Gadget obj, String command,String path) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(obj.getObject(command,path));
        return byteArrayOutputStream.toByteArray();
    }

    public static Object getRefObj(String classname) throws Exception {
        Class<?> aClass = Class.forName(classname);
        Object obj = aClass.getConstructor().newInstance();
        return obj;
    }

    public static Object getRefObj(String classname, Class[] cls,Object[] args) throws Exception {
        Class<?> aClass = Class.forName(classname);
        Object obj = aClass.getConstructor(cls).newInstance(args);
        return obj;
    }
}
