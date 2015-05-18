package info.rmapproject.api.authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

public class AuthUserToAgentMediator {
	
	private String PROPERTIES_FILEPATH = "/rmap/apache/tomcat7/conf/rmap_apitest_userNameToSysAgent.map";
	//private String PROPERTIES_FILEPATH = "C:/rmap_apitest_userNameToSysAgent.map";
	/*
	private String propertiesFileName = null;

	@Required
	public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }
	
    public String getPropertiesFileName() throws Exception {
        return this.propertiesFileName;
    }*/
    
	public AuthUserToAgentMediator(){}
	
	public URI getRMapAgentForUser(String username) throws Exception {
		URI systemAgentUri = null;
		Properties props = getUserSysAgentMap();
		if (props!=null){
			Object systemAgent = props.get(username);
			if (systemAgent!=null){
				systemAgentUri = new URI(systemAgent.toString());
			}			
		}
		return systemAgentUri;
	}
	
	public void setRMapAgentForUser(String username, String systemAgentUri) throws Exception {
		Properties props = getUserSysAgentMap();
		props.setProperty(username, systemAgentUri);
		File f = new File(PROPERTIES_FILEPATH);
		OutputStream out = new FileOutputStream( f );
		props.store(out, "saved user name to sys agent map");
		out.close();
	}
	
	private Properties getUserSysAgentMap() throws Exception {
		Properties props = new Properties();
		checkMapFileExists(PROPERTIES_FILEPATH);
		File file = new File(PROPERTIES_FILEPATH);		
		InputStream input = new FileInputStream( file );
		props.load(input);
		input.close();
		return props;
	}	
	
	private void checkMapFileExists(String filename) throws Exception{
		Properties props = new Properties();
		File file = new File(PROPERTIES_FILEPATH);
		if (file.createNewFile())	{
			props.setProperty("rmap", "ark:/22573/rmd3jq0");
			OutputStream out = new FileOutputStream( file );
			props.store(out, "saved user name to sys agent map");
			out.close();
		}
	}
	

}
