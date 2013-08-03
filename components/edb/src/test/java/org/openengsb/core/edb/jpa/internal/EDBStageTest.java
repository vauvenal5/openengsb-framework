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

import java.util.Arrays;
import java.util.Date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.openengsb.core.edb.api.EDBStage;
import org.openengsb.core.edb.api.EDBStageCommit;
import org.openengsb.core.edb.api.EDBStageObject;
import static org.hamcrest.Matchers.notNullValue;

public class EDBStageTest extends AbstractEDBTest
{
	
	public void testPersistStageObject_shouldWork()
	{
		String creator = "Svetoslav";
		String stageId = "stagePersistenceTest";
		Long dateTime = (new Date()).getTime();
		
		String objectId = "stagedInsertedObject";
		
		EDBStage stage = new JPAStage();
		stage.setCreator(creator);
		stage.setStageId(stageId);
		stage.setTimeStamp(dateTime);
		
		EDBStageCommit commit = this.getEDBStageCommit();
		commit.setStage(stage);
		
		EDBStageObject object = new EDBStageObject(stageId, objectId);
		
		commit.insert(object);
		
		//db.commit(commit);
		
		//todo: take closer look on EDBService
		assertThat(false, is(true));
	}
	
	@Test
    public void testCommit_shouldWork() throws Exception {
		JPAStage stage = new JPAStage();
		stage.setStageId("stage1");
		stage.setCreator("sveti");
		stage.setTimeStamp(Long.MIN_VALUE);
        EDBStageObject obj = new EDBStageObject("stage1","Tester");
        obj.putEntry("Test", "Hooray");
        EDBStageCommit ci = db.createEDBStageCommit(stage, Arrays.asList(obj), null, null);
        long time = db.commit(ci);

        obj = null;
        obj = db.getStagedObject("Tester", "stage1");
        String hooray = obj.getString("Test");

        assertThat(obj, notNullValue());
        assertThat(hooray, notNullValue());

        checkTimeStamps(Arrays.asList(time));
    }
}
