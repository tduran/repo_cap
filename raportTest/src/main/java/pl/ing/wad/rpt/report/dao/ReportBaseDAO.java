/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.ing.wad.rpt.report.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import pl.ing.wad.rpt.report.ReportConfigs;

public class ReportBaseDAO {

    protected Connection connection = null;
    private ReportConfigs reportConfigs;
    

    public ReportBaseDAO() {
    	reportConfigs = ReportConfigs.getInstance();
    }

    protected Connection getConnection() throws Exception {
        openConnection();
        return connection;
    }

    protected void openConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            try {
            	Class.forName("org.hsqldb.jdbcDriver");
                connection = DriverManager.getConnection(reportConfigs.getConnectionString());
            } catch (Exception ex) {
                System.out.println(ex.toString());
                throw new Exception("Blad otwarcia polaczenia:" + ex.toString());
            }
        }
        return;
    }

    protected void closeConnection() throws Exception {
        		connection.close();
    }
}