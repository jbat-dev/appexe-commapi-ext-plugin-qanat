package com.mobilous.ext.plugin.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jbat.http.rest.client.ClientResponseLoggingFilter;
import com.jbat.http.rest.entity.TableData;
import com.mobilous.ext.plugin.PluginService;
import com.mobilous.ext.plugin.constant.Constant;
import com.mobilous.ext.plugin.constant.DatasetKey;
import com.mobilous.ext.plugin.schema.DataType;
import com.mobilous.ext.plugin.util.HeterogeneousMap;

/**
 * The main implementation of the plugin goes here.
 *
 * @author yanto
 */
@PluginImplementation
public class PluginServiceImpl implements PluginService {

	private static String serviceName;
	private static String consumerKey; // Qanat domain
	@SuppressWarnings("unused")
	private static String consumerSecret;
	private static String username;
	private static String password;
	private static String authtype;
	private static String callbackURL;
	private static String domain;
	private static String port;
	private static String pluginFile;
	private static String pluginConfigFile;

	public PluginServiceImpl() {

		// default values
		serviceName = "Qanat";
		// !!! consumerKey must set at the config file.
		consumerKey = "";
		username = "cvadmin";
		password = "cvadmin";
		authtype = "custom";
		// FIXED domain means AppExe domain
		domain = "syajims01.mobilous.com";
		port = "6200";

		// Do not change
		callbackURL = "https://" + domain
				+ ":8181/commapi/extsvc/authenticate?servicename="
				+ serviceName;

		// update the plugin filename, if the plugin filename will be changed
		// update this
		pluginFile = "appexe-commapi-ext-plugin-qanat.jar";

		// update the plugin config filename should be same with plugin filename
		// the,
		// extension name shoud be ".xml" if the plugin filename will be changed
		// update this
		pluginConfigFile = "appexe-commapi-ext-plugin-qanat.xml";

		loadSettingsFromConfigfile();

	}

	// TODO Check XMLfile to upload, and
	/**
	 * loadSettingsFromConfigfile - the plugin will load the settings from the
	 * config file.
	 * 
	 * <pre>
	 *     the config file will be created automatically from the console.
	 *     If the developer needs the plugin config test file. you can ask the
	 *     Mobilous support team.
	 * </pre>
	 * */
	private void loadSettingsFromConfigfile() {
		try {
			File fXmlFile = new File("/usr/glassfish",
					"comm-server/ext/plugins/" + pluginConfigFile);
			if (!fXmlFile.exists()) {
				System.out
						.println("PLUGIN : loadSettingsFromConfigfile file does not exist. use default settings.- "
								+ fXmlFile.getAbsolutePath());
				return;
			}

			System.out.println("PLUGIN : loadSettingsFromConfigfile load:"
					+ fXmlFile.getAbsolutePath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("config");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					serviceName = eElement.getElementsByTagName("servicename")
							.item(0).getTextContent();
					consumerKey = eElement.getElementsByTagName("consumerkey")
							.item(0).getTextContent();
					domain = eElement.getElementsByTagName("domain").item(0)
							.getTextContent();

					username = eElement.getElementsByTagName("username")
							.item(0).getTextContent();
					password = eElement.getElementsByTagName("password")
							.item(0).getTextContent();

					System.out.println("servicename : "
							+ eElement.getElementsByTagName("servicename")
									.item(0).getTextContent());
					System.out.println("consumerKey : "
							+ eElement.getElementsByTagName("consumerkey")
									.item(0).getTextContent());
					System.out.println("domain : "
							+ eElement.getElementsByTagName("domain").item(0)
									.getTextContent());
					System.out.println("username : "
							+ eElement.getElementsByTagName("username").item(0)
									.getTextContent());
					System.out.println("password : "
							+ eElement.getElementsByTagName("password").item(0)
									.getTextContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Capabilities
	public String[] capabilities() {
		return new String[] { serviceName };
	}

	/**
	 * getServiceName() - AppExe server retrieves the information of this
	 * plugin.
	 */
	@Override
	public HeterogeneousMap getServiceName() {

		HeterogeneousMap map = new HeterogeneousMap();
		map.put(DatasetKey.SERVICENAME.getKey(), serviceName);

		// Supply the authorization login URL on for this plugin.
		// plugin console will use the URL to to login on the service that this
		// plugin is connecting.

		/*
		 * JBAT hosoi QanatPlugin use only username and id. for twitterPlugin
		 * that is using consumer_key, consumer_secret and so on. API said that
		 * set callbackURL for "DatasetKey.AUTHORIZE_URL.getKey()".
		 * 
		 * Under code is jbat original code by Mr.Tanaka.
		 * map.put(DatasetKey.AUTHORIZE_URL.getKey(),
		 * "http://console.mobilous.com/en/database-manager/"
		 * +serviceName+"?sid=jbat");
		 */

		// map.put(DatasetKey.AUTHORIZE_URL.getKey(), callbackURL);

		map.put("use_api", true, Boolean.class);
		map.put("pluginfile", pluginFile);

		return map;
	}

	/**
	 * authenticate() - Plugin users (devices) request for connection
	 * authentication.
	 */
	@Override
	public HeterogeneousMap authenticate(HeterogeneousMap dataset) {
		HeterogeneousMap retVal = new HeterogeneousMap();

		if (authtype.equalsIgnoreCase(Constant.OAUTH1.getValue())
				|| authtype.equalsIgnoreCase(Constant.OAUTH2.getValue())) {
			// If the authenticate request contains authorization code, this
			// plugin will exchange the authorization
			// code to access grant.

			if (dataset.get("code") == null) {
				// If the authenticate request does not contain authorization
				// code, this plugin returns the login URL for the device to
				// login and receive the authorization code.

				/*
				 * SALES FORCE SAMPLE CODE OAuth2Parameters oauthParams = new
				 * OAuth2Parameters(); oauthParams.setRedirectUri(callbackURL);
				 * retVal.put("authorize_url",
				 * connectionFactory.getOAuthOperations
				 * ().buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,
				 * oauthParams));
				 */

				retVal.put("authorize_url", "OAUTH authorization URL");

			} else {
				// If the authenticate request contains authorization code, this
				// plugin will exchange the authorization
				// code to access grant.

				// String code = dataset.get("code");
				/*
				 * SALES FORCE SAMPLE CODE //AccessGrant accessGrant =
				 * connectionFactory
				 * .getOAuthOperations().exchangeForAccess(code, callbackURL,
				 * null); retVal.put("auth_type", "OAuth2");
				 * retVal.put("user_connection", accessGrant,
				 * AccessGrant.class);
				 */

				retVal.put("auth_type", "OAuth2");
				retVal.put("user_connection", "OAUTH access grant");
			}
		} else {

			// do nothing because we directory connect with id/password, which
			// includes in configulation file.
			// retVal.put("authorize_url", null);
			// retVal.put("user_connection", "DummyGrant");
			username = dataset.get("username");
			password = dataset.get("password");
			System.out.println("PLUGIN : Authenticate " + serviceName
					+ " using custom with " + username + " / " + password);

			/*
			 * TODO JBAT hosoi Check how to tell the username and password to
			 * Qanat
			 */

			retVal.put("security", authtype);
			retVal.put("auth_type", authtype);
			retVal.put("servicename", serviceName);
			retVal.put("auth_status", "valid");

		}
		return retVal;
	}

	/**
	 * getSchema() - AppExe console request for the tables and its fieldnames to
	 * be set on the application. This function should return the tables and
	 * fieldnames that will be available for AppExe application.
	 */
	@Override
	public HeterogeneousMap getSchema(HeterogeneousMap dataset) {
		HeterogeneousMap schema = new HeterogeneousMap();
		// getSchema request for the list of tables

		/*
		 * JBAT hosoi get schema and tables information with REST Architecture.
		 */
		try {
			String tablename = dataset.get(DatasetKey.TABLE.getKey());
			List<String> tables = new ArrayList<String>();
			if (tablename == null || StringUtils.isBlank(tablename)) {
				System.out.println(">>>>>>>>>>>>>>>>>>Table begin added");

				// TODO REST Architecture to get tables at here

				schema.put(DatasetKey.SCHEMA.getKey(), tables, List.class);
			} else {
				System.out.println(">>>>>>>>>>>>>>>>>>Fields begin added :"
						+ tablename);
				List<String> textFields = new ArrayList<String>();
				List<String> integerFields = new ArrayList<String>();
				List<String> realFields = new ArrayList<String>();
				List<String> dateFields = new ArrayList<String>();
				List<String> boleanFields = new ArrayList<String>();

				// TODO REST Architecture to get Field name by table name at
				// here

				if (textFields.size() > 0) {
					schema.put(DataType.TEXT.getValue(), textFields, List.class);
				}
				if (integerFields.size() > 0) {
					schema.put(DataType.INTEGER.getValue(), integerFields,
							List.class);
				}
				if (realFields.size() > 0) {
					schema.put(DataType.REAL.getValue(), realFields, List.class);
				}
				if (dateFields.size() > 0) {
					schema.put(DataType.DATE.getValue(), dateFields, List.class);
				}
				if (boleanFields.size() > 0) {
					schema.put(DataType.BOOLEAN.getValue(), boleanFields,
							List.class);
				}
			}
		} catch (Exception e) {
			/* TODO errors handle */
			e.getStackTrace();
			schema.put("auth_status", "invalid");
			return schema;
		}

		schema.put("auth_status", "valid");
		return schema;

		/*
		 * String tablename = dataset.get(DatasetKey.TABLE.getKey());
		 * System.out.println("Plugin getSchema : tablename "+tablename); if
		 * (tablename==null || StringUtils.isBlank(tablename)) { // getSchema
		 * request for the list of tables
		 * System.out.println(">>>>>>>>>>>>>>>>>>Table begin added");
		 * List<String> tables = new ArrayList<String>();
		 * 
		 * tables.add("Zaiko"); tables.add("TestTable");
		 * //tables.add("QanatTestTable");
		 * schema.put(DatasetKey.SCHEMA.getKey(), tables, List.class);
		 * 
		 * }else{
		 * System.out.println(">>>>>>>>>>>>>>>>>>Fields begin added :"+tablename
		 * ); if(tablename.equals("TestTable")){ // getSchema request for
		 * table's fieldnames. List<String> textFields = new
		 * ArrayList<String>(); textFields.add("shohincd");
		 * textFields.add("shohinname"); textFields.add("tenpocd");
		 * textFields.add("tenponame"); textFields.add("stocknum");
		 * textFields.add("upddate"); textFields.add("stocktakingdate");
		 * schema.put(DataType.TEXT.getValue(), textFields, List.class);
		 * 
		 * }else if(tablename.equals("QanatTestTable")){ List<String> textFields
		 * = new ArrayList<String>(); textFields.add("f1");
		 * textFields.add("f2"); textFields.add("f3"); textFields.add("f4");
		 * textFields.add("f5"); textFields.add("f6"); textFields.add("f7");
		 * textFields.add("f8"); textFields.add("f9"); textFields.add("f10");
		 * textFields.add("f11"); textFields.add("f12");
		 * schema.put(DataType.TEXT.getValue(), textFields, List.class);
		 * 
		 * }else if(tablename.equals("Zaiko")){
		 * 
		 * List<String> textFields = new ArrayList<String>(); List<String>
		 * integerFields = new ArrayList<String>();
		 * 
		 * textFields.add("KTGRZ"); textFields.add("SCODZ");
		 * textFields.add("SMEIZ"); integerFields.add("HKNOZ");
		 * integerFields.add("ZAIKZ"); integerFields.add("JZAIZ");
		 * integerFields.add("STANZ"); integerFields.add("HTANZ");
		 * integerFields.add("ZKINZ"); textFields.add("TANTZ");
		 * textFields.add("TCODZ");
		 * 
		 * schema.put(DataType.TEXT.getValue(), textFields, List.class);
		 * schema.put(DataType.INTEGER.getValue(), integerFields, List.class);
		 * 
		 * }else{ schema.put("auth_status", "invalid"); return schema; }
		 * 
		 * }
		 */
	}

	// TODO
	/**
	 * numrecord() - Plugin implementation of get the number of records that the
	 * query will return
	 * 
	 * @param dataset
	 *            contains the tablename and where condition.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public HeterogeneousMap numrecord(HeterogeneousMap dataset) {

		HeterogeneousMap map = new HeterogeneousMap();

		/*  */
		System.out.println("PLUGIN numrecord is called");

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		try {
			System.out.println("SELECT COUNT() from " + tablename + " where "
					+ where);
			if (where != null) {
				// FIXME : Query actual query
				String retStr = requestToQanatDummy("numrecord");
			} else {
				// FIXME : Query actual query
				String retStr = requestToQanatDummy("numrecord");
			}

			// FIXME : replace this with the actual number of records result.

			System.out.println(">>>>>>>>>>>>>>>>>>Fields begin added :"
					+ tablename);
			int recodenum = 0;

			// TODO get number of recodes here

			map.put(DatasetKey.NUMBEROFRECORD.getKey(),
					new Integer(recodenum).toString());

			/*
			 * if (tablename.equals("TestTable")) {
			 * 
			 * map.put("numrec", new Integer(3).toString());
			 * 
			 * } else if (tablename.equals("QanatTestTable")) {
			 * 
			 * JSONArray array = requestToQanat(dataset, "Srv01");
			 * map.put("numrec", String.valueOf(array.size()));
			 * 
			 * } else if (tablename.equals("Zaiko")) {
			 * 
			 * map.put("numrec", new Integer(3).toString());
			 * 
			 * } else { throw new Exception("invalid table name: " + tablename);
			 * }
			 * 
			 * map.put("servicename", serviceName); map.put("auth_status",
			 * "valid");
			 */
		} catch (Exception e) {
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		return map;

	}

	// not touched @hosoi
	/**
	 * create() - Plugin implementation of create
	 * 
	 * @param dataset
	 *            contains the tablename and rec
	 */
	@SuppressWarnings("unused")
	@Override
	public HeterogeneousMap create(HeterogeneousMap dataset) {
		System.out.println("PLUGIN create is called");

		HeterogeneousMap map = new HeterogeneousMap();

		String tablename = dataset.get(DatasetKey.TABLE.getKey());
		String rec = dataset.get(DatasetKey.REC.getKey());

		if (rec == null) {
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		org.json.JSONObject setJson = new org.json.JSONObject(rec);
		System.out.println("PLUGIN UPDATE setJson rec " + setJson.toString());
		
		// this is just a sample on how to parse the set "REC", the actual
		// implementation should be based on
		// your system.
		String columnnames = createCommaSeparatedStringFromJson(setJson, 0);
		String values = createCommaSeparatedStringFromJson(setJson, 1);

		try {
			// TODO : Query actual query
			System.out.println("PLUGIN INSERT INTO " + tablename + " "
					+ columnnames + " VALUES " + values);
			String retStr = requestToQanatDummy("create");

			// return the created records to the comserver.
			// for (ResultItem first : queryResult.getRecords()) {
			//
			// Map row = salesforce.sObjectsOperations().getRow(first.getType(),
			// (String) first.getAttributes().get("Id"));
			// Iterator<?> itr = row.keySet().iterator();
			//
			// JSONObject obj = new JSONObject();
			// while (itr.hasNext()) {
			// String key = itr.next().toString();
			// obj.put(key, row.get(key));
			// }
			// arr.add(obj);
			// map.put(DatasetKey.DATA.getKey(), arr, JSONArray.class);
			//
			// }

			map.put(DatasetKey.DATA.getKey(), createDummyResult(),
					JSONArray.class);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;

	}

	// TODO
	/**
	 * read() - Plugin implementation of select
	 * 
	 * <pre>
	 * @param dataset might contain the following
	 *  table : dataset.get(DatasetKey.TABLE.getKey())
	 *  column names : dataset.get(DatasetKey.FIELDS.getKey())
	 *  where condition : dataset.get(DatasetKey.WHERE.getKey())
	 *  sort order : dataset.get(DatasetKey.ORDER.getKey())
	 *  limit : dataset.get(DatasetKey.LIMIT.getKey())
	 *  offset : dataset.get(DatasetKey.OFFSET.getKey())
	 * </pre>
	 */
	@Override
	public HeterogeneousMap read(HeterogeneousMap dataset) {
		HeterogeneousMap map = new HeterogeneousMap();

		System.out.println("PLUGIN read is called");

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		try {
			System.out.println("PLUGIN Select * from " + tablename + "where "
					+ where);

			if (where != null) {
				// FIXME : Query actual query
			} else {
				// FIXME : Query actual query
			}

			// FIXME return Result JSON data
			System.out.println(">>>>>>>>>>>>>>>>>>Fields begin added :"
					+ tablename);

//			JSONArray jsonarray = requestToQanatWithRest(dataset, tablename);
	//		map.put(DatasetKey.DATA.getKey(), jsonarray, JSONArray.class);

			/*
			 * if (tablename.equals("TestTable")) {
			 * 
			 * map.put(DatasetKey.DATA.getKey(), createDummyResult(),
			 * JSONArray.class); } else if (tablename.equals("QanatTestTable"))
			 * {
			 * 
			 * JSONArray array = requestToQanat(dataset, "Srv01");
			 * map.put(DatasetKey.DATA.getKey(), array, JSONArray.class);
			 * 
			 * } else if (tablename.equals("Zaiko")) {
			 * 
			 * JSONArray array = requestToQanat(dataset, "StockMaster");
			 * map.put(DatasetKey.DATA.getKey(), array, JSONArray.class); //
			 * map.put(DatasetKey.DATA.getKey(), // createDummyResultForZaiko(),
			 * JSONArray.class);
			 * 
			 * } else { throw new Exception("invalid table name: " + tablename);
			 * }
			 */
			map.put("servicename", serviceName);
			map.put("auth_status", "valid");

		} catch (Exception e) {
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		return map;

	}

	// not touched @hosoi
	/**
	 * update() - Plugin implementation of update
	 * 
	 * @param dataset
	 *            contains the tablename , fieldname, rec, and where condition.
	 */
	@SuppressWarnings("unused")
	@Override
	public HeterogeneousMap update(HeterogeneousMap dataset) {
		System.out.println("PLUGIN udpate is called");
		HeterogeneousMap map = new HeterogeneousMap();

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		String rec = dataset.get(DatasetKey.REC.getKey());

		org.json.JSONObject setJson = new org.json.JSONObject(rec);
		System.out.println("PLUGIN UPDATE json rec " + setJson.toString());

		// this is just a sample on how to parse the set "REC", the actual
		// implementation should be based on
		// your system.
		String setquery = createSetStringFromJson(setJson);

		if (setquery.length() <= 0) {
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		try {
			// QueryResult aggregate;
			if (where != null) {
				// TODO : Query actual query
				System.out.println("PLUGIN UPDATE " + tablename + " SET "
						+ setquery + " where " + where);
				String retStr = requestToQanatDummy("update");
			} else {
				// TODO : Query actual query
				System.out.println("PLUGIN UPDATE " + tablename + " SET "
						+ setquery);
				String retStr = requestToQanatDummy("update");
			}

			// return the updated records to the comserver.
			// for (ResultItem first : queryResult.getRecords()) {
			//
			// Map row = salesforce.sObjectsOperations().getRow(first.getType(),
			// (String) first.getAttributes().get("Id"));
			// Iterator<?> itr = row.keySet().iterator();
			//
			// JSONObject obj = new JSONObject();
			// while (itr.hasNext()) {
			// String key = itr.next().toString();
			// obj.put(key, row.get(key));
			// }
			// arr.add(obj);
			// map.put(DatasetKey.DATA.getKey(), arr, JSONArray.class);
			//
			// }
			// return the updated records to the comserver.

			// TODO : for training purpose only
			map.put(DatasetKey.DATA.getKey(), createDummyResult(),
					JSONArray.class);

		} catch (Exception e) {
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;
	}

	// not touched @hosoi
	/**
	 * update() - Plugin implementation of delete
	 * 
	 * @param dataset
	 *            contains the tablename and where condition.
	 */
	@SuppressWarnings("unused")
	@Override
	public HeterogeneousMap delete(HeterogeneousMap dataset) {
		System.out.println("PLUGIN delete is called");
		HeterogeneousMap map = new HeterogeneousMap();

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		try {
			// QueryResult aggregate;
			if (where != null) {
				System.out.println("PLUGIN DELETE FROM " + tablename
						+ " where " + where);
				// TODO : Query actual query
				String retStr = requestToQanatDummy("delete");
			} else {
				// basically we shouldnt allow delete request without where
				// condition.
				// for this purpose we allow that one.
				System.out.println("PLUGIN DELETE FROM " + tablename);
				// TODO : Query actual query
				String retStr = requestToQanatDummy("delete");
			}

			// return the updated records to the comserver.
			// for (ResultItem first : queryResult.getRecords()) {
			//
			// Map row = salesforce.sObjectsOperations().getRow(first.getType(),
			// (String) first.getAttributes().get("Id"));
			// Iterator<?> itr = row.keySet().iterator();
			//
			// JSONObject obj = new JSONObject();
			// hile (itr.hasNext()) {
			// String key = itr.next().toString();
			// obj.put(key, row.get(key));
			// }
			// arr.add(obj);
			// map.put(DatasetKey.DATA.getKey(), arr, JSONArray.class);
			//
			// }

			map.put(DatasetKey.DATA.getKey(), createDummyResult(),
					JSONArray.class);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;
	}

	
	
	/**
	 * ForREST Architecture BeanClass NOT used
	 * 
	 * @author JBAT hosoi
	 * @param dataset
	 * @param qanatServiceName
	 * @return
	 * @throws ParseException
	 */
	private TableData requestToQanatWithRest(HeterogeneousMap dataset,
			String qanatServiceName) throws ParseException {

		Client client = ClientBuilder.newClient()
				.register(JacksonFeature.class)
				.register(ClientResponseLoggingFilter.class);

		String qanatDomain = "http://" + consumerKey + ":" + port + "/qanat";

		// TODO set bean 
		TableData tableData = new TableData();
		TableData tableDataPersisted = client
				.target("http://" + consumerKey + ":6200/qanat") // qanat
				.request()
				.post(Entity.entity(tableData, MediaType.APPLICATION_JSON),
						TableData.class);

		// Fetch tableData and the data were set all parameter
		
		System.out.println("*********************************************************");
		TableData data = client.target(qanatDomain).request().get(TableData.class);
		System.out.println("tableData :" + data);
		System.out.println("tableData.getTable() :" + data.getTable());

		return data;
	}
	

	/**
	 * ugokebaeenen
	 * 
	 * @param dataset
	 * @param qanatServiceName
	 * @return
	 * @throws ParseException
	 */
	private JSONObject requestToQanat(HeterogeneousMap dataset,
			String qanatServiceName) throws ParseException {

		Client client = ClientBuilder.newBuilder().newClient();
		WebTarget target = client.target(consumerKey + ":" + port + "/qanat/rest");// "http://10.60.57.118/appexe"
		target = target.path(qanatServiceName);

//		  if you sent parameter at URL. set queryParams.  
//		 	    .queryParam("service", qanatServiceName)
//				.queryParam("params[args0]", username)
//				.queryParam("params[args1]", password);

		Invocation.Builder builder = target.request();
//		Response response = builder.get();
		String jstr = makeJsonObject(qanatServiceName);
//		Response response = builder.post(Entity.json(jstr));
		
		String resStr = builder.post(Entity.json(JSONObject.class), String.class);
		
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj = (JSONObject) (new JSONParser().parse(resStr));
		} catch (ClassCastException e) {
			// getting a not-Json expression string, for example "Error".
			System.err.println("JSONParser.parse : " + resStr);
			throw e;
		} catch (ParseException e) {
			throw e;
		}
		return jsonobj;
	}
	
	private String makeJsonObject(final String serviceName){
		Map<String, Object> obj = new LinkedHashMap<String, Object>();

		if(serviceName.equalsIgnoreCase("qanatapptablelist")){
			obj.put("user", username);
			obj.put("pass", password);
			obj.put("utype", 0);
		}
		
		String jsonText = JSONValue.toJSONString(obj);
		return jsonText;
	}

	public String requestToQanatDummy(String qanatServiceName)
			throws ParseException {

		Client client = ClientBuilder.newBuilder().newClient();
		WebTarget target = client.target(domain + "/appexe");
		target = target.path("WebService")
				.queryParam("dummy", qanatServiceName);
		Invocation.Builder builder = target.request();
		Response response = builder.get();
		String jsonStr = builder.get(String.class);

		return builder.get(String.class);

	}

	// Reference code only, not from template code.
	@SuppressWarnings("unchecked")
	private String createSetStringFromJson(org.json.JSONObject jsonobject) {

		Iterator<String> itr = jsonobject.keys();
		StringBuilder setquery = new StringBuilder();

		while (itr.hasNext()) {
			String key = itr.next();
			String value = jsonobject.getString(key);
			setquery.append(key).append("=").append(value).append(", ");
		}
		setquery.delete(setquery.length() - 2, setquery.length());
		return setquery.toString();

	}

	/**
	 * @param location
	 *            = 0 : fieldnames ,location = 1 : values
	 * */
	@SuppressWarnings("unchecked")
	private String createCommaSeparatedStringFromJson(
			org.json.JSONObject jsonobject, int location) {

		Iterator<String> itr = jsonobject.keys();
		StringBuilder result = new StringBuilder();

		while (itr.hasNext()) {
			if (location == 0) {
				String key = itr.next();
				result.append(key).append(",");
			} else if (location == 1) {
				String key = itr.next();
				result.append(jsonobject.get(key)).append(",");
			}

		}
		result.delete(result.length() - 1, result.length());

		return result.toString();
	}

	@SuppressWarnings("unchecked")
	private JSONArray createDummyResult() {

		JSONArray arr = new JSONArray();
		try {

			JSONObject obj = new JSONObject();
			obj.put("shohincd", "cd1");
			obj.put("shohinname", "name1");
			obj.put("tenpocd", "tempcd");
			obj.put("tenponame", "tenponame");
			obj.put("stocknum", "stocknum");
			obj.put("upddate", "2013-11-11-00:00:00");
			obj.put("stocktakingdate", "2013-11-11-00:00:00");
			arr.add(obj);

			JSONObject obj2 = new JSONObject();
			obj2.put("shohincd", "cd2");
			obj2.put("shohinname", "name2");
			obj2.put("tenpocd", "tempcd");
			obj2.put("tenponame", "tenponame");
			obj2.put("stocknum", "stocknum");
			obj2.put("upddate", "2014-11-11-00:00:00");
			obj2.put("stocktakingdate", "2014-11-11-00:00:00");
			arr.add(obj2);

			JSONObject obj3 = new JSONObject();
			obj3.put("shohincd", "cd3");
			obj3.put("shohinname", "name3");
			obj3.put("tenpocd", "tempcd");
			obj3.put("tenponame", "tenponame");
			obj3.put("stocknum", "stocknum");
			obj3.put("upddate", "2015-11-11-00:00:00");
			obj3.put("stocktakingdate", "2015-11-11-00:00:00");
			arr.add(obj3);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

}
