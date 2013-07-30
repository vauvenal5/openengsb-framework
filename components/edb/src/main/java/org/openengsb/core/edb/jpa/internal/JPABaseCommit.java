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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.openengsb.core.edb.api.EDBBaseCommit;
import org.openengsb.core.edb.api.EDBBaseObject;
import org.openengsb.core.edb.api.EDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class JPABaseCommit<T extends EDBBaseObject> extends VersionedEntity implements EDBBaseCommit<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JPABaseCommit.class);

    @Column(name = "COMMITER", length = 50)
    private String committer;
    @Column(name = "TIME")
    private Long timestamp;
    @Column(name = "CONTEXT", length = 50)
    private String context;
    @Column(name = "DELS")
    @ElementCollection
    private List<String> deletions;
    @Column(name = "OIDS")
    @ElementCollection
    private List<String> oids;
    @Column(name = "ISCOMMITED")
    private Boolean committed = false;
    @Column(name = "REVISION")
    private String revision;
    @Column(name = "PARENT")
    private String parent;

    private List<T> objects;

    @Transient
    private List<T> inserts;
    @Transient
    private List<T> updates;
    
    /**
     * the empty constructor is only for the jpa enhancer. Do not use it in real code.
     */
    @Deprecated
    protected JPABaseCommit() {
    }

    protected JPABaseCommit(String committer, String contextId) {
        this.committer = committer;
        this.context = contextId;

        oids = new ArrayList<String>();
        deletions = new ArrayList<String>();
        inserts = new ArrayList<T>();
        updates = new ArrayList<T>();
        this.revision = UUID.randomUUID().toString();
    }

    @Override
    public void setCommitted(Boolean committed) {
        this.committed = committed;
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public List<String> getOIDs() {
        fillOIDs();
        return oids;
    }

    public final List<T> getObjects() {
        List<T> objects = new ArrayList<T>();
        objects.addAll(inserts);
        objects.addAll(updates);
        return objects;
    }

    @Override
    public final List<String> getDeletions() {
        return deletions;
    }

    @Override
    public final String getCommitter() {
        return committer;
    }

    @Override
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public final Long getTimestamp() {
        return timestamp;
    }

    @Override
    public final String getContextId() {
        return context;
    }

    @Override
    public void delete(String oid) throws EDBException {
        if (deletions.contains(oid)) {
            LOGGER.debug("could not delete object {} because it was never added", oid);
            return;
        }
        deletions.add(oid);
        LOGGER.debug("deleted object {} from the commit", oid);
    }
    
    public void deleteAll(List<T> objects) throws EDBException {
        if (objects != null) {
            for (T object : objects) {
                delete(object.getOID());
            }
        }
    }

    private void fillOIDs() {
        if (oids == null) {
            oids = new ArrayList<String>();
        } else {
            oids.clear();
        }
        for (T o : objects) {
            oids.add(o.getOID());
        }
    }

    @Override
    public void insert(T obj) throws EDBException {
        if (!inserts.contains(obj)) {
            inserts.add(obj);
            LOGGER.debug("Added object {} to the commit for inserting", obj.getOID());
        }
    }
    
    public void insertAll(List<T> objects) throws EDBException {
        if (objects != null) {
            for (T object : objects) {
                insert(object);
            }
        }
    }

    @Override
    public void update(T obj) throws EDBException {
        if (!updates.contains(obj)) {
            updates.add(obj);
            LOGGER.debug("Added object {} to the commit for updating", obj.getOID());
        }
    }
    
    public void updateAll(List<T> objects) throws EDBException {
        if (objects != null) {
            for (T object : objects) {
                update(object);
            }
        }
    }

    @Override
    public List<T> getInserts() {
        return inserts;
    }

    @Override
    public List<T> getUpdates() {
        return updates;
    }

    @Override
    public UUID getParentRevisionNumber() {
        return parent != null ? UUID.fromString(parent) : null;
    }

    @Override
    public UUID getRevisionNumber() {
        return revision != null ? UUID.fromString(revision) : null;
    }

    @Override
    public void setHeadRevisionNumber(UUID head) {
        this.parent = head != null ? head.toString() : null;
    }
}
