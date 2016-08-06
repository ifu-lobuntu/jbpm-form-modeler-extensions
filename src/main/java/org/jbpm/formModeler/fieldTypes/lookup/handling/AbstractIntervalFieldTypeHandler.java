package org.jbpm.formModeler.fieldTypes.lookup.handling;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandler;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ampie on 2015/10/01.
 */
public abstract class AbstractIntervalFieldTypeHandler extends PlugableFieldHandler {
    protected String dropIcon;
    protected String iconFolder;
    protected String defaultFileIcon;
    private Logger log = LoggerFactory.getLogger(TimeIntervalFieldTypeHandler.class);

    @PostConstruct
    public void init() {
        // Initializing the images paths that are going to be used in the UI
        dropIcon = "/formModeler/images/general/16x16/ico-trash.png";
        iconFolder = "/formModeler/images/fileTypeIcons/16x16/";
        defaultFileIcon = "RTF.png";
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        try {
            Date from = parseDate(field, inputName, parametersMap, "From");
            Date to = parseDate(field, inputName, parametersMap, "To");
            return new Interval(from.getTime(), to.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date parseDate(Field field, String inputName, Map parametersMap, String s) throws ParseException {
        Object var = parametersMap.get(inputName + s);
        String val = null;
        if (var instanceof String[]) {
            val = ((String[]) var)[0];
        } else {
            val = (String) var;
        }
        return parseDate(val);
    }

    protected abstract Date parseDate(String s);

    @Override
    public String getShowHTML(Object value, Field field, String inputName, String namespace) {
        return getHtml(value, field, inputName, "readonly");
    }

    @Override
    public String getInputHTML(Object value, Field field, String inputName, String namespace, Boolean readonly) {
        String readOnly = Boolean.TRUE.equals(readonly) ? "readonly" : "";
        return getHtml(value, field, inputName, readOnly);
    }

    protected String getHtml(Object value, Field field, String inputName, String readOnly) {
        String type = getInputType();
        field.getCssStyle();
        if (value == null) {
            return
                    "<div><input type=\"" + type + "\" name=\"" + inputName + "From\" " + readOnly + "/> - <input type=\"" + type + "\" name=\"" + inputName + "To\" " + readOnly + "/></div>";
        } else {
            Interval interval = (Interval) value;
            Date from = getDate(interval.getStartMillis());
            Date to = getDate(interval.getEndMillis());
            return "<div><input type=\"" + type + "\" name=\"" + inputName + "From\" " + readOnly + "value=\"" + from.toString() + "\"/> - <input type=\"" + type + "\" name=\"" + inputName + "To\" value=\"" + to.toString() + "\" " + readOnly + "/></div>";
        }
    }

    protected abstract String getInputType();

    protected abstract Date getDate(long startMillis);

    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{Interval.class.getName()};
    }

    @Override
    public boolean isEmpty(Object value) {
        return value == null;
    }

    @Override
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        Map<String, String[]> result = new HashMap<String, String[]>();
        if (objectValue instanceof Interval) {
            result.put(inputName + "From", new String[]
                    {getDate(((Interval) objectValue).getStartMillis()).toString()});
            result.put(inputName + "To", new String[]
                    {getDate(((Interval) objectValue).getEndMillis()).toString()});
        }
        return result;
    }
}
