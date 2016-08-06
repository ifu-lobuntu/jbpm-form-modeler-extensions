package org.jbpm.formModeler.fieldTypes.lookup;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.Priority;
import org.jbpm.formModeler.service.annotation.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LookupFieldTypeDeployer implements Startable {
    private Logger log = LoggerFactory.getLogger(LookupFieldTypeDeployer.class);

    @Inject
    private FormManager formManager;
    @Inject
    private LocaleManager localeManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public void start() throws Exception {
        Map<String, Properties> formResources = loadFormResources();
        addSystemForm(CaseFileItemLookupFieldType.CODE, formResources);
        addSystemForm(MultiCaseFileItemLookupFieldType.CODE, formResources);
        addSystemForm(EnumLookupFieldType.CODE, formResources);
        addSystemForm(DateIntervalFieldType.CODE, formResources);
        addSystemForm(TimeIntervalFieldType.CODE, formResources);
    }

    private Map<String, Properties> loadFormResources() {
        Map<String, Properties> formResources = new HashMap<String, Properties>();
        loadFormResourcesInto("org/jbpm/formModeler/core/forms/forms-resources", formResources);
        loadFormResourcesInto("org/jbpm/formModeler/fieldTypes/lookup/messages", formResources);
        return formResources;
    }

    private void loadFormResourcesInto(String prefix, Map<String, Properties> formResources) {
        for (String lang : localeManager.getPlatformAvailableLangs()) {
            try {
                String key = lang.equals(localeManager.getDefaultLang()) ? "" : "_" + lang;
                InputStream in = LookupFieldTypeDeployer.class.getClassLoader().getResourceAsStream(prefix + key + ".properties");
                if (in == null) continue;
                Properties props = new Properties();
                props.load(in);
                Properties oldProperties = formResources.get(lang);
                if (oldProperties == null) {
                    formResources.put(lang, props);
                } else {
                    oldProperties.putAll(props);
                }
            } catch (Exception e) {
                log.warn("Error loading resources form lang \"{}\": {}", lang, e);
            }
        }
    }

    private void addSystemForm(String code, Map<String, Properties> formResources) throws Exception {
        try {
            String prefix = "org/jbpm/formModeler/fieldTypes/lookup/";
            InputStream in = LookupFieldTypeDeployer.class.getClassLoader().getResourceAsStream(prefix + code + ".form");
            Form form = formSerializationManager.loadFormFromXML(in, formResources);
            formManager.addSystemForm(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
