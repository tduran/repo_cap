package pl.ing.wad.rpt.report;

import pl.ing.wad.rpt.report.tools.ConfigLoader;

public class ReportConfigs {

    private ConfigLoader configLoader = new ConfigLoader();
    private String connectionString = "";
   
    private static ReportConfigs instance = null;
    public static ReportConfigs getInstance() {
        if (instance == null) {
            instance = new ReportConfigs();
        }
        return instance;
    }

    /**
     * Metoda do czytania wartosci parametrow pobranych z pliku konfiguracyjnego przy starcie
     * @author TDuran
     * @param parName
     * @return
     */
    public String getAppParmValue(String parName)
    {
    	return configLoader.getElementValue(parName);
    }
    
    public void loadConfiguration(String filePath) throws Exception
    {
        try
        {
            configLoader.loadConfigFile(filePath);
            this.connectionString = configLoader.getElementValue("dburl");
            System.out.println(connectionString);
            System.out.println("Konfiguracja zaladowana===============");
            System.out.println("Ladowanie wartosci statycznych...");
        }
        catch (Exception ex)
        {
            throw new Exception("Blad ladowania konfiguracji: "  + ex.toString());
        }
        
        //TD po przerzuceniu parametrow aplikacji do pliku xml ReportStaticValues staje sie zbedne
       /* try
        {
        	
            ReportStaticValues reportStaticValues = ReportStaticValues.getInstance();
            reportStaticValues.load_app_parameters();
            System.out.println("Ladowanie wartosci statycznych...OK");
        }
        catch (Exception ex)
        {
            System.out.println("Blad ladowania wartosci statycznych:\n" + ex.toString());
        }
        */
    }

	public String getConnectionString() {
		return connectionString;
	}

}
