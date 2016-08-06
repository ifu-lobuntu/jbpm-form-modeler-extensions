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

import javax.inject.Named;
import java.sql.Timestamp;
import java.util.Date;

@Named("org.jbpm.formModeler.fieldTypes.lookup.handling.DateIntervalFieldTypeHandler")
public class DateIntervalFieldTypeHandler extends AbstractIntervalFieldTypeHandler {

    @Override
    protected Date parseDate(String s) {
        return java.sql.Date.valueOf(s);
    }

    public static void main(String[] args) {
        System.out.println(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected String getInputType() {
        return "date";
    }

    @Override
    protected Date getDate(long startMillis) {
        return new java.sql.Date(startMillis);
    }

}
