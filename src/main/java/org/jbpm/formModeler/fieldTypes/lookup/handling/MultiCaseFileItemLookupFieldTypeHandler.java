/**
 * Copyright (C) 2012 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.fieldTypes.lookup.handling;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("org.jbpm.formModeler.fieldTypes.lookup.handling.MultiCaseFileItemLookupFieldTypeHandler")
public class MultiCaseFileItemLookupFieldTypeHandler extends AbstractCaseFileItemLookupFieldTypeHandler {

    private Logger log = LoggerFactory.getLogger(MultiCaseFileItemLookupFieldTypeHandler.class);
    @Inject
    private FormRenderContextManager formRenderContextManager;

    public final String SIZE_UNITS[] = new String[]{"bytes", "Kb", "Mb"};

    protected String dropIcon;
    protected String iconFolder;
    protected String defaultFileIcon;

    @PostConstruct
    public void init() {
        // Initializing the images paths that are going to be used in the UI
        dropIcon = "/formModeler/images/general/16x16/ico-trash.png";
        iconFolder = "/formModeler/images/fileTypeIcons/16x16/";
        defaultFileIcon = "RTF.png";
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        Object paramVal = parametersMap.get(inputName);
        if (paramVal != null) {
            ObjectMarshallingStrategy marshallingStrategy = getCaseFileItemMarshallingStrategy(inputName);
            String[] values = ((String[]) paramVal);
            List<Object> result = new ArrayList<Object>();
            for (String value : values) {
                result.add(LookupUtil.fromBase64String(marshallingStrategy, value));
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getShowHTML(Object value, Field field, String inputName, String namespace) {
        StringBuilder sb = new StringBuilder();
        if(value instanceof Collection){
            Collection coll= (Collection) value;
            for (Object o : coll) {
                sb.append(LookupUtil.resolveName(field,o));
                sb.append(',');
            }
        }
        return sb.substring(0, sb.length()-1);
    }

    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{Object.class.getName()};
    }

    @Override
    public boolean isEmpty(Object value) {
        if(value instanceof Collection){
            return ((Collection) value).isEmpty();
        }
        return true;
    }

    @Override
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        Map<String, Object> parametersMap = new HashMap<String, Object>();
        if (objectValue instanceof Collection) {
            ObjectMarshallingStrategy oms = getCaseFileItemMarshallingStrategy(inputName);
            Collection coll = (Collection) objectValue;
            Set<String> values = getSelectedValuesAsString(oms, coll);
            parametersMap.put(inputName, values.toArray(new String[values.size()]));
        }
        return parametersMap;
    }

    @Override
    protected String multiple() {
        return "multiple";
    }

    @Override
    protected Set<String> getSelectedValuesAsString(ObjectMarshallingStrategy oms, Object val) {
        Collection coll= (Collection) val;
        Set<String> values = new HashSet<String>();
        for (Object o : coll) {
            values.add(LookupUtil.toBase64String(o, oms));
        }
        return values;
    }
}
