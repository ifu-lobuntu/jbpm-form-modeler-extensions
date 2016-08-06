package org.jbpm.formModeler.fieldTypes.lookup;

import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.fieldTypes.lookup.handling.DateIntervalFieldTypeHandler;
import org.jbpm.formModeler.fieldTypes.lookup.handling.EnumLookupFieldTypeHandler;
import org.joda.time.Interval;

import java.util.Locale;
import java.util.ResourceBundle;

public class DateIntervalFieldType extends PlugableFieldType {
    public static final String CODE = "DateInterval";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getManagerClass() {
        return DateIntervalFieldTypeHandler.class.getName();
    }

    @Override
    public String getFieldClass() {
        return Interval.class.getName();
    }

    @Override
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.fieldTypes.lookup.messages", locale);
        return bundle.getString("dateInterval.description");
    }
}
