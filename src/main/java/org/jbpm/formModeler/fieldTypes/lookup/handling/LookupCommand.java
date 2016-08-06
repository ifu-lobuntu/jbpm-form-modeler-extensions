package org.jbpm.formModeler.fieldTypes.lookup.handling;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.cmmn.instance.CaseFilePersistence;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LookupCommand implements GenericCommand<Map<String, String>> {
    private final Field field;
    private final ObjectMarshallingStrategy oms;
    FormRenderContext context;

    public LookupCommand(FormRenderContext context, Field field, ObjectMarshallingStrategy oms) {
        this.context = context;
        this.field = field;
        this.oms = oms;
    }

    @Override
    public Map<String, String> execute(Context ctx) {
        KnowledgeCommandContext kcc = (KnowledgeCommandContext) ctx;
        Map<String, String> result = new HashMap<String, String>();
        field.setParam3("readAll");
        if ("readFromCaseInstance".equals(field.getParam3())) {
            Task task = (Task) context.getInputData().get("task");
            final long processInstanceId = task.getTaskData().getProcessInstanceId();
            WorkflowProcessInstance pi = (WorkflowProcessInstance) kcc.getKieSession().getProcessInstance(processInstanceId);
            if (pi != null) {
                WorkflowProcess p = (WorkflowProcess) pi.getProcess();
                VariableScope vs = (VariableScope) p.getDefaultContext(VariableScope.VARIABLE_SCOPE);
                for (Variable variable : vs.getVariables()) {
                    if (variable.getType() instanceof ObjectDataType) {
                        ObjectDataType odt = (ObjectDataType) variable.getType();
                        if (odt.getClassName().equals(field.getParam5())) {
                            Object varValue = pi.getVariable(variable.getName());
                            if (varValue instanceof Collection) {
                                populateResultMap((Collection) varValue, result);
                            } else if (varValue != null) {
                                result.put(LookupUtil.toBase64String(varValue, oms), LookupUtil.resolveName(field, varValue));
                            }
                        }
                    }
                }
            }
        } else if ("readAll".equals(field.getParam3())) {
            CaseFilePersistence cfp = (CaseFilePersistence) kcc.getKieSession().getEnvironment().get(CaseFilePersistence.ENV_NAME);
            populateResultMap(cfp.readAll(field.getParam5()),result);
        } else if (field.getParam3() != null && field.getParam3().length() > 0) {
            CaseFilePersistence cfp = (CaseFilePersistence) kcc.getKieSession().getEnvironment().get(CaseFilePersistence.ENV_NAME);
            populateResultMap(cfp.executeNamedQuery(field.getParam3(),context.getInputData()),result);
        }
        return result;
    }

    private void populateResultMap(Collection collection, Map<String, String> result) {
        for (Object o : collection) {
            result.put(LookupUtil.toBase64String(o, oms), LookupUtil.resolveName(field, o));
        }
    }
}
