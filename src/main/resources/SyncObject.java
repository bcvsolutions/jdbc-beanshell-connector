/**
 * CzechIdM
 * Copyright (C) 2014 BCV solutions s.r.o., Czech Republic
 * 
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License 2.1 as published by the Free Software Foundation;
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
 * Boston, MA 02110-1301 USA
 * 
 * You can contact us on website http://www.bcvsolutions.eu.
 */

package eu.bcvsolutions.utils;


import org.identityconnectors.framework.common.objects.SyncDeltaType;

/**
 * Synchronizační objekt, které se používá pro přenos synchronizačních údajů mezi konektorem a příslušnými BeanShell skripty. 
 * 
 * @author Jaromír Mlejnek
 */
public class SyncObject implements Comparable<SyncObject> {

	private Object objId;	
	@SuppressWarnings("rawtypes")
	private Comparable timestamp;
	private String operationType;
	
	public SyncObject() {
	}
	
	@SuppressWarnings("rawtypes")
	public SyncObject(int objId, Comparable timestamp, String operationType) {
		this.objId = objId;
		this.timestamp = timestamp;
		this.operationType = operationType;
	}
	
	public Object getObjId() {
		return objId;
	}
	
	@SuppressWarnings("rawtypes")
	public Comparable getTimestamp() {
		return timestamp;
	}
	
	public String getOperationType() {
		return operationType;
	}
	
	/**
	 * Metoda navrací SyncDeltaType dle hodnoty členské proměnné "operationType".
	 * @return SyncDeltaType (CREATE_OR_UPDATE nebo DELETE).
	 */
	public SyncDeltaType getSyncType() {		
		if (operationType.equals(SyncDeltaType.CREATE_OR_UPDATE.toString())) {
			return SyncDeltaType.CREATE_OR_UPDATE;
		}
		if (operationType.equals(SyncDeltaType.DELETE.toString())) {
			return SyncDeltaType.DELETE;
		}
		
		throw new RuntimeException("Bad SyncDeltaType.");
	}
	
	public void setObjId(Object objId) {
		this.objId = objId;
	}
	
	@SuppressWarnings("rawtypes")
	public void setTimestamp(Comparable timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(SyncObject o) {
		Comparable ts = o.getTimestamp();

        return this.timestamp.compareTo(ts);
	}
	
	@Override
	public String toString() {		
		return "ID = " + objId + ", timestamp = " + timestamp.toString() + ", operation = " + operationType;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if (obj instanceof SyncObject) {
			return this.objId == ((SyncObject) obj).getObjId();
		}
		return false;
	}
	
}
