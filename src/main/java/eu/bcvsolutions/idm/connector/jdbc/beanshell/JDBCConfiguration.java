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

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;


/**
 * Konfigurační třída JDBC konektoru.
 * 
 * @author Jaromír Mlejnek
 */
public class JDBCConfiguration extends AbstractConfiguration {	
	
	//Udaje pro navazani spojeni
	private String hostname;	
	private int port;
	private String username;
	private GuardedString password;
	private String databaseName;
	
	private String jdbcDriver;
	private String urlTemplate;
	
	
	//Cesty ke skriptum
	private String get;
	private String create;
	private String update;
	private String delete;
	private String listAll;
	private String getSyncToken;
	private String sync;
	
	/**
	 * Implicitní konstruktor.
	 */
    public JDBCConfiguration() {    	
    }

    @ConfigurationProperty(order = 1,
    		displayMessageKey = "JDBC_HOSTNAME_NAME",
    		helpMessageKey = "JDBC_HOSTNAME_HELP",
    		required = true)
    public String getHostname() {
		return hostname;
	}
    
    @ConfigurationProperty(order = 2,
    		displayMessageKey = "JDBC_PORT_NAME",
    		helpMessageKey = "JDBC_PORT_HELP",
    		required = true)
    public int getPort() {
    	return port;
    }
    
    @ConfigurationProperty(order = 3,
    		displayMessageKey = "JDBC_USERNAME_NAME",
    		helpMessageKey = "JDBC_USERNAME_HELP",
    		required = true)
    public String getUsername() {
		return username;
	}
    
    @ConfigurationProperty(order = 4, confidential = true,
    		displayMessageKey = "JDBC_PASSWORD_NAME",
    		helpMessageKey = "JDBC_PASSWORD_HELP")
    public GuardedString getPassword() {
		return password;
	}
    
    @ConfigurationProperty(order = 5,
    		displayMessageKey = "JDBC_DATABASENAME_NAME",
    		helpMessageKey = "JDBC_DATABASENAME_HELP",
    		required = true)
    public String getDatabaseName() {
		return databaseName;
	}
    
    @ConfigurationProperty(order = 6,
    		displayMessageKey = "JDBC_JDBCDRIVER_NAME",
    		helpMessageKey = "JDBC_JDBCDRIVER_HELP",
    		required = true)
    public String getJdbcDriver() {
		return jdbcDriver;
	}
    
    @ConfigurationProperty(order = 7,
    		displayMessageKey = "JDBC_URLTEMPLATE_NAME",
    		helpMessageKey = "JDBC_URLTEMPLATE_HELP",
    		required = true)
    public String getUrlTemplate() {
		return urlTemplate;
	}
    
    @ConfigurationProperty(order = 8,
    		displayMessageKey = "JDBC_GET_NAME",
    		helpMessageKey = "JDBC_GET_HELP",
    		required = true)
    public String getGet() {
		return get;
	}
    
    @ConfigurationProperty(order = 9,
    		displayMessageKey = "JDBC_CREATE_NAME",
    		helpMessageKey = "JDBC_CREATE_HELP",
    		required = true)
    public String getCreate() {
		return create;
	}
    
    @ConfigurationProperty(order = 10,
    		displayMessageKey = "JDBC_UPDATE_NAME",
    		helpMessageKey = "JDBC_UPDATE_HELP",
    		required = true)
    public String getUpdate() {
		return update;
	}
    
    @ConfigurationProperty(order = 11,
    		displayMessageKey = "JDBC_DELETE_NAME",
    		helpMessageKey = "JDBC_DELETE_HELP",
    		required = true)
    public String getDelete() {
		return delete;
	}
    
    @ConfigurationProperty(order = 12,
    		displayMessageKey = "JDBC_LISTALL_NAME",
    		helpMessageKey = "JDBC_LISTALL_HELP",
    		required = true)
    public String getListAll() {
    	return listAll;
    }
    
    @ConfigurationProperty(order = 13,
    		displayMessageKey = "JDBC_GETSYNCTOKEN_NAME",
    		helpMessageKey = "JDBC_GETSYNCTOKEN_HELP",
    		required = true)
    public String getGetSyncToken() {
    	return getSyncToken;
    }
    
    @ConfigurationProperty(order = 14,
    		displayMessageKey = "JDBC_SYNC_NAME",
    		helpMessageKey = "JDBC_SYNC_HELP",
    		required = true)
    public String getSync() {
    	return sync;
    }
    
    public void setHostname(String hostname) {
		this.hostname = hostname;
	}
    
    public void setPort(int port) {
		this.port = port;
	}
    
    public void setUsername(String username) {
		this.username = username;
	}
    
    public void setPassword(GuardedString password) {
		this.password = password;
	}
    
    public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
    
    public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
    
    public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
    
    public void setGet(String get) {
		this.get = get;
	}
    
    public void setCreate(String create) {
		this.create = create;
	}
    
    public void setUpdate(String update) {
		this.update = update;
	}
    
    public void setDelete(String delete) {
		this.delete = delete;
	}
    
    public void setListAll(String listAll) {
    	this.listAll = listAll;
    }
    
    public void setGetSyncToken(String getSyncToken) {
		this.getSyncToken = getSyncToken;
	}
    
    public void setSync(String sync) {
		this.sync = sync;
	}
    
    /**
     * Vrací a formátuje zprávu.
     * 
     * @param key klíč pro výběr zprávy z "Message.properties".
     * @return Zpráva.
     */
    public String getMessage(String key) {
    	return getConnectorMessages().format(key, key);
    }
    
    /**
     * Vrací a formátuje zprávu.
     * 
     * @param key klíč pro výběr zprávy z "Message.properties"
     * @param objects parametry
     * @return Zpráva.
     */
    public String getMessage(String key, Object... objects) {
    	return getConnectorMessages().format(key, key, objects);
    }
               
    /**
     * Metoda validuje uvedená data. Pokud není vyžadovaná informace uvedená, 
     * tak vyhodí vyjímku typu IllegalArgumentException.
     */
    public void validate() {    	
    	if (StringUtil.isBlank(getHostname())) {
    		throw new IllegalArgumentException("DB hostname must be specified.");
    	}
    	if (getPort() <= 0 || getPort() >= 65535) {
    		throw new IllegalArgumentException("Port must be in range between <0,65535>.");
    	}    	
    	if (StringUtil.isBlank(getDatabaseName())) {
    		throw new IllegalArgumentException("Database name must be specified.");
    	}
    	if (StringUtil.isBlank(getJdbcDriver())) {
    		throw new IllegalArgumentException("JDBC driver must be specified.");
    	}
    	if (StringUtil.isBlank(getUrlTemplate())) {
    		throw new IllegalArgumentException("URL template must be specified.");
    	}
    	if (StringUtil.isBlank(getGet())) {
    		throw new IllegalArgumentException("Path to GET script must be set.");
    	}
    	if (StringUtil.isBlank(getCreate())) {
    		throw new IllegalArgumentException("Path to CREATE script must be set.");
    	}
    	if (StringUtil.isBlank(getUpdate())) {
    		throw new IllegalArgumentException("Path to UPDATE script must be set.");
    	}
    	if (StringUtil.isBlank(getDelete())) {
    		throw new IllegalArgumentException("Path to DELETE script must be set.");
    	}
    	if (StringUtil.isBlank(getListAll())) {
    		throw new IllegalArgumentException("Path to LISTALL script must be set.");
    	}
    	if (StringUtil.isBlank(getGetSyncToken())) {
    		throw new IllegalArgumentException("Path to GETSYNCTOKEN script must be set.");
    	}
    	if (StringUtil.isBlank(getSync())) {
    		throw new IllegalArgumentException("Path to SYNC script must be set.");
    	}
    }
    
    /**
     * Metoda načte šablonu URL pro zvolenou databazi a provede náhradu speciálních znaků. Nahrazuje se:
     * 		<b>%h</b> je nahrazeno adresou databázového stroje (host),
     * 		<b>%p</b> je nahrazeno číslem portu,
     * 		<b>%d</b> je nahrazeno názvem databáze.
     * 
     * Pokud se znak % nachází před jiným písmenem, tak se celá dvojice (% a odpovídající znak) odstraní.
     * Pokud se v šabloně nachází dva znaky % za sebou, tak se odstraní pouze první z nich (eskejpování).    
     *      
     * @return URL pro připojení k DB odpovídající zadaným konfoguračním údajům. 
     */
    public String formateDatabaseURL() {
		StringBuilder sb = new StringBuilder();		
		char ch;
		int len = getUrlTemplate().length();
		for(int i = 0; i < len; i++) {
			ch = getUrlTemplate().charAt(i);
			
			if (ch != '%') {
				sb.append(ch);
			} else {
				if (i + 1 < len) {
					i++;
					ch = getUrlTemplate().charAt(i);
					
					if (ch == '%')
						sb.append(ch);
					else if (ch == 'h')
						sb.append(getHostname());
					else if (ch == 'p')
						sb.append(getPort());
					else if (ch == 'd')
						sb.append(getDatabaseName());
				}
			}
		}
		
		return sb.toString();
	}


}
