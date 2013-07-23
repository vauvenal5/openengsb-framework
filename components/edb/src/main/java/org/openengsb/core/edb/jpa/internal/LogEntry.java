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

import org.openengsb.core.edb.api.EDBCommit;
import org.openengsb.core.edb.api.EDBLogEntry;
import org.openengsb.core.edb.api.EDBObject;
import org.openengsb.core.edb.api.EDBStageCommit;
import org.openengsb.core.edb.api.EDBStageObject;

public class LogEntry<C extends EDBCommit, O extends EDBObject> implements EDBLogEntry<C, O> {
    private C commit;
    private O object;

    public LogEntry(C c, O obj) {
        this.commit = c;
        this.object = obj;
    }

    @Override
    public C getCommit() {
        return commit;
    }

    @Override
    public O getObject() {
        return object;
    }
}
