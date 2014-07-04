package pl.ing.wad.rpt.report.service;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.ing.wad.rpt.report.CsvUtils;
import pl.ing.wad.rpt.report.ReportStaticValues;
import pl.ing.wad.rpt.report.dao.ReportBaseDAO;
import pl.ing.wad.rpt.report.filenet.FileNetCommunication;

public class RptWorkerXfiles extends ReportBaseDAO {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String outFileName = "Report_rpt_"+ sdf.format(new Date()) + ".csv";
	ReportStaticValues reportStaticValues = ReportStaticValues.getInstance();

	public RptWorkerXfiles() {
		super();
	}

	public void generateReport(String reportSql, Number orderId, String header) throws Exception {

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet rs2 = null;  
		PreparedStatement pst=null;
		
		PrintWriter out = null;
		
		outFileName = orderId.toString() + "_" + outFileName;
		
		String filePath = reportStaticValues.getAppParmValue("REPORT_OUT_FOLDER") + outFileName;
		System.out.println("----------------Rozpoczeto " + new Date());
		System.out.println("Generacja raportu do pliku:" + filePath);
		System.out.println("Wykorzystane zapytanie:" + reportSql);

		try {

			conn = getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(reportSql);
			pst = conn.prepareStatement("select XF_COLUMN_NAME,XF_LABEL,XF_COMPUTE from tkr_xfiles_box where SUBSTR(xf_column_name,0,29)=?" );
			out = new PrintWriter(new File(filePath), "windows-1250");//kodowanie odpowienie dla Excela pod windowsem
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsCount = rsmd.getColumnCount();
			int[] columnTypes = new int[columnsCount];
			String[] columnNames = new String[columnsCount];
			String[] columnNamesLabel=new String[columnsCount];
					
                        
            for (int i=1; i<columnsCount+1; i++) {
                String columnName = rsmd.getColumnName(i);
                columnNames[i-1] = rsmd.getColumnName(i);
                columnTypes[i-1] = rsmd.getColumnType(i);
                
                               
                pst.setString(1, columnName);                
                rs2=pst.executeQuery();              
                	rs2.next();
                	columnNamesLabel[i-1] = rs2.getString("XF_LABEL");
            }
			
			
			//jesli skonfigurowano heaer to zostanie on umieszczony w pierwszej linii
			if(header != null && header.length() > 0){
				header = CsvUtils.escapeCsv(header);
				out.print(header);
				out.print(CsvUtils.NEW_LINE);
			}
			
			String csvRow = CsvUtils.stringArrayToCVS(columnNamesLabel);
			out.print(csvRow);

			while (rs != null && rs.next() ) {
				
				String[] resultRow = new String[columnsCount]; 
				for (int i = 0; i < columnTypes.length; i++) {
					//TODO prepare for processing all types
					int type = columnTypes[i];
					String value = "";
					switch (type) {
//					dla wartosci numerycznych chcemy pusta jesli w bazie jest null
					case Types.TINYINT:
					case Types.SMALLINT:
					case Types.INTEGER:	
					case Types.BIGINT:	
					case Types.FLOAT:	
					case Types.REAL:	
					case Types.DOUBLE:	
					case Types.NUMERIC:	
					case Types.DECIMAL:	
						BigDecimal numericValue = rs.getBigDecimal(i + 1);
						if(rs.wasNull()){
							value = "";
						}else{
							value = numericValue.toString();
						}
						break;

					default:
						value = rs.getString(i + 1);
						if(value==null){
							value="";
						}						
						break;
					}
					resultRow[i] = value;
				}
				csvRow = CsvUtils.stringArrayToCVS(resultRow);
				out.print(CsvUtils.NEW_LINE);//nowa linia przed dodaniem nowego wiersza, aby na koncu pliku nie bylo nowej linii
				out.print(csvRow);
			}

			out.flush();
			out.close();
			
			FileNetCommunication fileNetCommunication = new FileNetCommunication();
			String documentId = fileNetCommunication.createDocument(filePath);
			
			PreparedStatement pstmt = conn.prepareStatement("update RPT_ORDERED SET ORD_OST_ID = 2, ORD_RESULT_PATH = ? WHERE ORD_ID = ?" );
			pstmt.setString(1, documentId);
			pstmt.setInt(2, orderId.intValue());
			pstmt.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			try {
			st.executeUpdate("update RPT_ORDERED SET ORD_OST_ID = 3 WHERE ORD_ID = "
					+ orderId);
			}
			catch (SQLException ex1) {
				System.out.println(ex1.toString());
			}
			throw new Exception("Blad generowania raportu:" + ex.toString(), ex
					.getCause());
		} finally {
			rs2.close();
			pst.close();
			closeConnection();
		}
		System.out.println("----------------Zakonczono " + new Date());
	}
}
