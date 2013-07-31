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
package org.openengsb.core.edb.jpa.internal;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class JPAStageObject extends JPABaseObject<JPAStageEntry> {
	@Column(name="STAGEID")
	private String stageId;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<JPAStageEntry> entries;
	
	public JPAStageObject() {
		super();
	}
	
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	
	public String getStageId() {
		return this.stageId;
	}

	@Override
	public List<JPAStageEntry> getEntries()
	{
		return this.entries;
	}

	@Override
	public void setEntries(List<JPAStageEntry> entries)
	{
		this.entries = entries;
	}
}
