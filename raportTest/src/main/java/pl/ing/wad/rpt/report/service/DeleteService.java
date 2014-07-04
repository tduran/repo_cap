package pl.ing.wad.rpt.report.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pl.ing.wad.rpt.report.dao.ReportBaseDAO;
import pl.ing.wad.rpt.report.filenet.FileNetCommunication;

public class DeleteService extends ReportBaseDAO {

	//ReportStaticValues reportStaticValues = ReportStaticValues.getInstance();

	public DeleteService() {}

	public void startService() throws Exception {

		Number orderId = 0;
		boolean deleteReports = true;
		
		System.out.println("Start kasowania starych raportow.");
		if (deleteReports) {

			//Thread.sleep(1000);
			
			try {
				Connection connection = getConnection();
				PreparedStatement st = connection.prepareStatement("select * from RPT_ORDERED where ORD_EXP_DATE < trunc(SYSDATE)");
				//PreparedStatement st = connection.prepareStatement("select * from RPT_ORDERED where ORD_OST_ID = ? and ORD_EXP_DATE < trunc(SYSDATE)");
				//st.setInt(1, 2);
				//st.setInt(2, 1);
				ResultSet rs = st.executeQuery();
				int deletedReportsCount = 0;
				while(rs.next()) {
					String resultPath = rs.getString("ORD_RESULT_PATH");
					orderId = rs.getInt("ORD_ID");
					FileNetCommunication fileNetCommunication = new FileNetCommunication();
					fileNetCommunication.deleteDocument(resultPath);
					System.out.println("Kasowanie zamowienia o id = " + orderId);
					st = connection.prepareStatement("delete from RPT_ORDERED WHERE ORD_ID = ?");
					st.setLong(1, orderId.longValue());
					st.executeUpdate();
					connection.commit();
					
					File deleteThis = new File(resultPath);
					if(deleteThis.delete()){
						System.out.println("Usunieto plik: " + deleteThis.getPath() +" dla raportu o Id: "+ orderId);
						deletedReportsCount += 1;
					}else{
						System.out.println("Nie udala sie proba usuniecia pliku: " + deleteThis.getPath());
					}
				}
				
				System.out.println("Usunieto "+ deletedReportsCount +" raportow.");
				deleteReports = false;
				
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}
		
		
	}
	
}
