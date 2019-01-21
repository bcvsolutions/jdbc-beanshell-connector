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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.api.operations.ScriptOnResourceApiOp;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.ScriptContext;
import org.identityconnectors.framework.common.objects.SyncDelta;
import org.identityconnectors.framework.common.objects.SyncDeltaBuilder;
import org.identityconnectors.framework.common.objects.SyncResultsHandler;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.ScriptOnResourceOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.SyncOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import eu.bcvsolutions.idm.connector.jdbc.beanshell.search.CompositeFilter;
import eu.bcvsolutions.idm.connector.jdbc.beanshell.search.JDBCFilter;
import eu.bcvsolutions.idm.connector.jdbc.beanshell.search.SearchFilter;
import eu.bcvsolutions.idm.connector.jdbc.beanshell.utils.SyncObject;


/**
 * Main JDBC connector class
 * 
 * @author Jaromír Mlejnek
 */
@ConnectorClass(displayNameKey="JDBC_CONNECTOR",
		configurationClass=JDBCConfiguration.class)
public class JDBCConnector implements Connector, CreateOp, DeleteOp, 
	SearchOp<JDBCFilter>, UpdateOp, SchemaOp, TestOp, SyncOp, ScriptOnResourceOp, ScriptOnResourceApiOp {

	private JDBCConfiguration config;
	private JDBCConnection connection;
		
	private static Schema schema;
	
	private static final Log log = Log.getLog(JDBCConnector.class);
	
	
	/**
	 * Implicitní konstruktor třídy JDBCConnector.	 
	 */
    public JDBCConnector() {	 
    }        

    /**
     * Metoda navracející konfiguraci.
     */
    public JDBCConfiguration getConfiguration() {
        return config;
    }
    
    /**
     * Metoda navracející instanci třídy zabezpečující spojení s koncovým systémem.
     * @return Instance třídy JDBCConnection.
     */
    public JDBCConnection getConnection() {
    	return connection;
    }
    
    /**
     * Inicializační metoda.
     */
    public void init(Configuration cfg) {
    	log.info("Connector INIT.");
    	config = (JDBCConfiguration) cfg;
    	connection = new JDBCConnection(config);    	
    }
    
    /**
     * Metoda pro ukončení spojení s koncovým systémem.
     */
    public void dispose() {
    	log.info("Connection dispose.");    	
    	if (connection != null) {
    		connection.dispose();
    	}
    }
    
    /**
     * Metoda testující spojení s koncovým systémem.
     */
    public void test() {		
		connection.test();
	}
    
    /**
     * Metoda pro vytváření objektu na koncovém systému.
     */
    public Uid create(ObjectClass oclass, Set<Attribute> attrs, OperationOptions options) {
    	checkClassType(oclass);
    	Uid uid = null;    	    	    	
    	Connection conn = null;
    	
    	try {
    		conn = connection.startConnection();    		
    		Interpreter bsh = new Interpreter();
    		
    		bsh.set(JDBCConstants.JDBC_OBJCLASS, oclass);
    		bsh.set(JDBCConstants.JDBC_ATTRIBUTES, attrs);    		
    		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
    		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);    		
    		bsh.source(config.getCreate());
    		
    		uid = (Uid) bsh.get(JDBCConstants.JDBC_NEW_UID);
    		
    	} catch (TargetError targetErr) {    		
    		throw new ConnectorException(targetErr);  		    		
    	} catch (Exception ex) {
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	}   	
    	
    	return uid;
    }
    
    /**
     * Metoda pro změnu objektu na koncovém systému.
     */
    public Uid update(ObjectClass objclass, Uid uid, Set<Attribute> replaceAttributes, OperationOptions options) {    	
    	checkClassType(objclass);    	    	        	
    	Uid newUid = null;    	
    	Connection conn = null;
    	
    	try {
    		conn = connection.startConnection();    		    		
    		Interpreter bsh = new Interpreter();    	
    		
    		bsh.set(JDBCConstants.JDBC_OBJCLASS, objclass);
    		bsh.set(JDBCConstants.JDBC_UID, uid);
    		bsh.set(JDBCConstants.JDBC_ATTRIBUTES, replaceAttributes);
    		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
    		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);
    		bsh.source(config.getUpdate());    		    		
    		 
    		newUid = (Uid) bsh.get(JDBCConstants.JDBC_NEW_UID);
    		
    	} catch (TargetError targetErr) {    		
    		throw new ConnectorException(targetErr);    		    		    		    		
    	} catch (Exception ex) {    		
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	}   	
    	
    	return newUid;
    }
    
    /**
     * Metoda pro smazání objektu na koncovém systému.
     */
    public void delete(ObjectClass objClass, Uid uid, OperationOptions options) {    	
    	checkClassType(objClass);    	    	        	
    	Connection conn = null;
    	
    	try {
    		conn = connection.startConnection();
    		Interpreter bsh = new Interpreter();
 		
    		bsh.set(JDBCConstants.JDBC_OBJCLASS, objClass);
    		bsh.set(JDBCConstants.JDBC_UID, uid);
    		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
    		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);    		
    		bsh.source(config.getDelete());    		    		    	
    		    		
    	} catch (TargetError targetErr) {
    		throw new ConnectorException(targetErr);
    	} catch (Exception ex) {
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	}   	    	
    }
    
    /**
     * Metoda pro vytváření dotazovacích filtrů. Podporován jen filtr pro vyhledávání dle identifikátoru 
     * (metoda navrací identifikátor daného objektu).
     */
    public FilterTranslator<JDBCFilter> createFilterTranslator(ObjectClass oclass, OperationOptions options) {
    	return new AbstractFilterTranslator<JDBCFilter>() {
    		/**
    		 * Metoda vytvářející dotaz pro vyhledání daného objektu. V tomto případě si necháme navrátit
    		 * jen uid identifikátor daného objektu.
    		 */
    		@Override
    		protected JDBCFilter createEqualsExpression(EqualsFilter filter, boolean not) {    			    			    			
    			Attribute attr = filter.getAttribute();
    			if (attr == null) {
    				throw new IllegalArgumentException("Filter attribute is null.");
    			}
    			
    			SearchFilter searchFilter = new SearchFilter();
    			if (attr.is(Uid.NAME)) {    				
    				searchFilter.setUid(((Uid)attr).getUidValue());
    			} else {
    				searchFilter.setAttrName(attr.getName());
    				searchFilter.setAttrValue(attr.getValue() != null ? attr.getValue().get(0) : null);
    			}
    			searchFilter.setRelation(SearchFilter.Relation.EQUAL);
    			
    			return searchFilter;   			
    		}
    		
    		@Override
    		protected JDBCFilter createAndExpression(JDBCFilter leftExpression, JDBCFilter rightExpression) {    			
    			return new CompositeFilter(leftExpression, rightExpression, CompositeFilter.Relation.AND);
    		}
    		
    		@Override
    		protected JDBCFilter createOrExpression(JDBCFilter leftExpression, JDBCFilter rightExpression) {
    			return new CompositeFilter(leftExpression, rightExpression, CompositeFilter.Relation.OR);
    		}
    		
    		@Override
    		protected JDBCFilter createStartsWithExpression(StartsWithFilter filter, boolean not) {
    			SearchFilter searchFilter = new SearchFilter();
    			searchFilter.setAttrName(filter.getName());
    			searchFilter.setAttrValue(filter.getValue() + "%");
    			if (not) {
    				searchFilter.setRelation(SearchFilter.Relation.NOT_LIKE);
    			} else {
    				searchFilter.setRelation(SearchFilter.Relation.LIKE);
    			}
    			
    			return searchFilter;
    		}
    		
    		@Override
    		protected JDBCFilter createEndsWithExpression(EndsWithFilter filter, boolean not) {
    			SearchFilter searchFilter = new SearchFilter();
    			searchFilter.setAttrName(filter.getName());
    			searchFilter.setAttrValue("%" + filter.getValue());
    			if (not) {
    				searchFilter.setRelation(SearchFilter.Relation.NOT_LIKE);
    			} else {
    				searchFilter.setRelation(SearchFilter.Relation.LIKE);
    			}
    			
    			return searchFilter;
    		}
    		    		
		};
    }
        
    /**
     * Metoda pro provádění dotazů na koncovém systému.
     */
	@SuppressWarnings("rawtypes")
	public void executeQuery(ObjectClass oclass, JDBCFilter filter, ResultsHandler handler, OperationOptions options) {
    	checkClassType(oclass);    	        	
    	Connection conn = null;    	
    	ConnectorObject connectorObject = null;
    	try {
			conn = connection.startConnection();						
			
			if (filter == null || filter.getUid() == null) {				
				
				// List all objects.				
				List listOfObjects = getAllConnectorObjects(filter, oclass, conn, options);
				Iterator it = listOfObjects.iterator();
				while (it.hasNext()) {
					Object nextItem = it.next();
					
					if (nextItem instanceof ConnectorObject) {
						
						/**
						 * Handle the connector object returned from the script
						 */
						connectorObject = (ConnectorObject) nextItem;
						if (connectorObject != null) {						
							handler.handle(connectorObject);						
						}
						
					} else if (nextItem instanceof String) {
						
						/**
						 * For given UID return a full connector object.
						 */
						connectorObject = this.getConnectorObject((String) nextItem, oclass, conn, options);
						if (connectorObject != null) {						
							handler.handle(connectorObject);						
						}
						
					} else {
						throw new IllegalStateException("JDBC Connector: Uknown item type in listAll result list: " + nextItem.toString());
					}
										
				}				   	    
			} else {			
				
				// List appropriate objects for given query.								
				connectorObject = getConnectorObject(filter.getUid(), oclass, conn, options);
				
				if (connectorObject != null) {
					handler.handle(connectorObject);
				}
				
			}
			
    	} catch (TargetError targetErr) {    		
    		throw new ConnectorException(targetErr);    		    		    		    		
    	} catch (Exception ex) {    		
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	}
    }
    
	/**
	 * Metoda navrací seznam identifikátorů všech objektů daného typu na koncovém systému.
	 * 
	 * @param oclass typ objektů, které hledáme.
	 * @param conn aktivní spojení s koncovým systémem.
	 * @param options případné další vlastnosti.
	 * @return Seznam všech objektů daného typu na koncovém zařízení. 
	 * @throws EvalError
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    @SuppressWarnings("rawtypes")
	public List getAllConnectorObjects(JDBCFilter filter, ObjectClass oclass, Connection conn, 
			OperationOptions options) throws EvalError, FileNotFoundException, IOException {    	    	
    	List resultList = null;    	
    	Interpreter bsh = new Interpreter();       
    	
		bsh.set(JDBCConstants.JDBC_OBJCLASS, oclass);		
		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);
		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
		bsh.set(JDBCConstants.JDBC_FILTER, filter);
		bsh.source(config.getListAll());	
		
		resultList = (List) bsh.get(JDBCConstants.JDBC_LISTALL);
		
		return resultList;
    }
    
    /**
     * Metoda vyhledávající objekt koncového systému odpovídající danému identifikátoru.
     * 
     * @param query identifikátor daného objektu.
     * @param oclass typ hledaného objektu.
     * @param conn aktivní připojení ke koncovému systému.
     * @param options případné další vlastnosti.
     * @return Odpovídající objekt na koncovém systému (instance třídy ConnectorObject).
     * @throws EvalError
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ConnectorObject getConnectorObject(String uid, ObjectClass oclass, 
    		Connection conn, OperationOptions options) throws EvalError, FileNotFoundException, IOException {    	
    	ConnectorObject conObject = null;
    	Interpreter bsh = new Interpreter();
    	
    	bsh.set(JDBCConstants.JDBC_OBJCLASS, oclass);      		
		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);
		bsh.set(JDBCConstants.JDBC_UID, uid);			
		bsh.source(config.getGet());
		
		conObject = (ConnectorObject) bsh.get(JDBCConstants.JDBC_CONNECTOR_OBJ);		
		
		return conObject;
    }
    
    /**
     * Metoda navracející schéma daného konektoru.    
     */
    public Schema schema() {
    	//Lazy initialization
    	if (schema != null) {
    		return schema;
    	}
    	
    	final SchemaBuilder builder = new SchemaBuilder(getClass());
    	
    	Set<AttributeInfo> attributes = new HashSet<AttributeInfo>();    	
    	//Name je podporovanym atributem.
    	attributes.add(Name.INFO);
    	//Heslo je podporovanym atributem.
    	attributes.add(OperationalAttributeInfos.PASSWORD);
    	
    	// TODO: po upgrade na nove idm zde generuji cele schema, predelat na skript
    	attributes.add(new AttributeInfoBuilder("e-mail", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("mobil", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("idmpwd", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("statni", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("mistnost", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("objekt", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("fax", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("mistni", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("oscis", String.class).setRequired(true).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	attributes.add(new AttributeInfoBuilder("idmlogin", String.class).setCreateable(true).setReadable(true).setUpdateable(true).build());
    	
    	//Prozatim pouze ACCOUNT.
    	//TODO Dodelat pro GROUP.
    	builder.defineObjectClass(ObjectClass.ACCOUNT_NAME, attributes);
    	
    	schema = builder.build();
    	return schema;
    }
    
    /**
     * Handler pracuje nad záznamy, jejichž timestamp je větší než sync token. 
     */
	@SuppressWarnings("unchecked")
	public void sync(ObjectClass objClass, SyncToken token, SyncResultsHandler handler, OperationOptions options) {		
		checkClassType(objClass);    	    	        	
    	Connection conn = null;
    	List<SyncObject> listOfSyncObject = null;
    	PriorityQueue<SyncObject> prior = null;
    	ConnectorObject conObject = null;
    	
    	try {
    		conn = connection.startConnection();
    		Interpreter bsh = new Interpreter();  
    		
    		bsh.set(JDBCConstants.JDBC_OBJCLASS, objClass);
    		bsh.set(JDBCConstants.JDBC_TOKEN, (token != null ? transformByteArrayToObject((byte [])token.getValue()) : null));
    		bsh.set(JDBCConstants.JDBC_OPTIONS, options);
    		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);    		
    		bsh.source(config.getSync());     		
    		
    		listOfSyncObject = (ArrayList<SyncObject>) bsh.get(JDBCConstants.JDBC_LISTOFSYNC);    		
    		if (listOfSyncObject.size() != 0) {
    			prior = new PriorityQueue<SyncObject>(listOfSyncObject.size());
    		} else {
    			prior = new PriorityQueue<SyncObject>();
    		}
    		
    		prior.addAll(listOfSyncObject);
    		
    		SyncDeltaBuilder builder = new SyncDeltaBuilder();
    		SyncDelta delta = null;    		
    		SyncObject syncObj = null;
    		
    		while (!prior.isEmpty()) {    		
    			syncObj = prior.poll();
    			
    			// Vyhledame dany ConnectorObject pro zamestnance
    			try {
    				conObject = getConnectorObject(String.valueOf(syncObj.getObjId()), objClass, conn, options);
    				
    			} catch (TargetError targetErr) {    		
    	    		// ucet nebyl nalezen
    				continue;
    			}
    			
    			// Vytvorime delta				
				builder.setObject(conObject);
				builder.setToken(new SyncToken(transformObjectToByteArray(syncObj.getTimestamp())));				
				builder.setDeltaType(syncObj.getSyncType());
				delta = builder.build();	
				
				boolean canContinue = handler.handle(delta);
				if (!canContinue) {
					break;
				}
    		}    		
    		    		
    	} catch (Exception ex) {
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	} 
	}

	/**
	 * Navrátí token odpovídající TIMESTAMPu poslední poslední provedené změna nad sledovanými tabulkami.
	 * {@inheritDoc}
	 */
	public SyncToken getLatestSyncToken(ObjectClass objClass) {    	
		checkClassType(objClass);    	 	    
    	Connection conn = null;
    	SyncToken token = null;
    	
    	try {
    		conn = connection.startConnection();
    		Interpreter bsh = new Interpreter();    
    		
    		bsh.set(JDBCConstants.JDBC_OBJCLASS, objClass);    		    
    		bsh.set(JDBCConstants.JDBC_CONNECTION, conn);    		
    		bsh.source(config.getGetSyncToken());
    		
    		Object timestamp = bsh.get(JDBCConstants.JDBC_TOKEN);
    		
    		// Serializace timestampu na pole bajtu a jeho ulozeni jako synchronizacni token. 
    		token = new SyncToken(transformObjectToByteArray(timestamp));    		
    		
    	} catch (TargetError targetErr) {    		 
    		throw new ConnectorException(targetErr);    		    		
    	} catch (Exception ex) {
    		throw new ConnectorException(ex);
    	} finally {
    		connection.closeConnection(conn);
    	}   	
    	
    	return token;
	}
	
	/**
	 * Transformuje pole bajtů reprezentujících serializovaný objekt na původní objekt.
	 * @param array pole bajtů reprezentující serializovaný objekt
	 * @return Zrekonstruovaný objekt.
	 */
	private Object transformByteArrayToObject(byte [] array) {
		if (array == null) return null;
		
		ObjectInputStream ois = null;
		Object obj = null;		
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(array));
			obj = ois.readObject();			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return obj;
	}
	
	/**
	 * Transformuje objekt do pole bajtů.
	 * @param obj objekt, který se má serializovat do pole bajtů
	 * @return Výsledné pole bajtů.
	 */
	private byte [] transformObjectToByteArray(Object obj) {
		if (obj == null) return null;
		
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos); 
			oos.writeObject(obj);
		    oos.flush(); 
		    oos.close(); 
		    bos.close();
		    bytes = bos.toByteArray ();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return bytes;				
	}
	
    /**
     * Metoda kontroluje, zda jsou objekty typu ACCOUNT nebo GROUP. Pokud se nejedna ani o nejeden z nich,
     * tak je vyvolána chyba <b>ConnectorException</b>.
     * 
     * @param oclass třído objektů, která se má zkontrolovat.
     * @throws ConnectorException
     */
    private void checkClassType(ObjectClass oclass) throws ConnectorException {
    	if (!oclass.is(ObjectClass.ACCOUNT_NAME) && !oclass.is(ObjectClass.GROUP_NAME)) {
    		log.error("Bad exception type: {0}", oclass);
    		throw new ConnectorException("Bad object type.");
    	}      	
    }

    /**
     * Metoda, ktera spusti skript na koncovem systemu.
     * Pokud je zadany jazyk skriptu "bsh_path", konektor sam nalezne skript na dane ceste uvedene v textu skriptu.
     * Pokud je zadany jazyk skriptu "bsh", konektor provede zadany vstupni skript.
     * @return objekt na vystupu ze skriptu
     */
	public Object runScriptOnResource(ScriptContext request,
			OperationOptions options) {
		
		//nactu jazyk a text skriptu
		String lang = request.getScriptLanguage();
		String text = request.getScriptText();
		
		if (lang == null) {
			log.error("Script language not specified!");
    		throw new ConnectorException("Script language not specified!");
		}
		
		//reader pro cteni skriptu
		Reader inputReader = null;
		//spojeni na databazi, ktere bude predano skriptu na vstupu
		Connection conn = null;  	
		Interpreter bsh = new Interpreter();
		//vystup ze skriptu
		Object result = null;
		
		try {
			
			//je-li v jazyce zadano bsh_path, predpoklada se, ze je na vstupu cesta k souboru se skriptem
			if (JDBCConstants.JDBC_LANG_BSH_PATH.equalsIgnoreCase(lang)) {
				inputReader = new FileReader(new File(text));
			
			//je-li v jazyce zadano bsh, predpoklada se, ze je na vstupu samotny text skriptu
			} else if (JDBCConstants.JDBC_LANG_BSH.equalsIgnoreCase(lang)) {
				inputReader = new StringReader(text);
			
			//pro ostatni jazyky vyhazuji vyjimku
			} else {
				log.error("Unsupported language: {0}", lang);
				throw new ConnectorException("Unsupported language: " + lang);
			}
		
			//ziskam spojeni na databazi
			conn = connection.startConnection();
			//predam options, spojeni a parametry
			bsh.set(JDBCConstants.JDBC_OPTIONS, options);
			bsh.set(JDBCConstants.JDBC_CONNECTION, conn);
			for (String argName : request.getScriptArguments().keySet()) {
				bsh.set(argName, request.getScriptArguments().get(argName));
			}
			//provedu skript
			bsh.eval(inputReader);
			
			//prectu vystup
			result = bsh.get(JDBCConstants.JDBC_RESULT);
			
		} catch (EvalError e) {
			throw new ConnectorException(e);
		} catch (Exception e) {
			throw new ConnectorException(e);
		} finally {
    		connection.closeConnection(conn);
    	}  
		
		return result;
	}
    
}
