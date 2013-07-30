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

import java.util.Map;

/**
 *
 * 
 */
public class EDBStageObject extends EDBBaseObject<EDBStageObjectEntry> {
//extends EDBObject {
	private static final String SID_CONST = "sid";
	
    public EDBStageObject(String stageId, String oid) {
        super(oid);
		setStageId(stageId);
    }

    public EDBStageObject(String stageId, String oid, Map<String, EDBStageObjectEntry> data) {
        super(oid, data);
		setStageId(stageId);
    }
	
	public String getStageId(){
		return this.getString(SID_CONST);
	}
	
	public void setStageId(String stageId){
		this.putEntry(SID_CONST, stageId);
	}

	@Override
	public void putEntry(String key, Object value, String type) {
		put(key, new EDBStageObjectEntry(this.getStageId(), key, value, type));
	}
}
