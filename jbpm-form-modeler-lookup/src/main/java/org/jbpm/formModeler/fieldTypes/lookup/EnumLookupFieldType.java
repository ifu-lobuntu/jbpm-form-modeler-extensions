package org.jbpm.formModeler.fieldTypes.lookup;

import java.util.Locale;
import java.util.ResourceBundle;

import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.fieldTypes.lookup.handling.EnumLookupFieldTypeHandler;

public class EnumLookupFieldType extends PlugableFieldType {
    public static final String CODE = "EnumLookup";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getManagerClass() {
        return EnumLookupFieldTypeHandler.class.getName();
    }

    @Override
    public String getFieldClass() {
        return Object.class.getName();
    }

    @Override
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.fieldTypes.lookup.messages", locale);
        return bundle.getString("enumLookup.description");
    }
}
