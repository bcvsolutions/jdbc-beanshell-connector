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

package eu.bcvsolutions.idm.connector.jdbc.beanshell;

/**
 * Třída definující konstanty.
 * 
 * @author Jaromír Mlejnek
 */
public class JDBCConstants {
	
	private JDBCConstants(){		
	}
	
	// Names of input variables
	public static final String JDBC_OPTIONS = "options";
	public static final String JDBC_OBJCLASS = "objClass";
	public static final String JDBC_ATTRIBUTES = "attributes";
	public static final String JDBC_CONNECTION = "conn";
	public static final String JDBC_UID = "uid";
	public static final String JDBC_TOKEN = "token";
	public static final String JDBC_FILTER = "filter";
	
	// Names of output variables	
	public static final String JDBC_NEW_UID = "newUid";	
	public static final String JDBC_CONNECTOR_OBJ = "connectorObject";
	public static final String JDBC_LISTALL = "listOfIDs";	
	public static final String JDBC_LISTOFSYNC = "listOfSync";
	public static final String JDBC_RESULT = "result";
	
	// Script languages
	public static final String JDBC_LANG_BSH = "bsh";
	public static final String JDBC_LANG_BSH_PATH = "bsh_path";		

	public static final String DEFAULT_COLUMN_NAME_QUOTE = "`";
	public static final String DEFAULT_COLUMN_VALUE_QUOTE = "'";
	public static final String GAP = " ";
}
