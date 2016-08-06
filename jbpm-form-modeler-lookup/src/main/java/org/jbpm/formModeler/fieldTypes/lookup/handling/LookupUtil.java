package org.jbpm.formModeler.fieldTypes.lookup.handling;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.formModeler.api.model.Field;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

import java.io.*;

/**
 * Created by ampie on 2015/09/07.
 */
public class LookupUtil {
    public static String toBase64String(Object objectValue, ObjectMarshallingStrategy marshallingStrategy) {
        if(objectValue==null){
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //write the details from the parametersMap
            marshallingStrategy.write(new ObjectOutputStream(baos), objectValue);
            String serializedValue = new String(Base64.encodeBase64(baos.toByteArray()));
            return serializedValue;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String resolveName(Field field, Object o) {
        Object name;
        try {
            name = o.getClass().getMethod("get" + Character.toUpperCase(field.getParam4().charAt(0)) + field.getParam4().substring(1)).invoke(o);
        } catch (RuntimeException e1) {
            throw e1;
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        return (String)name;
    }

    public static Object fromBase64String(ObjectMarshallingStrategy marshallingStrategy, String base64) throws IOException, ClassNotFoundException {
        ByteArrayInputStream baos = new ByteArrayInputStream(Base64.decodeBase64(base64.getBytes()));
        //write the details from the parametersMap
        return marshallingStrategy.read(new ObjectInputStream(baos));
    }


}
