package com.mobilous.ext.plugin.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.co.jbat.qanat.rest.entity.QanatErrorResponse;
import jp.co.jbat.qanat.rest.entity.QanatRequest;
import jp.co.jbat.qanat.rest.entity.QanatRequestMember;
import jp.co.jbat.qanat.rest.entity.QanatResponse;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilous.ext.plugin.PluginService;
import com.mobilous.ext.plugin.constant.Constant;
import com.mobilous.ext.plugin.constant.DatasetKey;
import com.mobilous.ext.plugin.schema.DataType;
import com.mobilous.ext.plugin.util.HeterogeneousMap;

/**
 * AppExe For Qanat Plugin
 * @author yutaro hosoi(JBAT)
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
	@SuppressWarnings("unused")
	private static String callbackURL;
	private static String domain;
	private static String port;  // Using consumerSecret
	private static String pluginFile;
	private static String pluginConfigFile;
	
	// for qanat
	private static String userType = "310";
	// TODO make Property DATA
	private static final String TABLELIST = "qanapptablelist";
	private static final String COLUMNDATA = "qanapprecordinfo";
	private static final String RECODENUM = "qanapprecordcount";
	private static final String DATAVALUE = "qanapprecordvalue";


	public PluginServiceImpl() {

		// default values
		serviceName = "Qanat";
		port = "80";

// TODO For TEST
//consumerKey = "54.65.87.122";
consumerKey = "10.60.46.241";
username = "cvadmin";
password = "";
authtype = "custom";
domain = "syajims01.mobilous.com";

		// Do not change
		callbackURL = "https://" + domain
				+ ":8181/commapi/extsvc/authenticate?servicename="
				+ serviceName;

		pluginFile = "appexe-commapi-ext-plugin-qanat.jar";

		// update the plugin config filename should be same with plugin filename
		// the, extension name shoud be ".xml" if the plugin filename will be changed
		// update this
		
		pluginConfigFile = "appexe-commapi-ext-plugin-qanat.xml";

		loadSettingsFromConfigfile();

	}

	
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
					System.out.println("servicename : "
							+ serviceName);
					
					consumerKey = eElement.getElementsByTagName("consumerkey")
							.item(0).getTextContent();
					System.out.println("consumerKey : "
							+ consumerKey);
					
					// !! cousumerSecret is Qanat port number. !!
					port = eElement.getElementsByTagName("consumerSecret")
							.item(0).getTextContent();
					System.out.println("(port)consumerSecret : "
							+ port);
					
					domain = eElement.getElementsByTagName("domain").item(0)
							.getTextContent();
					System.out.println("domain : "
							+ domain);
					
					username = eElement.getElementsByTagName("username")
							.item(0).getTextContent();
					System.out.println("username : "
							+ username);
					
					password = eElement.getElementsByTagName("password")
							.item(0).getTextContent();
					System.out.println("password : "
							+ password);
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
		// map.put(DatasetKey.AUTHORIZE_URL.getKey(), callbackURL); //optional

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

				retVal.put("auth_status", "invalid");	// USE CUSTOM
				retVal.put(DatasetKey.AUTH_TYPE.getKey(), "OAuth2");
				retVal.put(DatasetKey.SERVICENAME.getKey(), serviceName);

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
				retVal.put("auth_status", "invalid");	// USE CUSTOM
				retVal.put(DatasetKey.AUTH_TYPE.getKey(), "OAuth2");
				retVal.put(DatasetKey.SERVICENAME.getKey(), serviceName);
			}
		} else {

			// do nothing because we directory connect with id/password.
			
			if(!(username.isEmpty() || password.isEmpty())){
				retVal.put("auth_status", "invalid");	
			}
			
			retVal.put(DatasetKey.AUTH_TYPE.getKey(), authtype);
			retVal.put(DatasetKey.SERVICENAME.getKey(), serviceName);
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
		try {
			String tablename = dataset.get(DatasetKey.TABLE.getKey());
			List<String> tables = new ArrayList<String>();
			
			if (tablename == null || StringUtils.isBlank(tablename)) {
				System.out.println("[QanatPlugin] >>>>>>>>>>>>>>>>>>Table begin added");
				JSONArray data_a = requestToQanat(TABLELIST, tablename);
				
				if(data_a == null || data_a.isEmpty()){
					System.out.println("[QanatPlugin] [ERROR] request Error [Check DataBase Username Password");
					schema.put("auth_status", "invalid");
					return schema;
				} else {
					System.out.println("[QanatPlugin] Response comming");
				}
							
				for(Integer i = 0; i < data_a.size(); i++){
					JSONObject tableData = (JSONObject)data_a.get(i);
					String  tableName= (String)tableData.get(i.toString());
					tables.add(tableName);
				}
				System.out.println("[QanatPlugin] Tables : " + tables);
				schema.put(DatasetKey.SCHEMA.getKey(), tables, List.class);
				
			} else {
				
				System.out.println("[QanatPlugin] >>>>>>>>>>>>>>>>>>Fields begin added :"
						+ tablename);
				
				List<String> textFields = new ArrayList<String>();
				List<String> integerFields = new ArrayList<String>();
				List<String> realFields = new ArrayList<String>();
				List<String> dateFields = new ArrayList<String>();
				List<String> booleanFields = new ArrayList<String>();
			
				JSONArray data_a = requestToQanat(COLUMNDATA, tablename);
				
				if(data_a == null || data_a.isEmpty()){
					System.out.println("[QanatPlugin] requestError ");
					schema.put("auth_status", "invalid");
					return schema;
				}else {
					System.out.println("[QanatPlugin] Response comming");
				}
			
				for(int i = 0; i < data_a.size(); i++){
					JSONObject fieldData = (JSONObject)data_a.get(i);
					String fieldType = (String)fieldData.get("Type");
					String fieldName = (String)fieldData.get("Name");
					switch(fieldType){
					case "TEXT":
						textFields.add(fieldName);
						break;
					case "INT":
						integerFields.add(fieldName);
						break;
					case "REAL":
						realFields.add(fieldName);
						break;
					case "DATE":
						dateFields.add(fieldName);
						break;
					case "BOOL":
						booleanFields.add(fieldName);
						break;
					default :
						System.out.println("[QanatPlugin] Strange Type comming");
					}
				}

				if (textFields.size() > 0) {
					System.out.println("[QanatPlugin] TEXT Field : " + textFields);
					schema.put(DataType.TEXT.getValue(), textFields, List.class);
				}
				if (integerFields.size() > 0) {
					System.out.println("[QanatPlugin] INTEGER Field : " + integerFields);
					schema.put(DataType.INTEGER.getValue(), integerFields,
							List.class);
				}
				if (realFields.size() > 0) {
					System.out.println("[QanatPlugin] REAL Field : " + realFields);
					schema.put(DataType.REAL.getValue(), realFields, List.class);
				}
				if (dateFields.size() > 0) {
					System.out.println("[QanatPlugin] DATE Field : " + dateFields);
					schema.put(DataType.DATE.getValue(), dateFields, List.class);
				}
				if (booleanFields.size() > 0) {
					System.out.println("[QanatPlugin] BOOLEAN Field : " + booleanFields);
					schema.put(DataType.BOOLEAN.getValue(), booleanFields,
							List.class);
				}
			}
		} catch (Exception e) {
			System.err.println("[QanatPlugin] getSchema Error");
			e.printStackTrace();
			schema.put("auth_status", "invalid");
			return schema;
		}

		schema.put("auth_status", "valid");
		return schema;
	}

	/**
	 * numrecord() - Plugin implementation of get the number of records that the
	 * query will return
	 * 
	 * @param dataset
	 *            contains the tablename and where condition.
	 */
	@Override
	public HeterogeneousMap numrecord(HeterogeneousMap dataset) {

		HeterogeneousMap map = new HeterogeneousMap();

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		System.out.println("[QanatPlugin] >>>>>>>>>>>>>>>>>>numrecord begin : "
				+ tablename);
		
		try {
			System.out.println("[QanatPlugin] SELECT COUNT() from " + tablename + " where "
					+ where);
			if (where != null) {
//				String retStr = requestToQanatDummy("numrecord");
			} else {
//				String retStr = requestToQanatDummy("numrecord");
			}

			JSONArray data_a = requestToQanat(RECODENUM, tablename);
			
			if(data_a == null || data_a.isEmpty()){
				System.out.println("[QanatPlugin] requestError Occurred");
				map.put("servicename", serviceName);
				map.put("auth_status", "invalid");
				return map;
			}
			
			JSONObject numobj = (JSONObject)data_a.get(0);
			String recodenum = numobj.get("Count").toString();

			map.put(DatasetKey.NUMBEROFRECORD.getKey(), recodenum);

		} catch (Exception e) {
			System.err.println("[QanatPlugin] numrecord Error");
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}
		
		map.put("servicename", serviceName);
		map.put("auth_status", "valid");		
		return map;

	}

	/**
	 * create() - Plugin implementation of create
	 * 
	 * @param dataset
	 *            contains the tablename and rec
	 */
	@Override
	public HeterogeneousMap create(HeterogeneousMap dataset) {
		System.out.println("[QanatPlugin] PLUGIN create is called");

		HeterogeneousMap map = new HeterogeneousMap();

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;

	}

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

		String where = dataset.get(DatasetKey.WHERE.getKey());
		String tablename = dataset.get(DatasetKey.TABLE.getKey());

		try {
			System.out.println("[QanatPlugin] PLUGIN Select * from " + tablename + "where "
					+ where);

			// in Qanat "where" was not used.
			System.out.println("[QanatPlugin] >>>>>>>>>>>>>>>>>> Read Begin:"
					+ tablename);
			
			JSONArray data_a = requestToQanat(DATAVALUE, tablename);
			if(data_a == null || data_a.isEmpty()){
				System.out.println("[QanatPlugin] [ERROR] request Error Occurred");
				map.put("servicename", serviceName);
				map.put("auth_status", "invalid");
				return map;
			}
			


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
			map.put(DatasetKey.DATA.getKey(), data_a, JSONArray.class);

		} catch (Exception e) {
			System.err.println("[QanatPlugin] [ERROR] Read Error");
			e.printStackTrace();
			map.put("servicename", serviceName);
			map.put("auth_status", "invalid");
			return map;
		}
		map.put("servicename", serviceName);
		map.put("auth_status", "valid");
		return map;

	}

	/**
	 * update() - Plugin implementation of update
	 * 
	 * @param dataset
	 *            contains the tablename , fieldname, rec, and where condition.
	 */
	@Override
	public HeterogeneousMap update(HeterogeneousMap dataset) {
		System.out.println("[QanatPlugin] PLUGIN udpate is called");
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;
	}

	/** 
	 * update() - Plugin implementation of delete
	 * 
	 * @param dataset
	 *            contains the tablename and where condition.
	 */
	@Override
	public HeterogeneousMap delete(HeterogeneousMap dataset) {
		System.out.println("[QanatPlugin] PLUGIN delete is called");
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("servicename", serviceName);
		// map.put("auth_status", "valid");
		map.put("auth_status", "invalid");
		return map;
	}

	/**
	 * ForREST Architecture Using 
	 * 
	 * @author JBAT
	 * @param dataset
	 * @param serviceURL
	 * @return
	 * @throws ParseException
	 * @throws JsonProcessingException 
	 * @throws Exception
	 */
	private JSONArray requestToQanat(final String serviceURL,
			final String tablename) throws ParseException, JsonProcessingException, Exception {
		System.out.println("[QanatPlugin] Request Start");
		String qanatDomain = "http://" + consumerKey + ":" + port + "/qanat/rest";
		System.out.println("[QanatPlugin] Request Service : " + serviceURL);
		System.out.println("[QanatPlugin] Request table   : " + tablename);
		System.out.println("[QanatPlugin] Request URL     : " + qanatDomain + "/" + serviceURL);


		// Define Request
		QanatRequest request = new QanatRequest();
		QanatRequestMember requestMember = makeRequestMember(serviceURL, tablename);
		if (requestMember == null) {
			System.out.println("[QanatPlugin] [ERROR] make requestMember Error");
			throw new IllegalArgumentException("不正なメンバです。");
		}
		request.getData().add(requestMember);
		
		// Show Request Json
		String jsonstr;
		ObjectMapper mapper = new ObjectMapper();
		jsonstr = mapper.writeValueAsString(request);
		System.out.println("requestData _start");
		System.out.println(jsonstr);
		System.out.println("requestData_end");
		
		// Fetch Data
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(qanatDomain).path(serviceURL);

		Response response = target.request().post(Entity.entity(request, MediaType.APPLICATION_JSON));
		if(response.getStatus() == 200){
			QanatResponse data = new QanatResponse();			
			data = response.readEntity(QanatResponse.class);
			jsonstr = mapper.writeValueAsString(data.getData());
			JSONArray json_a = (JSONArray)JSONValue.parse(jsonstr);
			System.out.println("responseData _start");
			System.out.println(json_a);
			System.out.println("responseData_end");
			return json_a;
		} else if (response.getStatus() == 404) {
			System.out.println("[QanatPlugin] [ERROR] Qanat Server NOT found.");
			return null;
		} else if (response.getStatus() == 500){
			System.out.println("[QanatPlugin] [ERROR] Qanat Server Internal Error Occurred");

			QanatErrorResponse error = new QanatErrorResponse();
			error = response.readEntity(QanatErrorResponse.class);
			jsonstr = mapper.writeValueAsString(error.getError());
			System.out.println("500Error _start");
			System.out.println("Qanat Message : " + error.getError().getMessage());
			System.out.println("Qanat Trace   : " + error.getError().getTrace());
			System.out.println(jsonstr);
			System.out.println("500seError_end");
			return null;
		} else if (response.getStatus() == 503){
			System.out.println("[QanatPlugin] [ERROR] Qanat Server Service Unavailable");
			return null;
		} else {
			System.out.println("[QanatPlugin] [ERROR] Qanat Server Responsed : " + response.getStatus());
			return null;
		}

	}
	
	/**
	 * Set ReqestMember
	 * @param serviceURL
	 * @param tablename
	 * @return
	 */
	private QanatRequestMember makeRequestMember(final String serviceURL,
			final String tablename) {
		QanatRequestMember reqmem = new QanatRequestMember();
		reqmem.setUser(username);
		reqmem.setPass(password);
		if (serviceURL.equals("qanapptablelist")) {
			reqmem.setUtype(userType);
		} else if (serviceURL.equals("qanapprecordinfo")) {
			reqmem.setTablename(tablename);
			reqmem.setUtype(userType);
		} else if (serviceURL.equals("qanapprecordcount")) {
			reqmem.setTablename(tablename);
			reqmem.setUtype(userType);
		} else if (serviceURL.equals("qanapprecordvalue")) {
			reqmem.setTablename(tablename);
			reqmem.setUtype(userType);
		} else {
			System.out.println("undefined servicename");
			return null;
		}

		return reqmem;
	}

}
