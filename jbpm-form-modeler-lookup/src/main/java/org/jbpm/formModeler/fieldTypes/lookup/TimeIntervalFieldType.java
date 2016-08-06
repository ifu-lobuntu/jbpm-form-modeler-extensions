package org.jbpm.formModeler.fieldTypes.lookup;

import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.fieldTypes.lookup.handling.EnumLookupFieldTypeHandler;
import org.jbpm.formModeler.fieldTypes.lookup.handling.TimeIntervalFieldTypeHandler;
import org.joda.time.Interval;

import java.util.Locale;
import java.util.ResourceBundle;

public class TimeIntervalFieldType extends PlugableFieldType {
    public static final String CODE = "TimeInterval";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getManagerClass() {
        return TimeIntervalFieldTypeHandler.class.getName();
    }

    @Override
    public String getFieldClass() {
        return Interval.class.getName();
    }

    @Override
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.fieldTypes.lookup.messages", locale);
        return bundle.getString("timeInterval.description");
    }
}
