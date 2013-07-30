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

import java.util.List;
import java.util.UUID;

/**
 *
 * @author vauve_000
 */
public interface EDBBaseCommit<T extends EDBBaseObject>
{
	/**
     * Add an object to be inserted. The object's timestamp must match the commit's timestamp.
     */
    void insert(T obj) throws EDBException;
    
    /**
     * Add an object to be updated. The object's timestamp must match the commit's timestamp.
     */
    void update(T obj) throws EDBException;
	
	/**
     * Delete an object that already exists.
     */
    void delete(String oid) throws EDBException;

    /**
     * For a query-commit: Retrieve a list of OIDs representing the objects which have been changed by this commit.
     */
    List<String> getOIDs();

    /**
     * For a created commit: retrieve the list of all objects that have been inserted to this commit.
     */
    List<T> getInserts();
    
    /**
     * For a created commit: retrieve the list of all objects that have been updated to this commit.
     */
    List<T> getUpdates();
    
    /**
     * For a created commit: retrieve the list of all objects that should be inserted or updated with this commit.
     */
    List<T> getObjects();

    /**
     * For both, a created, or a queried commit: Retrieve a list of deleted OIDs.
     */
    List<String> getDeletions();

    /**
     * Get the committer's name.
     */
    String getCommitter();

    /**
     * Get the commit's timestamp.
     */
    Long getTimestamp();

    /**
     * Get the commit's context id.
     */
    String getContextId();

    /**
     * returns if this commit was already committed
     */
    boolean isCommitted();

    /**
     * sets if the commit is already committed, should be called by the EnterpriseDatabaseService at the commit
     * procedure
     */
    void setCommitted(Boolean committed);

    /**
     * this setter should be called by the EnterpriseDatabaseService at the commit procedure
     */
    void setTimestamp(Long timestamp);
    
    /**
     * Returns the revision number for the EDBCommit object.
     */
    UUID getRevisionNumber();
    
    /**
     * Returns the revision number of the parent of the EDBCommit object.
     */
    UUID getParentRevisionNumber();
    
    /**
     * Sets the revision number of the parent of the EDBCommit object.
     */
    void setHeadRevisionNumber(UUID revisionNumber);
}
