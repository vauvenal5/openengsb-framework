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

package org.openengsb.core.ekb.persistence.query.edb.internal;

import org.openengsb.core.api.model.CommitMetaInfo;
import org.openengsb.core.api.model.CommitQueryRequest;
import org.openengsb.core.api.model.ModelDescription;
import org.openengsb.core.api.model.QueryRequest;
import org.openengsb.core.edb.api.*;
import org.openengsb.core.ekb.api.*;
import org.openengsb.core.ekb.common.EDBConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the QueryInterface service. It's main responsibilities are the loading of elements from the EDB and
 * converting them to the correct format.
 */
public class QueryInterfaceService implements QueryInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryInterfaceService.class);
    private EngineeringDatabaseService edbService;
    private EDBConverter edbConverter;
    private ModelRegistry modelRegistry;
    private List<QueryParser> queryParsers;

    @Override
    public <T> T getModel(Class<T> model, String oid) {
        return this.getModel(model, oid, null);
    }

    @Override
    public <T> T getModel(Class<T> model, String oid, String stageId) {
        LOGGER.debug("Invoked getModel with the model {} and the oid {}", model.getName(), oid);
        EDBObject object = edbService.getObject(oid, stageId);
        return (T) edbConverter.convertEDBObjectToModel(model, object);
    }

    @Override
    public <T> List<T> getModelHistory(Class<T> model, String oid) {
        return this.getModelHistory(model, oid, null);
    }

    @Override
    public <T> List<T> getModelHistory(Class<T> model, String oid, String stageId) {
        LOGGER.debug("Invoked getModelHistory with the model {} and the oid {}", model.getName(), oid);
        return (List<T>) edbConverter.convertEDBObjectsToModelObjects(model, edbService.getHistory(oid, stageId));
    }

    @Override
    public <T> List<T> getModelHistoryForTimeRange(Class<T> model, String oid, Long from, Long to) {
        return this.getModelHistoryForTimeRange(model, oid, from, to, null);
    }

    @Override
    public <T> List<T> getModelHistoryForTimeRange(Class<T> model, String oid, Long from, Long to, String stageId) {
        LOGGER.debug("Invoked getModelHistoryForTimeRange with the model {} and the oid {} for the "
                + "time period of {} to {}", new Object[]{ model.getName(), oid, new Date(from).toString(),
                new Date(to).toString() });
        return (List<T>) edbConverter.convertEDBObjectsToModelObjects(model,
                edbService.getHistoryForTimeRange(oid, from, to, stageId));
    }

    @Override
    public <T> List<T> query(Class<T> model, QueryRequest request) {
       return this.query(model, request, null);
    }

    @Override
    public <T> List<T> query(Class<T> model, QueryRequest request, String stageId) {
        LOGGER.debug("Query for model {} with the request {}", model.getName(), request);
        request.addParameter(EDBConstants.MODEL_TYPE, model.getName());
        return (List<T>) edbConverter.convertEDBObjectsToModelObjects(model, edbService.query(request, stageId));
    }

    @Override
    public <T> List<T> queryByString(Class<T> model, String query) {
        return this.queryByString(model, query, null);
    }

    @Override
    public <T> List<T> queryByString(Class<T> model, String query, String stageId) {
        return query(model, parseQueryString(query), stageId);
    }

    @Override
    public <T> List<T> queryByStringAndTimestamp(Class<T> model, String query, String timestamp) {
        return this.queryByStringAndTimestamp(model, query, timestamp, null);
    }

    @Override
    public <T> List<T> queryByStringAndTimestamp(Class<T> model, String query, String timestamp, String stageId) {
        Long time;
        if (timestamp == null || timestamp.isEmpty()) {
            LOGGER.debug("Got invalid timestamp string. Use the current timestamp instead");
            time = System.currentTimeMillis();
        } else {
            time = Long.parseLong(timestamp);
        }
        QueryRequest request = parseQueryString(query);
        request.setTimestamp(time);
        return query(model, request, stageId);
    }

    @Override
    public QueryRequest parseQueryString(String query) throws EKBException {
        if (query.isEmpty()) {
            return QueryRequest.create();
        }
        for (QueryParser parser : queryParsers) {
            if (parser.isParsingPossible(query)) {
                return parser.parseQueryString(query);
            }
        }
        throw new EKBException("There is no active parser which is able to parse the query string " + query);
    }

    @Override
    public <T> List<T> queryForActiveModels(Class<T> model) {
        return this.queryForActiveModels(model, null);
    }

    @Override
    public <T> List<T> queryForActiveModels(Class<T> model, String stageId) {
        LOGGER.debug("Invoked queryForActiveModels with the model {}", model.getName());
        return query(model, QueryRequest.create(), stageId);
    }

    @Override
    public UUID getCurrentRevisionNumber() {
        return edbService.getCurrentRevisionNumber();
    }

    @Override
    public UUID getCurrentRevisionNumber(String stageId) {
        return edbService.getCurrentRevisionNumber(stageId);
    }

    @Override
    public UUID getLastRevisionNumberOfContext(String contextId) {
        return this.getLastRevisionNumberOfContext(contextId, null);
    }

    @Override
    public UUID getLastRevisionNumberOfContext(String contextId, String stageId) {
        return edbService.getLastRevisionNumberOfContext(contextId, stageId);
    }

    @Override
    public List<CommitMetaInfo> queryForCommits(CommitQueryRequest request) throws EKBException {
        return this.queryForCommits(request, null);
    }

    @Override
    public List<CommitMetaInfo> queryForCommits(CommitQueryRequest request, String stageId) throws EKBException {
        return edbService.getRevisionsOfMatchingCommits(request, stageId);
    }

    @Override
    public EKBCommit loadCommit(String revision) throws EKBException {
        return this.loadCommit(revision, null);
    }

    @Override
    public EKBCommit loadCommit(String revision, String stageId) throws EKBException {
        try {
            EDBCommit commit = edbService.getCommitByRevision(revision, stageId);
            return convertEDBCommitToEKBCommit(commit);
        } catch (EDBException e) {
            throw new EKBException("There is no commit with the revision " + revision);
        }
    }

    /**
     * Converts an EDBCommit object into an EKBCommit object.
     */
    private EKBCommit convertEDBCommitToEKBCommit(EDBCommit commit) throws EKBException {
        EKBCommit result = new EKBCommit();
        Map<ModelDescription, Class<?>> cache = new HashMap<>();
        result.setRevisionNumber(commit.getRevisionNumber());
        result.setComment(commit.getComment());
        result.setParentRevisionNumber(commit.getParentRevisionNumber());
        result.setDomainId(commit.getDomainId());
        result.setConnectorId(commit.getConnectorId());
        result.setInstanceId(commit.getInstanceId());

        EDBStage stage = commit.getEDBStage();
        if(stage != null)
        {
            result.setStageId(stage.getStageId());
        }

        for (EDBObject insert : commit.getInserts()) {
            result.addInsert(createModelOfEDBObject(insert, cache));
        }
        for (EDBObject update : commit.getUpdates()) {
            result.addUpdate(createModelOfEDBObject(update, cache));
        }
        for (String delete : commit.getDeletions()) {
            EDBObject object = edbService.getObject(delete, commit.getTimestamp(), result.getStageId());
            result.addDelete(createModelOfEDBObject(object, cache));
        }
        return result;
    }

    /**
     * Converts an EDBObject instance into a model. For this, the method need to retrieve the model class to be able to
     * instantiate the corresponding model objects. If the conversion fails, null is returned.
     */
    private Object createModelOfEDBObject(EDBObject object, Map<ModelDescription, Class<?>> cache) {
        try {
            ModelDescription description = getDescriptionFromObject(object);
            Class<?> modelClass;
            if (cache.containsKey(description)) {
                modelClass = cache.get(description);
            } else {
                modelClass = modelRegistry.loadModel(description);
                cache.put(description, modelClass);
            }
            return edbConverter.convertEDBObjectToModel(modelClass, object);
        } catch (IllegalArgumentException | ClassNotFoundException e) {
            LOGGER.warn("Unable to create model of the object {}", object.getOID(), e);
            return null;
        }
    }

    /**
     * Extracts the required values to lookup a model class from the given EDBObject. If this object does not contain
     * the required information, an IllegalArgumentException is thrown.
     */
    private ModelDescription getDescriptionFromObject(EDBObject obj) {
        String modelName = obj.getString(EDBConstants.MODEL_TYPE);
        String modelVersion = obj.getString(EDBConstants.MODEL_TYPE_VERSION);
        if (modelName == null || modelVersion == null) {
            throw new IllegalArgumentException("The object " + obj.getOID() + " contains no model information");
        }
        return new ModelDescription(modelName, modelVersion);
    }

    public void setEdbService(EngineeringDatabaseService edbService) {
        this.edbService = edbService;
    }

    public void setEdbConverter(EDBConverter edbConverter) {
        this.edbConverter = edbConverter;
    }

    public void setModelRegistry(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
    }
    
    public void setQueryParsers(List<QueryParser> queryParsers) {
        this.queryParsers = queryParsers;
    }
}
