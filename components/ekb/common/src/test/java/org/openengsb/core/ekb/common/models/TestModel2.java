/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.core.ekb.common.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openengsb.core.api.model.OpenEngSBModel;
import org.openengsb.core.api.model.OpenEngSBModelEntry;
import org.osgi.framework.Version;

/**
 * Little class to test if the querying of models also works with self implemented classes and not only with proxied
 * interfaces
 */
@SuppressWarnings("serial")
public class TestModel2 implements OpenEngSBModel {
    private String id;
    private String name;
    private Date date;
    private ENUM enumeration;
    private List<String> list = new ArrayList<String>();
    private SubModel sub;
    private String edbId;
    private List<SubModel> subs = new ArrayList<SubModel>();
    private Map<String, OpenEngSBModelEntry> tail = new HashMap<String, OpenEngSBModelEntry>();

    public enum ENUM {
            A,
            B,
            C
    }

    @Override
    public List<OpenEngSBModelEntry> toOpenEngSBModelEntries() {
        List<OpenEngSBModelEntry> entries = toOpenEngSBModelValues();
        entries.addAll(tail.values());
        return entries;
    }
    
    @Override
    public List<OpenEngSBModelEntry> toOpenEngSBModelValues() {
        List<OpenEngSBModelEntry> entries = Arrays.asList(new OpenEngSBModelEntry("id", id, String.class),
            new OpenEngSBModelEntry("name", name, String.class),
            new OpenEngSBModelEntry("date", date, Date.class),
            new OpenEngSBModelEntry("test", enumeration, ENUM.class));
        for (int i = 0; i < list.size(); i++) {
            entries.add(new OpenEngSBModelEntry("list" + i, list.get(i), String.class));
        }
        if (sub != null) {
            entries.add(new OpenEngSBModelEntry("sub.id", sub.getId(), String.class));
            entries.add(new OpenEngSBModelEntry("sub.value", sub.getValue(), String.class));
        }
        return entries;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setEdbId(String edbId) {
        this.edbId = edbId;
    }

    public String getEdbId() {
        return edbId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ENUM getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(ENUM enumeration) {
        this.enumeration = enumeration;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setSub(SubModel sub) {
        this.sub = sub;
    }

    public SubModel getSub() {
        return sub;
    }

    public void setSubs(List<SubModel> subs) {
        this.subs = subs;
    }

    public List<SubModel> getSubs() {
        return subs;
    }

    @Override
    public void addOpenEngSBModelEntry(OpenEngSBModelEntry entry) {
        tail.put(entry.getKey(), entry);
    }

    @Override
    public void removeOpenEngSBModelEntry(String key) {
        tail.remove(key);
    }

    @Override
    public Object retrieveInternalModelId() {
        return edbId;
    }
    
    @Override
    public String retrieveModelName() {
        return this.getClass().getName();
    }
    
    @Override
    public String retrieveModelVersion() {
        return new Version("1.0.0").toString();
    }

    @Override
    public List<OpenEngSBModelEntry> getOpenEngSBModelTail() {
        return new ArrayList<OpenEngSBModelEntry>(tail.values());
    }

    @Override
    public void setOpenEngSBModelTail(List<OpenEngSBModelEntry> entries) {
        for (OpenEngSBModelEntry entry : entries) {
            tail.put(entry.getKey(), entry);
        }        
    }

    @Override
    public Long retrieveInternalModelTimestamp() {
        return null;
    }

    @Override
    public Integer retrieveInternalModelVersion() {
        return null;
    }
}
