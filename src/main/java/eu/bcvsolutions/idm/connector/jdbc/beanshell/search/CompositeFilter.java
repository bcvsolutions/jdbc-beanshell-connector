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
 * 
 * @author Jaromir Mlejnek
 *
 */
public class CompositeFilter implements JDBCFilter  {

	/**
	 * Logical relations. 
	 */
	public static enum Relation {		
		AND,		
		OR
	}
	
	/**
	 * Left side of relation.
	 */
	private JDBCFilter left;
	
	/**
	 * Right side of relation.
	 */
	private JDBCFilter right;
	
	private Relation relation;
	
	public CompositeFilter() {
		this.left = null;
		this.right = null;
		this.relation = null;
	}
	
	public CompositeFilter(JDBCFilter left, JDBCFilter right, Relation relation) {
		this.left = left;
		this.right = right;
		this.relation = relation;
	}
	
	@Override
	public String getSqlQuery() {
		return getSqlQuery(JDBCConstants.DEFAULT_COLUMN_NAME_QUOTE, JDBCConstants.DEFAULT_COLUMN_VALUE_QUOTE);
	}
	
	@Override
	public String getSqlQuery(String columnNameQuote, String columnValueQuote) {
		if (left == null || right == null) {
			throw new IllegalArgumentException("Left or right side of expression is NULL");
		}
		if (relation == null) {
			throw new IllegalArgumentException("Relation of expression is NULL");
		}
		
		StringBuilder sb = new StringBuilder();		
		sb.append("(").append(left.getSqlQuery(columnNameQuote, columnValueQuote)).append(JDBCConstants.GAP);
		sb.append(relation.toString()).append(JDBCConstants.GAP).append(right.getSqlQuery(columnNameQuote, columnValueQuote));
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public String getUid() {
		return null;
	}
	
	public JDBCFilter getLeft() {
		return left;
	}
	
	public void setLeft(JDBCFilter left) {
		this.left = left;
	}
	
	public JDBCFilter getRight() {
		return right;
	}
	
	public void setRight(JDBCFilter right) {
		this.right = right;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
}
