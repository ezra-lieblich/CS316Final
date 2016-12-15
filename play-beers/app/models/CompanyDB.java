package models;

import play.db.*;
import play.Logger;

import java.lang.Exception;
import java.lang.String;
import java.sql.*;
import javax.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDB {
    public static class CompanyInfo {
        public String ticker;
        public double pricePerEarnings;
        public double yearHigh;
        public String name;
        public double priceSales;
        public double price;
        public String exchange;
        //Not filled out a lot of times
        public double dividendYield;
        public double earningsPerShare;
        public double volume;
        public double dailyHigh;
        public double fiftyDayMovingAverage;
        public double twohundredDayMovingAverage;
        public double divedendPerShare;
        public double marketCap;
        public double open;
        public double avgDailyVolume;
        public Map<String, Float> companyData;


        public CompanyInfo(String key) {
        	ticker = key;
        	name = "";
            companyData = new HashMap<String, Float>();
            try {
                setupCompanyInfo(key);

            }
            catch (Exception e){
            }
        }
        
    
        private void setupCompanyInfo(String key) throws SQLException{
            Connection connection = null;
            String[] stringCols = new String[]{"name","ticker","exchange"};
            try {
                connection = DB.getConnection();
                // retrieve basic info:
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM current Where ticker = ?");
                statement.setString(1, key);
                ResultSet rs = statement.executeQuery();
                if (! rs.next()) {
                	Logger.debug("fuck");
                    return;
                }
                name = rs.getString("name");
                int size = rs.getMetaData().getColumnCount();
                Logger.debug("size={}", size);
                for (int i = 0+1; i < size+1; i++) {
                	String name = rs.getMetaData().getColumnName(i);
                	if (!Arrays.asList(stringCols).contains(name)){
                		Logger.debug("column={}", name);
                		companyData.put(name, rs.getFloat(name));
                	}
                }
               rs.close();
               statement.close();
            }
            catch(Exception e){
            	Logger.debug("Couldnt connect");
            }
        }
    }

    public static CompanyInfo getCompanyInfo(String key) {
        return new CompanyInfo(key);
    }
    
    public static List<String> getColumnNames() {
    	List<String> columns = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = DB.getConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM current Where ticker = ?");
            statement.setString(1, "AMZN");
            ResultSet rs = statement.executeQuery();

            if (! rs.next()) {
                return columns;
            }
            int size = rs.getMetaData().getColumnCount();
            for (int i = 0+1; i < size+1; i++) {
            	String name = rs.getMetaData().getColumnName(i);
            	columns.add(name);
            }

            statement.close();
        } catch (Exception e) {
        	Logger.debug("COULDNT GET COLUMN NAMES");
        }
        return columns;
    }
    
    //list of names or Object with other information
    public static List<String> queryResults(List<QueryObject> queries) {
    	List<String> names = new ArrayList<String>();
      Connection connection = null;
      try {
          connection = DB.getConnection();
          String preparedText = getPreparedStatement(queries);
          Logger.debug(preparedText);
          PreparedStatement statement = connection
                  .prepareStatement(preparedText);
          ResultSet rs = statement.executeQuery();
          while(rs.next()) {
        	  names.add(rs.getString(1));
          }
          statement.close();
      } catch (Exception e) {
    	  Logger.debug(e.toString());
      }
      return names;
    }
    
    private static String getPreparedStatement(List<QueryObject> queries) {
    	String conditions = "Select ticker FROM current Where ";
    	boolean first = true;
    	for (QueryObject query : queries) {
    		if (validateQuery(query)) {
    			if (first) {
    				conditions += query.column + query.operator + query.value;
    				first = false;
    			}
    			else{
    				conditions += " And " + query.column + query.operator + query.value;
    			}
    		}
    	}
    	return conditions;
    }

	private static boolean validateQuery(QueryObject query) {
		
		return validOperator(query.operator) && validValue(query.value);
	}

	private static boolean validValue(String value) {
		try{
			Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private static boolean validOperator(String operator) {
		String[] operators = new String[]{"=", "!=", ">", "<", ">=", "<="};
		return Arrays.asList(operators).contains(operator);
	}

	public static List<QuarterlyReportObject> getCompanyQuarterlyReports(String key) throws SQLException{
		List<QuarterlyReportObject> quarterlyReportList = new ArrayList<QuarterlyReportObject>();
    	Connection connection = null;
    	try {
            connection = DB.getConnection();                
           
            PreparedStatement statement = connection
                    .prepareStatement("SELECT name, date, path "
                    		+ "FROM current "
                    		+ "FULL OUTER JOIN quarterly "
                    		+ "ON current.name = quarterly.conm "
                    		+ "WHERE current.name = ? "
                    		+ "AND quarterly.type = ?");
            statement.setString(1, key);
            statement.setString(2, "10-Q");
            ResultSet rs = statement.executeQuery();
            if (! rs.next()) {             	
                return quarterlyReportList;
            }            
            rs.last();
            int numRows = rs.getRow();
            rs.beforeFirst();
            for (int i = 1; i < numRows + 1; i++){
            	rs.absolute(i);
            	QuarterlyReportObject quarterlyReport = new QuarterlyReportObject(
            			rs.getString("name"), 
            			rs.getString("date"),
            			rs.getString("path"));
            	quarterlyReportList.add(quarterlyReport);
            }
            rs.close();
            statement.close();
            return quarterlyReportList;            
    	}
        catch(Exception e){
        	Logger.debug("Couldnt connect");
        }
    	return null;
	}

	

    
}