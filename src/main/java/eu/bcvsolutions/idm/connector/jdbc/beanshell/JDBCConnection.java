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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;

/**
 * Třída pro vytváření spojení s koncovým systémem.
 * 
 * @author Jaromír Mlejnek
 */
public class JDBCConnection {
	
	private JDBCConfiguration config;	
	
	private static final Log log = Log.getLog(JDBCConnection.class);
	
	/**
	 * Konstruktor.
	 * 
	 * @param cnf
	 */
    public JDBCConnection(JDBCConfiguration cnf) {    	
    	if (cnf != null) {
    		config = (JDBCConfiguration) cnf;
    	}    	
    }        

    public void dispose() {    	    	
    }   

    /**
     * Metoda testující spojení s databází.
     */
    public void test() {
    	log.info("Connection test.");
    	config.validate();
    	Connection conn = startConnection();
    	closeConnection(conn);
    }
    
    public JDBCConfiguration getConfig() {
		return config;
	}
    
    /**
     * Metoda pro vytvoření spojení s databází. 
     * 
     * @return Instance třídy Connection.
     */
    public Connection startConnection() {    	
    	String user = config.getUsername();
    	GuardedString passwd = config.getPassword();
    	String password = "";
    	if (passwd != null) {
    		GuardedStringAccessor gsa = new GuardedStringAccessor();
        	passwd.access(gsa);
        	password = new String(gsa.getArray());
    	}	    	
    	
    	Connection conn = null;
    	
    	try {    		
    		Class.forName(config.getJdbcDriver());
    		
    		if (StringUtil.isNotBlank(user)) {    			
    			conn = DriverManager.getConnection(config.formateDatabaseURL(), user, password);    			
    			log.info("Connection started for user {0}", user);
    		} else {
    			conn = DriverManager.getConnection(config.formateDatabaseURL());
    			log.info("Connection started.");
    		}
    		    		
    	} catch (ClassNotFoundException cnfExc) {
    		log.error("Appropriate JDBC driver not found.");
    		throw new ConnectionFailedException(cnfExc.getMessage());    		
    	} catch (SQLException sqlExc) {
    		log.error("Connection failed.");
    		throw new ConnectionFailedException(sqlExc.getMessage());
    	}
    	
    	return conn;
    }
    
    /**
     * Metoda pro uzavření daného spojení.
     * 
     * @param connection Instance třídy Connection reprezentující otevřené spojení s databází.
     */
    public void closeConnection(Connection connection) {
    	try {
    		if (connection != null) {
    			connection.close();
    			connection = null;
    		}
    		log.info("Connection closed.");
    	} catch (SQLException sqlExc) {
    		log.error("Error during closing connection. Message: {0}", sqlExc.getMessage());
    		throw new ConnectionFailedException(sqlExc.getMessage());
    	}
    }
    
    
}
