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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import org.apache.openjpa.persistence.jdbc.Index;


@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class JPABaseObject<T extends JPAEntry> extends VersionedEntity {
    
    @Column(name = "TIME")
    protected Long timestamp;
    @Column(name = "ISDELETED")
    protected Boolean isDeleted;
    @Index
    @Column(name = "OID")
    protected String oid;   
    
    
    protected JPABaseObject() {
        isDeleted = false;
    }
    
    public abstract List<T> getEntries();
    
    public abstract void setEntries(List<T> entries);
    
    public Boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.isDeleted = deleted;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOID() {
        return oid;
    }

    public void setOID(String oid) {
        this.oid = oid;
    }
    
    
}