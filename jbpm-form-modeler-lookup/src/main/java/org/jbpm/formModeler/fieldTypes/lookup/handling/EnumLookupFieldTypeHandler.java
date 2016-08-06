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

import java.io.*;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandler;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("org.jbpm.formModeler.fieldTypes.lookup.handling.EnumLookupFieldTypeHandler")
public class EnumLookupFieldTypeHandler extends PlugableFieldHandler {

    private Logger log = LoggerFactory.getLogger(EnumLookupFieldTypeHandler.class);

    // @Inject
    private DeploymentService deploymentService;

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
        try {
            FormRenderContext context = formRenderContextManager.getRootContext(inputName);
            InternalRuntimeManager manager = (InternalRuntimeManager)RuntimeManagerRegistry.get().getManager(context.getDeploymentId());
            Object var=parametersMap.get(inputName);
            if(var instanceof String[]){
                var=((String[])var)[0];
            }
            String[] split = ((String) var).split("\\|");
            Class<? extends Enum> cls = (Class<? extends Enum>) Class.forName(split[0], true, manager.getEnvironment().getClassLoader());
            return Enum.valueOf(cls, split[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected ObjectMarshallingStrategy getCaseFileItemMarshallingStrategy(String inputName) {
        return null;
    }

    @Override
    public String getShowHTML(Object value, Field field, String inputName, String namespace) {
        return value + "";// TODO configure name field
    }

    @Override
    public String getInputHTML(Object value, Field field, String inputName, String namespace, Boolean readonly) {
        try {
            StringBuilder sb = new StringBuilder("<option>Select ...</option>");
            if (field.getParam5() != null) {
                try {
                    FormRenderContext context = formRenderContextManager.getRootContext(inputName);
                    InternalRuntimeManager manager = (InternalRuntimeManager)RuntimeManagerRegistry.get().getManager(context.getDeploymentId());
                    Class<?> enumClass = Class.forName(field.getParam5(),true,manager.getEnvironment().getClassLoader());
                    Enum<?>[] enums= (Enum<?>[]) enumClass.getEnumConstants();
                    for (Enum<?> anEnum : enums) {
                        sb.append("<option value=\"");
                        sb.append(enumClass.getName());
                        sb.append("|");
                        sb.append(anEnum.name());
                        sb.append("\">");
                        sb.append(anEnum.name());
                        sb.append("</option>");
                    }
                } catch (ClassNotFoundException e) {
                    String param4 = field.getParam4();
                    if (param4 != null) {
                        String[] split = param4.split("\\,");
                        for (String option : split) {
                            sb.append("<option value=\"");
                            sb.append("\">");
                            sb.append(option);
                            sb.append("</option>");
                        }
                    }
                }
            }
            return "<select name=\"" + inputName + "\" > " + sb + " </select > ";
        } catch (Exception e) {
            return "<select><option>Select ...</option></select>";
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
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        if(objectValue instanceof Enum){
            Enum en= (Enum) objectValue;
            return Collections.singletonMap(inputName, en.getClass().getName() +"|"+  en.name());
        }
        return Collections.singletonMap(inputName, "");
    }
}
