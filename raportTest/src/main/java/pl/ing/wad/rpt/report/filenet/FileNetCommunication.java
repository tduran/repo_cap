package pl.ing.wad.rpt.report.filenet;

import pl.ing.wad.rpt.report.ReportConfigs;

public class FileNetCommunication implements IFileNetCommunication {
	
	private ContentEngineSession contentEngineSession = null;
	private ReportConfigs configs = ReportConfigs.getInstance();

	
	public FileNetCommunication() throws Exception {
		contentEngineSession = new ContentEngineSession();
		contentEngineSession.loginCE(	configs.getAppParmValue("CE_PASS"), 
										configs.getAppParmValue("CE_USER"), 
										configs.getAppParmValue("WCM_API_CONFIG_FILE_PATH"));
		//TODO implement
	}
	
	public void deleteReport(String id) {
		//TODO implement
	}
	
	public void testFile() throws Exception {
		//TODO test
	
	}
	
	public void deleteDocument(String id) {
		//TODO implement
	}
	
	public String createDocument(String pathToSystemFile) throws Exception {
		return null;
		//TODO implement
	}	
}
