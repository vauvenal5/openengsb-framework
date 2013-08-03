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

package org.openengsb.core.edb.jpa.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openengsb.core.edb.api.EDBBaseObject;

import org.openengsb.core.edb.api.EDBObject;
import org.openengsb.core.edb.api.EDBObjectEntry;
import org.openengsb.core.edb.jpa.internal.JPABaseObject;
import org.openengsb.core.edb.jpa.internal.JPAEntry;
import org.openengsb.core.edb.jpa.internal.JPAObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The EDBUtils class contains functions needed in the whole EDB implementation.
 */
public final class EDBUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDBUtils.class);
    private static List<EDBConverterStep> steps = new ArrayList<EDBConverterStep>(Arrays.asList(
        new StringConverterStep(), new DateConverterStep(), new DefaultConverterStep()));

    private EDBUtils() {
    }

    /**
     * Converts a JPAEntry object into an EDBObjectEntry element. If there is a problem with the instantiation of the
     * type of the JPAEntry, the simple string object will be written in the resulting element. To instantiate the type
     * first the static method "valueOf" of the type will be tried. If that didn't work, then the constructor of the
     * object with a string parameter is used. If that didn't work either, the simple string will be set in the entry.
     */
    /*public static EDBObjectEntry convertJPAEntryToEDBObjectEntry(JPAEntry entry) {
        for (EDBConverterStep step : steps) {
            if (step.doesStepFit(entry.getType())) {
                LOGGER.debug("EDBConverterStep {} fit for type {}", step.getClass().getName(), entry.getType());
                return step.convertToEDBObjectEntry(entry);
            }
        }
        LOGGER.error("No EDBConverterStep fit for JPAEntry {}", entry);
        return null;
    }*/
	
	public static <E extends EDBObjectEntry, J extends JPAEntry> E convertJPAEntryToEDBObjectEntry(J entry) {
		for (EDBConverterStep step : steps) {
            if (step.doesStepFit(entry.getType())) {
                LOGGER.debug("EDBConverterStep {} fit for type {}", step.getClass().getName(), entry.getType());
                return (E)step.convertToEDBObjectEntry(entry);
            }
        }
        LOGGER.error("No EDBConverterStep fit for JPAEntry {}", entry);
        return null;
	}

    /**
     * Converts a JPAEntry object into an EDBObjectEntry.
     */
    /*public static JPAEntry convertEDBObjectEntryToJPAEntry(EDBObjectEntry entry) {
        for (EDBConverterStep step : steps) {
            if (step.doesStepFit(entry.getType())) {
                LOGGER.debug("EDBConverterStep {} fit for type {}", step.getClass().getName(), entry.getType());
                return step.convertToJPAEntry(entry);
            }
        }
        LOGGER.error("No EDBConverterStep fit for EDBObjectEntry {}", entry);
        return null;
    }*/
	
	public static <E extends EDBObjectEntry, J extends JPAEntry> J convertEDBObjectEntryToJPAEntry(E entry) {
		for (EDBConverterStep step : steps) {
            if (step.doesStepFit(entry.getType())) {
                LOGGER.debug("EDBConverterStep {} fit for type {}", step.getClass().getName(), entry.getType());
                return (J)step.convertToJPAEntry(entry);
            }
        }
        LOGGER.error("No EDBConverterStep fit for EDBObjectEntry {}", entry);
        return null;
	}
	
	private static List<JPAEntry> convertEDBObjectEntriesToJPAEntries(EDBObject object){
		List<JPAEntry> entries = new ArrayList<JPAEntry>();
		for (EDBObjectEntry entry : object.values()) {
            entries.add(convertEDBObjectEntryToJPAEntry(entry));
        }
		return entries;
	}
	
//	private static List<JPAStageEntry> convertEDBObjectEntriesToJPAEntries(EDBStageObject object){
//		List<JPAStageEntry> entries = new ArrayList<JPAStageEntry>();
//		for (EDBStageObjectEntry entry : object.values()) {
//            entries.add(EDBUtils.<EDBStageObjectEntry, JPAStageEntry>convertEDBObjectEntryToJPAEntry(entry));
//        }
//		return entries;
//	}

    /**
     * Converts a JPAObject object into an EDBObject.
     */
    
	private static EDBBaseObject convertJPAObjectToEDBObjectHelper(JPABaseObject object) {
        EDBBaseObject result = null;
		
		if(object instanceof JPAObject) {
			result = new EDBObject(object.getOID());
		 }
//		else {
//			result = new EDBStageObject(((JPAStageObject)object).getStageId(), object.getOID());
//		}
		
        for (JPAEntry kvp : (List<JPAEntry>)object.getEntries()) {
            EDBObjectEntry entry = convertJPAEntryToEDBObjectEntry(kvp);
            result.put(entry.getKey(), entry);
        }
        result.setDeleted(object.isDeleted());
        result.updateTimestamp(object.getTimestamp());
        return result;
    }
	
	public static EDBObject convertJPAObjectToEDBObject(JPAObject object) {
		return (EDBObject)convertJPAObjectToEDBObjectHelper(object);
	}
	
//	public static EDBStageObject convertJPAObjectToEDBObject(JPAStageObject object) {
//		return (EDBStageObject)convertJPAObjectToEDBObjectHelper(object);
//	}

	public static JPABaseObject convertEDBObjectToJPAObject(EDBBaseObject object) {
		JPABaseObject result = null;
		
		if(object instanceof EDBObject) {
			result = new JPAObject();
			result.setEntries(EDBUtils.convertEDBObjectEntriesToJPAEntries((EDBObject)object));
		}
//		else {
//			result = new JPAStageObject();
//			result.setEntries(EDBUtils.convertEDBObjectEntriesToJPAEntries((EDBStageObject)object));
//		}
		
        result.setTimestamp(object.getTimestamp());
        result.setOID(object.getOID());
        result.setDeleted(object.isDeleted());
		
        return result;
	}
	
    /**
     * Converts an EDBObject object into a JPAObject object.
     */
    /*public static JPAObject convertEDBObjectToJPAObject(EDBObject object) {
        return (JPAObject) EDBUtils.convertEDBObjectToJPAObjectHelper(object);
    }
	
	public static JPAStageObject convertEDBObjectToJPAObject(EDBStageObject object) {
        return (JPAStageObject) EDBUtils.convertEDBObjectToJPAObjectHelper(object);
    }*/

    /**
     * Converts a list of EDBObjects into a list of JPAObjects
     */
    public static List<JPAObject> convertEDBObjectsToJPAObjects(List<EDBObject> objects) {
        List<JPAObject> result = new ArrayList<JPAObject>();
        for (EDBBaseObject object : objects) {
            result.add((JPAObject)convertEDBObjectToJPAObject(object));
        }
        return result;
    }

    /**
     * Converts a list of JPAObjects into a list of EDBObjects
     */
    public static <E extends EDBBaseObject, J extends JPABaseObject> List<E> convertJPAObjectsToEDBObjects(List<J> objects) {
        List<E> result = new ArrayList<E>();
        for (J object : objects) {
            result.add((E)convertJPAObjectToEDBObjectHelper(object));
        }
        return result;
    }
}
