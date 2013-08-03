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

package org.openengsb.core.edb.api;

import java.util.HashMap;
import java.util.Map;

/**
 * EDBObject handle an object that is ready to be put into the EDB and give access to some metadata. It contains a
 * map of helper functions for easier adding and retrieving of the EDBObjectEntries and the values it contains.
 */
@SuppressWarnings("serial")
public class EDBObject extends EDBBaseObject<EDBObjectEntry> {
    
	private EDBStage stage;
	
	/**
     * Create an EDBObject with a specified OID.
     */
    public EDBObject(String oid) {
        super(oid);
		this.stage = null;
    }
	
	public EDBObject(String oid, EDBStage stage){
		super(oid);
		this.stage = stage;
	}

    /**
     * Create an EDBObject using a Map of data. The OID is stored after loading the data Map, so any already existing
     * values with the special key representing the OID will be overwritten by the provided parameters.
     */
    public EDBObject(String oid, Map<String, EDBObjectEntry> data) {
        super(oid, data);
    }

    /**
     * Adds an EDBObjectEntry to this EDBObject
     */
    public void putEDBObjectEntry(String key, Object value, String type) {
        this.putEntry(key, value, type);
    }

    /**
     * Adds an EDBObjectEntry to this EDBObject
     */
    public void putEDBObjectEntry(String key, Object value, Class<?> type) {
        this.putEntry(key, value, type);
    }
    
    /**
     * Adds an EDBObjectEntry to this EDBObject. It uses the type of the given object value as type parameter
     */
    public void putEDBObjectEntry(String key, Object value) {
		this.putEntry(key, value);
    }

	@Override
	public void putEntry(String key, Object value, String type) {
		put(key, new EDBObjectEntry(key, value, type));
	}
	
	public void setEDBStage(EDBStage stage) {
		this.stage = stage;
	}
	
	public EDBStage getEDBStage(){
		return this.stage;
	}
}
