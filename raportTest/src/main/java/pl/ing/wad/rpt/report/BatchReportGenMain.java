package pl.ing.wad.rpt.report;

import pl.ing.wad.rpt.report.service.DeleteService;
import pl.ing.wad.rpt.report.service.RptService;

public class BatchReportGenMain {

	public static void main(String[] args) {
		
		try {
			ReportConfigs configs = ReportConfigs.getInstance();
			configs.loadConfiguration(args[0]);
			System.out.println("Startuje serwis batchowy kasowania raportow RPT");
			//TD po przerzuceniu parametrow aplikacji do pliku xml ReportStaticValues staje sie zbedne
			//ReportStaticValues reportStaticValues = ReportStaticValues.getInstance();
			System.setProperty("java.security.auth.login.config",configs.getAppParmValue("JaasFilePath"));
			new DeleteService().startService();	
			System.out.println("Startuje serwis batchowy raportow RPT");
			new RptService().startService();
			System.out.println("Serwis batchowy raportow RPT zatrzymany...CU later :]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
