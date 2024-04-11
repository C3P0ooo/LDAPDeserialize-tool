package org.util;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;

/*
* 获取client的 ip及端口
*/
public class IPUtils {
    public static SocketAddress getSocket(InMemoryInterceptedSearchResult result) throws Exception {
        Class<?> aClass = Class.forName("com.unboundid.ldap.listener.interceptor.InterceptedOperation");
        Method getClientConnection = aClass.getDeclaredMethod("getClientConnection");
        getClientConnection.setAccessible(true);
        Object invoke = getClientConnection.invoke(result);

        Class<?> aClass2 = Class.forName("com.unboundid.ldap.listener.LDAPListenerClientConnection");
        Field socket = aClass2.getDeclaredField("socket");
        socket.setAccessible(true);
        Socket socket1 = (Socket) socket.get(invoke);

        return socket1.getRemoteSocketAddress();
    }
}
