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

import com.google.common.base.Objects;

/**
 *
 * 
 */
public class EDBStageObjectEntry extends EDBObjectEntry {
	private String stageId;
	
	public EDBStageObjectEntry() {
    }

    public EDBStageObjectEntry(String stageId, String key, Object value, Class<?> type) {
        super(key, value, type);
		this.stageId = stageId;
    }

    public EDBStageObjectEntry(String stageId, String key, Object value, String type) {
        super(key, value, type);
		this.stageId = stageId;
    }
	
	public String getStageId(){
		return this.stageId;
	}
	
	public void setStageId(String stageId){
		this.stageId = stageId;
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(getClass()).add("stageId", stageId).add("key", this.getKey()).add("value", this.getValue()).add("type", this.getType()).toString();
	}
}
