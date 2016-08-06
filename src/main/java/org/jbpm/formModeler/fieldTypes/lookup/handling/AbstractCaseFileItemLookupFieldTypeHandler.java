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

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class AbstractCaseFileItemLookupFieldTypeHandler extends PlugableFieldHandler {
    @Inject
    private FormRenderContextManager formRenderContextManager;

    protected ObjectMarshallingStrategy getCaseFileItemMarshallingStrategy(String inputName) {
        FormRenderContext context = formRenderContextManager.getRootContext(inputName);
        InternalRuntimeManager manager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(context.getDeploymentId());
        if (manager != null) {
            ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) manager.getEnvironment().getEnvironment()
                    .get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
            if (strategies != null) {
                for (ObjectMarshallingStrategy strategy : strategies) {
                    if (strategy.getClass().getName().equals("org.jbpm.cmmn.casefile.jpa.JpaPlaceHolderResolverStrategy")) {
                        // TODO use instanceof
                        return strategy;
                    }
                }
            }
        }
        return null;
    }


    @Override
    public String getInputHTML(Object value, final Field field, String inputName, String namespace, Boolean readonly) {
        ObjectMarshallingStrategy oms = getCaseFileItemMarshallingStrategy(inputName);
        Set<String> serializedValues= getSelectedValuesAsString(oms, value);
        StringBuilder sb = new StringBuilder();
        if (oms != null) {
            FormRenderContext context = formRenderContextManager.getRootContext(inputName);
            InternalRuntimeManager manager = (InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(context.getDeploymentId());
            final RuntimeEngine re = manager.getRuntimeEngine(EmptyContext.get());
            Map<String, String> map = re.getKieSession().execute(new LookupCommand(context, field, oms));
            for (Map.Entry<String, String> o : map.entrySet()) {
                sb.append("<option value=\"");
                sb.append(o.getKey());
                sb.append("\" ");
                if (serializedValues.contains(o.getKey())) {
                    sb.append("selected ");
                }
                sb.append(">");
                sb.append(o.getValue());

                sb.append("</option>");
            }
        } else {
            sb.append("<option>Select ...</option>");
        }

        return "<select name=\"" + inputName + "\" "+multiple()+"><option>Select ... </option>" + sb.toString() + "</select>";//TODO use async json request to retrieve the options
    }

    protected abstract String multiple();

    protected abstract Set<String> getSelectedValuesAsString(ObjectMarshallingStrategy oms, Object value);

    @Override
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        Map<String, Object> parametersMap = new HashMap<String, Object>();
        if (objectValue != null) {
            ObjectMarshallingStrategy marshallingStrategy = getCaseFileItemMarshallingStrategy(inputName);
            String serializedValue = LookupUtil.toBase64String(objectValue, marshallingStrategy);
            parametersMap.put(inputName, new String[]{serializedValue});
        }
        return parametersMap;
    }
}
