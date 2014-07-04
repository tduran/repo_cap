package pl.ing.wad.rpt.report.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pl.ing.wad.rpt.report.ReportConfigs;
import pl.ing.wad.rpt.report.dao.ReportBaseDAO;

public class RptService extends ReportBaseDAO {

	private ReportConfigs configs = ReportConfigs.getInstance();

	// TD configloader zastapil ReportStaticValues jako miejsce przechowywania
	// parametrow aplikacji
	// ReportStaticValues reportStaticValues = ReportStaticValues.getInstance();

	public RptService() {}

	public void startService() throws Exception {

		String reportSql = "";
		Number orderId = 0;

		// na potrzeby testow
/*		{
			Connection connection = getConnection();
			Statement st = connection.createStatement();
			st.executeUpdate("update RPT_ORDERED SET ORD_OST_ID = 1");
		}*/

		//Thread.sleep(1000);

		try {
			Connection connection = getConnection();
			// Statement st = connection.createStatement(); TD to zostanie zastapione funkcja preparedstatement co da nam mozliwosc dynamicznej zmiany parametrow zapytania
			System.out.println("Proba pobrania zamowienia z bazy danych.");

			// ResultSet rs = st.executeQuery("select * from RPT_ORDERED where rownum = 1");//pobieramy jeden wiersz do przetworzenia
			PreparedStatement prst = connection.prepareStatement("select * from RPT_ORDERED"); // where ORD_OST_ID = ? and rownum = ?");

			//prst.setInt(1, 1);
			//prst.setInt(2, 1);
			ResultSet rs = prst.executeQuery();// pobieramy jeden wiersz do
												// przetworzenia
			while (rs.next()) {
				reportSql = rs.getString("ORD_QUERY");
				orderId = rs.getInt("ORD_ID");
				String header = rs.getString("ORD_HEADER");
				String generator = rs.getString("ORD_GENERATOR");
				System.out.println("Przetwarzeanie zamowienia o id = "	+ orderId.toString());
				//do celow testowych exp_date jeden dzien do tylu
				PreparedStatement update = connection.prepareStatement("update RPT_ORDERED SET ORD_OST_ID = ?, ORD_EXP_DATE = trunc(SYSDATE-1) WHERE ORD_ID = ?");
				update.setInt(1, 4);
				update.setLong(2, orderId.longValue());
				update.executeUpdate();
				connection.commit();
				if ("DEFAULT".equals(generator) || generator == null) {
					new RptWorker().generateReport(reportSql, orderId, header);
				} else if ("XFILES".equals(generator)) {
					new RptWorkerXfiles().generateReport(reportSql, orderId,header);
				}
			} 
/*			else {
				System.out.println("Brak zamowien...zamykam serwis batchowy");
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
