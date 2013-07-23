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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openengsb.core.api.context.ContextHolder;
import org.openengsb.core.api.security.AuthenticationContext;
import org.openengsb.core.edb.api.EDBCommit;
import org.openengsb.core.edb.api.EDBDiff;
import org.openengsb.core.edb.api.EDBException;
import org.openengsb.core.edb.api.EDBLogEntry;
import org.openengsb.core.edb.api.EDBObject;
import org.openengsb.core.edb.api.EDBStageCommit;
import org.openengsb.core.edb.api.EDBStageObject;
import org.openengsb.core.edb.api.hooks.EDBBeginCommitHook;
import org.openengsb.core.edb.api.hooks.EDBErrorHook;
import org.openengsb.core.edb.api.hooks.EDBPostCommitHook;
import org.openengsb.core.edb.api.hooks.EDBPreCommitHook;
import org.openengsb.core.edb.jpa.internal.dao.JPADao;
import org.openengsb.core.edb.jpa.internal.util.EDBUtils;

/**
 * The implementation of the EngineeringDatabaseService, extending the AbstractEDBService
 */
public class EDBService extends AbstractEDBService {
    private JPADao dao;
    private AuthenticationContext authenticationContext;
    
    public EDBService(JPADao dao, AuthenticationContext authenticationContext,
            List<EDBBeginCommitHook> beginCommitHooks, List<EDBPreCommitHook> preCommitHooks,
            List<EDBPostCommitHook> postCommitHooks, List<EDBErrorHook> errorHooks,
            Boolean revisionCheckEnabled) {
        super(beginCommitHooks, preCommitHooks, postCommitHooks, errorHooks, revisionCheckEnabled, EDBService.class);
        this.dao = dao;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public Long commit(EDBCommit commit) throws EDBException {
        return performCommitLogic(commit);
    }    

    /**
     * Only here for the TestEDBService where there is a real implementation for this method.
     */
    protected void beginTransaction() {
    }

    /**
     * Only here for the TestEDBService where there is a real implementation for this method.
     */
    protected void commitTransaction() {
    }
    
    /**
     * Only here for the TestEDBService where there is a real implementation for this method.
     */
    protected void rollbackTransaction() {
    }
    
    @Override
    public EDBObject getObject(String oid) throws EDBException {
        getLogger().debug("loading newest JPAObject with the oid {}", oid);
        JPAObject temp = dao.getJPAObject(oid);
        return EDBUtils.convertJPAObjectToEDBObject(temp);
    }
    
    @Override 
    public EDBStageObject getStagedObject(String oid, String sid) throws EDBException {
        getLogger().debug("loading newest JPAObject with the oid {} and sid {}", new Object[]{oid, sid});
        JPAStageObject temp = dao.getStagedJPAObject(oid,sid);
        return EDBUtils.convertJPAStageObjectToEDBStageObject(temp);
    }

    @Override
    public List<EDBObject> getObjects(List<String> oids) throws EDBException {
        List<JPAObject> objects = dao.getJPAObjects(oids);
        return EDBUtils.convertJPAObjectsToEDBObjects(objects);
    }
    
    @Override
    public List<EDBStageObject> getStagedObjects(List<String> oids, String sid) throws EDBException {
        List<JPAStageObject> objects = dao.getStagedJPAObjects(oids, sid);
        return EDBUtils.convertJPAStageObjectsToEDBStageObjects(objects);
    }

    @Override
    public List<EDBObject> getHistory(String oid) throws EDBException {
        getLogger().debug("loading history of JPAObject with the oid {}", oid);
        List<JPAObject> objects = dao.getJPAObjectHistory(oid);
        return EDBUtils.convertJPAObjectsToEDBObjects(objects);
    }
    
    @Override
    public List<EDBStageObject> getStagedHistory(String oid, String sid) throws EDBException {
        getLogger().debug("loading history of JPAStageObject with the oid {} and sid {}", new Object[]{oid,sid});
        List<JPAStageObject> objects = dao.getStagedJPAObjectHistory(oid, sid);
        return EDBUtils.convertJPAStageObjectsToEDBStageObjects(objects);
    }

    @Override
    public List<EDBObject> getHistoryForTimeRange(String oid, Long from, Long to) throws EDBException {
        getLogger().debug("loading JPAObject with the oid {} from "
                + "the timestamp {} to the timestamp {}", new Object[]{ oid, from, to });
        List<JPAObject> objects = dao.getJPAObjectHistory(oid, from, to);
        return EDBUtils.convertJPAObjectsToEDBObjects(objects);
    }
    
    @Override
    public List<EDBStageObject> getStagedHistoryForTimeRange(String oid, String sid, Long from, Long to) throws EDBException {
         getLogger().debug("loading JPAObject with the oid {} and sid {} from "
                + "the timestamp {} to the timestamp {}", new Object[]{ oid, sid, from, to });
        List<JPAStageObject> objects = dao.getStagedJPAObjectHistory(oid, sid, from, to);
        return EDBUtils.convertJPAStageObjectsToEDBStageObjects(objects);
    }
    
    @Override
    public List<EDBLogEntry> getLog(String oid, Long from, Long to) throws EDBException {
        getLogger().debug("loading the log of JPAObject with the oid {} from "
                + "the timestamp {} to the timestamp {}", new Object[]{ oid, from, to });
        List<EDBObject> history = getHistoryForTimeRange(oid, from, to);
        List<JPACommit> commits = dao.getJPACommit(oid, from, to);
        if (history.size() != commits.size()) {
            throw new EDBException("inconsistent log " + Integer.toString(commits.size()) + " commits for "
                    + Integer.toString(history.size()) + " history entries");
        }
        List<EDBLogEntry> log = new ArrayList<EDBLogEntry>();
        for (int i = 0; i < history.size(); ++i) {
            log.add(new LogEntry(commits.get(i), history.get(i)));
        }
        return log;
    }
    
    @Override
    public List<EDBLogEntry> getLog(String oid, String sid, Long from, Long to) throws EDBException{
        //TODO paul
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EDBObject> getHead() throws EDBException {
        return dao.getJPAHead(System.currentTimeMillis()).getEDBObjects();
    }
    
    @Override
    public List<EDBStageObject> getStageHead(String sid) throws EDBException {
        throw new UnsupportedOperationException();
        
        //return dao.getStagedJPAHead(System.currentTimeMillis(), sid).getStagedEDBObjects();
    }

    @Override
    public List<EDBObject> getHead(long timestamp) throws EDBException {
        getLogger().debug("load the elements of the JPAHead with the timestamp {}", timestamp);
        JPAHead head = dao.getJPAHead(timestamp);
        if (head != null) {
            return head.getEDBObjects();
        }
        throw new EDBException("Failed to get head for timestamp " + Long.toString(timestamp));
    }
    
    @Override
    public List<EDBStageObject> getStagedHead(String sid, long timestamp) throws EDBException {
        throw new UnsupportedOperationException();
        /*
        getLogger().debug("load the elements of the JPAHead from stage {} with the timestamp {}", new Object[] {sid, timestamp});
        JPAHead head = dao.getStagedJPAHead(timestamp, sid);
        if (head != null) {
            return head.getStagedEDBObjects();
        }
        throw new EDBException("Failed to get head for timestamp " + Long.toString(timestamp)); 
        */
    }

    @Override
    public List<EDBObject> queryByKeyValue(String key, Object value) throws EDBException {
        getLogger().debug("query for objects with key = {} and value = {}", key, value);
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return queryByMap(queryMap);
    }
    
    @Override
    public List<EDBStageObject> queryStageByKeyValue(String sid, String key, Object value) throws EDBException{
        getLogger().debug("query for objects with sid {} and key = {} and value = {}", new Object[] {sid, key, value});
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return queryStageByMap(sid, queryMap);
    }

    @Override
    public List<EDBObject> queryByMap(Map<String, Object> queryMap) throws EDBException {
        try {
            return EDBUtils.convertJPAObjectsToEDBObjects(dao.query(queryMap));
        } catch (Exception ex) {
            throw new EDBException("failed to query for objects with the given map", ex);
        }
    }
    
    @Override
    public List<EDBStageObject> queryStageByMap(String sid, Map<String, Object> query) throws EDBException {
        try {
            return EDBUtils.convertJPAStageObjectsToEDBStageObjects(dao.queryStaged(query, sid));
        } catch (Exception ex) {
            throw new EDBException("failed to query for objects with given map and stage", ex);
        }
    }

    @Override
    public List<EDBObject> query(Map<String, Object> queryMap, Long timestamp) throws EDBException {
        try {
            return EDBUtils.convertJPAObjectsToEDBObjects(dao.query(queryMap, timestamp));
        } catch (Exception ex) {
            throw new EDBException("failed to query for objects with the given map", ex);
        }
    }
    
    @Override
    public List<EDBStageObject> queryStage(String sid, Map<String, Object> query, Long timestamp) throws EDBException {
        try{
            return EDBUtils.convertJPAStageObjectsToEDBStageObjects(dao.queryStaged(query, sid, timestamp));
         } catch (Exception ex) {
            throw new EDBException("failed to query for objects with the given map, stage, timestamp", ex);
        }
    }

    @Override
    public List<EDBCommit> getCommitsByKeyValue(String key, Object value) throws EDBException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return getCommits(queryMap);
    }
    
    @Override
    public List<EDBStageCommit> getStagedCommitsByKeyValue(String key, Object value, String sid) throws EDBException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return getStagedCommits(queryMap, sid);
    }

    @Override
    public List<EDBCommit> getCommits(Map<String, Object> queryMap) throws EDBException {
        List<JPACommit> commits = dao.getCommits(queryMap);
        return new ArrayList<EDBCommit>(commits);
    }
    
    @Override
    public List<EDBStageCommit> getStagedCommits(Map<String, Object> query, String sid) throws EDBException {
        List<JPAStageCommit> commits = dao.getStagedCommits(query, sid);
        return new ArrayList<EDBStageCommit>(commits);
    }

    @Override
    public JPACommit getLastCommitByKeyValue(String key, Object value) throws EDBException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return getLastCommit(queryMap);
    }
    
    @Override 
    public JPAStageCommit getLastStagedCommitByKeyValue(String key, Object value, String sid) throws EDBException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put(key, value);
        return getLastStagedCommit(queryMap, sid);
    }

    @Override
    public JPACommit getLastCommit(Map<String, Object> queryMap) throws EDBException {
        JPACommit result = dao.getLastCommit(queryMap);
        return result;
    }
    
    @Override
    public JPAStageCommit getLastStagedCommit(Map<String, Object> query, String sid) throws EDBException {
        JPAStageCommit result = dao.getLastStagedCommit(query, sid);
        return result;
    }

    
    @Override
    public UUID getCurrentRevisionNumber() throws EDBException {
        try {
            return getCommit(System.currentTimeMillis()).getRevisionNumber();
        } catch (EDBException e) {
            getLogger().debug("There was no commit so far, so the current revision number is null");
            return null;
        }
    }
    
    @Override
    public UUID getStagedCurrentRevisionNumber(String sid) throws EDBException {
        try {
            return getStagedCommit(System.currentTimeMillis(), sid).getRevisionNumber();
        } catch (EDBException e) {
            getLogger().debug("There was no commit so far, so the current revision number is null");
            return null;
        }
    }

    @Override
    public JPACommit getCommit(Long from) throws EDBException {
        List<JPACommit> commits = dao.getJPACommit(from);
        if (commits == null || commits.size() == 0) {
            throw new EDBException("there is no commit for this timestamp");
        } else if (commits.size() > 1) {
            throw new EDBException("there are more than one commit for one timestamp");
        }
        return commits.get(0);
    }
    
    @Override
    public JPAStageCommit getStagedCommit(Long from, String sid) throws EDBException {
        List<JPAStageCommit> commits = dao.getStagedJPACommit(from, sid);
        if (commits == null || commits.size() == 0) {
            throw new EDBException("there is no commit for this timestamp and/or stage");
        } else if (commits.size() > 1) {
            throw new EDBException("there are more than one commit for one timestamp and/or stage");
        }
        return commits.get(0);
    }

    @Override
    public Diff getDiff(Long firstTimestamp, Long secondTimestamp) throws EDBException {
        List<EDBObject> headA = getHead(firstTimestamp);
        List<EDBObject> headB = getHead(secondTimestamp);

        return new Diff(getCommit(firstTimestamp), getCommit(secondTimestamp), headA, headB);
    }
    
    @Override
    public Diff getStagedDiff(Long firstTimestamp, Long secondTimestamp, String sid1, String sid2) throws EDBException {
        List<EDBStageObject> headA = getStagedHead(sid1, firstTimestamp);
        List<EDBStageObject> headB = getStagedHead(sid2, secondTimestamp);
        
        throw new UnsupportedOperationException();
        //return new Diff(getCommit())
    }

    @Override
    public List<String> getResurrectedOIDs() throws EDBException {
        return dao.getResurrectedOIDs();
    }
    
    @Override
    public List<String> getStagedResurrectedOIDs(String sid) throws EDBException {
        return dao.getStagedResurrectedOIDs(sid);
    }

    @Override
    public List<EDBObject> getStateOfLastCommitMatching(Map<String, Object> queryMap) throws EDBException {
        JPACommit ci = getLastCommit(queryMap);
        return getHead(ci.getTimestamp());
    }
    
    @Override
    public List<EDBStageObject> getStagedStateOfLastCommitMatching(Map<String, Object> query, String sid) throws EDBException {
        throw new UnsupportedOperationException();
        //JPAStageCommit ci = getLastStagedCommit(query, sid);
        //return getHead(ci.getTimestamp());    
    }

    @Override
    public List<EDBObject> getStateOfLastCommitMatchingByKeyValue(String key, Object value) throws EDBException {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(key, value);
        return getStateOfLastCommitMatching(query);
    }
    
    @Override
    public List<EDBStageObject> getStagedStateOfLastCommitMatchingByKeyValue(String key, Object value, String sid) throws EDBException {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(key, value);
        return getStagedStateOfLastCommitMatchingByKeyValue(key, value, sid);
    }

    @Override
    public EDBCommit createEDBCommit(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes)
        throws EDBException {
        String committer = getAuthenticatedUser();
        String contextId = getActualContextId();
        JPACommit commit = new JPACommit(committer, contextId);
        getLogger().debug("creating commit for committer {} with contextId {}", committer, contextId);
        commit.insertAll(inserts);
        commit.updateAll(updates);
        commit.deleteAll(deletes);
        commit.setHeadRevisionNumber(getCurrentRevisionNumber());
        return commit;
    }
	
	@Override
	public EDBStageCommit createEDBStageCommit(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes)
		throws EDBException {
		String committer = getAuthenticatedUser();
        String contextId = getActualContextId();
        JPAStageCommit commit = new JPAStageCommit(committer, contextId);
        getLogger().debug("creating staged commit for committer {} with contextId {}", committer, contextId);
        commit.insertAll(inserts);
        commit.updateAll(updates);
        commit.deleteAll(deletes);
        commit.setHeadRevisionNumber(getCurrentRevisionNumber());
        return commit;
	}

    /**
     * Returns the actual authenticated user.
     */
    private String getAuthenticatedUser() {
        return (String) authenticationContext.getAuthenticatedPrincipal();
    }

    /**
     * Returns the actual context id.
     */
    private String getActualContextId() {
        return ContextHolder.get().getCurrentContextId();
    }
}
