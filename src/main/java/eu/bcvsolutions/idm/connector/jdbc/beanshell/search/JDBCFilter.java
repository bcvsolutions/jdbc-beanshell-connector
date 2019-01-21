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

/**
 * This interface specified methods which return queries for search.
 * 
 * @author Jaromir Mlejnek
 *
 */
public interface JDBCFilter {

	/**
	 * Method returns WHERE clause for the filter.
	 * @return WHERE part of SQL SELECT statement
	 */
	public String getSqlQuery();
	
	/**
	 * Method returns WHERE clause for the filter. 
	 * Given column name is bounded by "columnNameQuote"
	 * and the value is bounded by "columnValueQuote".
	 * 
	 * @param columnNameQuote
	 * @param columnValueQuote
	 * @return WHERE part of SQL SELECT statement
	 */
	public String getSqlQuery(String columnNameQuote, String columnValueQuote);
	
	/**
	 * Returns UID.
	 * @return
	 */
	public String getUid();
	
}
