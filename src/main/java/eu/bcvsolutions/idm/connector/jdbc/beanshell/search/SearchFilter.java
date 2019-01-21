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

package eu.bcvsolutions.idm.connector.jdbc.beanshell.search;

import eu.bcvsolutions.idm.connector.jdbc.beanshell.JDBCConstants;

/**
 * This class encapsulates a basic search filter. 
 * 
 * @author Jaromir Mlejnek
 */
public final class SearchFilter implements JDBCFilter {
	
	/**
	 * Supported relations for search.
	 */
	public static enum Relation {
					
		EQUAL ("="),
		
		NOT_EQUAL ("!="), 
						
		LIKE ("LIKE"),
		
		NOT_LIKE ("NOT LIKE"),
		
		IS_NULL ("IS NULL"),
		
		IS_NOT_NULL ("IS NOT NULL"),
		
		GREATER_THAN (">"),
		
		GREATER_EQUAL (">="),
		
		LESS_THAN ("<"),
		
		LESS_EQUAL ("<=");
		
		private final String operand;
		
		private Relation(String operand) {
			this.operand = operand;
		}
		
		public String getOperand() {
			return this.operand;
		}
	}		
	
	/**
	 * Name of column to search
	 */
	private String attrName;
	
	/**
	 * Value for search
	 */
	private Object attrValue;
	
	/**
	 * SQL relation
	 */
	private Relation relation; 
	
	/**
	 * Uid value for the searching item. 
	 * It's used for backward compatibility
	 * with already implemented BeanShell
	 * scripts.  
	 */
	private String uid;
	
	public SearchFilter() {
		this.attrName = null;
		this.attrValue = null;
		this.relation = null;
		this.uid = null;
	}
	
	public SearchFilter(String attrName, Object attrValue, Relation relation, String uid) {
		this.attrName = attrName;
		this.attrValue = attrValue;
		this.relation = relation;
		this.uid = uid;
	}
	
	public String getAttrName() {
		return attrName;
	}
	
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	
	public Object getAttrValue() {
		return attrValue;
	}
	
	public void setAttrValue(Object attrValue) {
		this.attrValue = attrValue;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof SearchFilter) {
			SearchFilter filter = (SearchFilter) obj;
			
			if ((attrName == null) ? (filter.getAttrName() != null) : !attrName.equals(filter.getAttrName())) {
				return false;
			}
			if ((attrValue == null) ? (filter.getAttrValue() != null) : !attrValue.equals(filter.getAttrValue())) {
				return false;
			}
			if ((relation == null) ? (filter.getRelation() != null) : !relation.equals(filter.getRelation())) {
				return false;
			}
			if ((uid == null) ? (filter.getUid() != null) : !uid.equals(filter.getUid())) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public String getSqlQuery() {
		return getSqlQuery(JDBCConstants.DEFAULT_COLUMN_NAME_QUOTE, JDBCConstants.DEFAULT_COLUMN_VALUE_QUOTE);
	}
	
	@Override
	public String getSqlQuery(String columnNameQuote, String columnValueQuote) {
		if (uid != null) {
			return null;
		}
		if (attrName == null || relation == null) {
			throw new IllegalArgumentException("Attribute name or relation is not set");
		}
		
		StringBuilder sb = new StringBuilder();		
		sb.append(columnNameQuote).append(attrName).append(columnNameQuote);
		sb.append(JDBCConstants.GAP);
		
		switch (relation) {
			case EQUAL:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case NOT_EQUAL:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case LIKE:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case NOT_LIKE:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case IS_NULL:
				sb.append(relation.getOperand());
				break;
			case IS_NOT_NULL:
				sb.append(relation.getOperand());
				break;
			case GREATER_THAN:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case GREATER_EQUAL:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case LESS_EQUAL:
				getColumnValuePart(sb, columnValueQuote);
				break;
			case LESS_THAN:
				getColumnValuePart(sb, columnValueQuote);
				break;
			default:
				throw new IllegalArgumentException("Invalid relation operand");
		}
		
		return sb.toString();
	}
	
	/**
	 * Method sets up operand and value into the string builder.
	 * @param sb
	 * @param columnValueQuote
	 */
	private final void getColumnValuePart(StringBuilder sb, String columnValueQuote) {
		if (attrValue == null) {
			throw new IllegalArgumentException("Search attribute value is NULL");
		}
		
		sb.append(relation.getOperand()).append(JDBCConstants.GAP);
		sb.append(columnValueQuote).append(attrValue.toString()).append(columnValueQuote);		
	}
}
