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
import java.util.Map;
import java.util.UUID;
import org.openengsb.core.api.model.CommitMetaInfo;
import org.openengsb.core.api.model.CommitQueryRequest;
import org.openengsb.core.api.model.QueryRequest;
import org.openengsb.core.edb.api.EDBCommit;
import org.openengsb.core.edb.api.EDBLogEntry;
import org.openengsb.core.edb.api.EDBObject;
import org.openengsb.core.edb.api.EDBObjectEntry;
import org.openengsb.core.edb.api.EDBStage;

public interface EDBTestDataGenerator {

    EDBCommit createEDBCommit(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes);

    EDBObject getEDBObject(String oid);

    EDBObject createEDBObject(String oid, Map<String, EDBObjectEntry> data);

    EDBObject createEDBObject(String oid);

    void assertStage(EDBStage actual);

    List<EDBCommit> getCommitsByKeyValue(String key, Object value);

    List<EDBObject> getHistory(String oid);

    List<EDBLogEntry> getLog(String oid, Long from, Long to);

    Diff getDiff(Long firstTimestamp, Long secondTimestamp);

    List<String> getResurrectedOIDs();

    List<EDBObject> query(QueryRequest request);

    EDBCommit getCommitByRevision(String revision);

    List<CommitMetaInfo> getRevisionsOfMatchingCommits(CommitQueryRequest request);

    UUID getLastRevisionNumberOfContext(String contextId);
}