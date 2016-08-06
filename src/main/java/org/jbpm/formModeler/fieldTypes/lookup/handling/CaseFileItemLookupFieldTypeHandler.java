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

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandler;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Named("org.jbpm.formModeler.fieldTypes.lookup.handling.CaseFileItemLookupFieldTypeHandler")
public class CaseFileItemLookupFieldTypeHandler extends AbstractCaseFileItemLookupFieldTypeHandler {

    private Logger log = LoggerFactory.getLogger(CaseFileItemLookupFieldTypeHandler.class);


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
    public String getShowHTML(Object value, Field field, String inputName, String namespace) {
        return LookupUtil.resolveName(field, value);
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        Object paramVal = parametersMap.get(inputName);
        if (paramVal != null) {

            ObjectMarshallingStrategy marshallingStrategy = getCaseFileItemMarshallingStrategy(inputName);
            if (paramVal instanceof String[]) {
                paramVal = ((String[]) paramVal)[0];
            }
            String string = paramVal.toString();
            return LookupUtil.fromBase64String(marshallingStrategy, string);
        } else {
            return null;
        }
    }


    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{Object.class.getName()};
    }

    @Override
    public boolean isEmpty(Object value) {
        return value == null;
    }

    @Override
    protected String multiple() {
        return "";
    }

    @Override
    protected Set<String> getSelectedValuesAsString(ObjectMarshallingStrategy oms, Object value) {
        return Collections.singleton(LookupUtil.toBase64String(value,oms));
    }
}
