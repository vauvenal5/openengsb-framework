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
package org.openengsb.core.edb.jpa.internal;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.openengsb.core.edb.api.EDBStageObjectEntry;

@SuppressWarnings("serial")
@Entity
public class JPAStageEntry extends JPAEntry {
	@Column(name="STAGEID")
	private String stageId;
	
	public JPAStageEntry(){
		super();
		stageId = "";
	}
	
	public JPAStageEntry(EDBStageObjectEntry entry) {
		super(entry);
		this.stageId = entry.getStageId();
	}
	
	public String getStageId() {
		return this.stageId;
	}
	
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
}
