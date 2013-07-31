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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;


/**
 * this defines a jpa object in the database. The correlation to the EDBObject is that
 * the JPAObject can be converted to an EDBObject through the EDBUtils class.
 */
@SuppressWarnings("serial")
@Entity
public class JPAObject extends JPABaseObject<JPAEntry> {
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<JPAEntry> entries;
	
    public JPAObject(){
        super();
    }
	
	@Override
	public List<JPAEntry> getEntries() {
        return entries;
    }
    
	@Override
    public void setEntries(List<JPAEntry> entries) {
        this.entries = entries;
    } 
}
