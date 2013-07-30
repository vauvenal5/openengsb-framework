/*
 * Copyright 2013 vauve_000.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openengsb.core.edb.api;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vauve_000
 */
public abstract class EDBBaseObject<T extends EDBObjectEntry> extends HashMap<String, T> {
	private static final String OID_CONST = "oid";
    private static final String DELETED_CONST = "isDeleted";
	
	protected EDBBaseObject(String oid) {
		super();
		this.setOID(oid);
	}

	protected EDBBaseObject(String oid, Map<String, T> data) {
		super(data);
		this.setOID(oid);
	}
	
    /**
     * Retrieve the timestamp for this object.
     */
    public final Long getTimestamp() {
        return getLong(EDBConstants.MODEL_TIMESTAMP);
    }

    /**
     * This function updates the timestamp for this object. This is necessary if you want to commit the object to the
     * database. Should be set by the EnterpriseDatabaseService in the commit procedure.
     */
    public void updateTimestamp(Long timestamp) {
        putEntry(EDBConstants.MODEL_TIMESTAMP, timestamp);
    }

    /**
     * Retrieve the OID for this object.
     */
    public String getOID() {
        return getString(OID_CONST);
    }

    /**
     * Sets the OID
     */
    public void setOID(String oid) {
        putEntry(OID_CONST, oid);
    }

    /**
     * Returns the value of the EDBObjectEntry for the given key, casted as String. Returns null if there is no element
     * for the given key, or the value for the given key is null.
     */
    public String getString(String key) {
        return getObject(key, String.class);
    }

    /**
     * Returns the value of the EDBObjectEntry for the given key, casted as Long. Returns null if there is no element
     * for the given key, or the value for the given key is null.
     */
    public Long getLong(String key) {
        return getObject(key, Long.class);
    }

    /**
     * Returns the value of the EDBObjectEntry for the given key. Returns null if there is no element for the given key,
     * or the value for the given key is null.
     */
    public Object getObject(String key) {
        return getObject(key, Object.class);
    }

    /**
     * Returns the value of the EDBObjectEntry for the given key, casted as the given class. Returns null if there is no
     * element for the given key, or the value for the given key is null.
     */
    @SuppressWarnings("unchecked")
    public <F> F getObject(String key, Class<F> clazz) {
        T entry = get(key);
        return entry == null ? null : (F) entry.getValue();
    }

    /**
     * Returns true if the object is deleted.
     */
    public final Boolean isDeleted() {
        Boolean deleted = getObject(DELETED_CONST, Boolean.class);
        return deleted != null ? deleted : false;
    }

    /**
     * Sets the boolean value if the object is deleted or not
     */
    public void setDeleted(Boolean deleted) {
        putEntry(DELETED_CONST, deleted);
    }

    /**
     * Adds an EDBObjectEntry to this EDBObject
     */
    public abstract void putEntry(String key, Object value, String type);

    /**
     * Adds an EDBObjectEntry to this EDBObject
     */
    public void putEntry(String key, Object value, Class<?> type) {
        putEntry(key, value, type.getName());
    }
    
    /**
     * Adds an EDBObjectEntry to this EDBObject. It uses the type of the given object value as type parameter
     */
    public void putEntry(String key, Object value) {
        putEntry(key, value, value.getClass());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, T> entry : this.entrySet()) {
            appendEntry(entry, builder);
        }
        builder.append("}");

        return builder.toString();
    }

    /**
     * Analyzes the entry and write the specific information into the StringBuilder.
     */
    private void appendEntry(Map.Entry<String, T> entry, StringBuilder builder) {
        if (builder.length() > 2) {
            builder.append(",");
        }
        builder.append(" \"").append(entry.getKey()).append("\"");
        builder.append(" : ").append(entry.getValue());
    }
}
