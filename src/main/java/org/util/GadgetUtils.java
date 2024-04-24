package org.util;

import org.gadget.inter.Gadget;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class GadgetUtils {
    public static byte[] getBytes(Gadget obj, String command) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(obj.getObject(command));
        return byteArrayOutputStream.toByteArray();
    }
}
