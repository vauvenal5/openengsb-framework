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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import org.openengsb.core.edb.api.EDBStage;
import org.openengsb.core.edb.api.EDBStageCommit;
import javax.persistence.ManyToOne;
import org.openengsb.core.edb.api.EDBException;
import org.openengsb.core.edb.api.EDBObject;
import org.openengsb.core.edb.api.EDBStageObject;

@Entity
public class JPAStageCommit extends JPABaseCommit<EDBStageObject> implements EDBStageCommit {

	@ManyToOne(fetch= FetchType.LAZY)
	private EDBStage stage;

	/**
     * the empty constructor is only for the jpa enhancer. Do not use it in real code.
     */
    @Deprecated
	public JPAStageCommit()
	{
	}
	
	public JPAStageCommit(String committer, String contextId) {
		super(committer, contextId);
	}
	
	@Override
	public EDBStage getStage() {
		return this.stage;
	}

	@Override
	public void setStage(EDBStage stage) {
		this.stage = stage;
	}
}