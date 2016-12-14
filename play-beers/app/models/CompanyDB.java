package models;

import play.db.*;

import java.lang.Exception;
import java.lang.String;
import java.sql.*;
import javax.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
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
        public Map<String, Double> companyData;


        public CompanyInfo(String key) {
            companyData = new HashMap<String, Double>();
            try {
                setupCompanyInfo(key);

            }
            catch (Exception e){
            }
        }

        public CompanyInfo(String key, String test) {
            companyData = new HashMap<String, Double>();
            name = key;
            ticker = "AAPL";
            companyData.put("test field", 392.0);
            companyData.put("word", 37.0);
        }


        private void setupCompanyInfo(String key) throws SQLException{
            Connection connection = null;
            try {
                connection = DB.getConnection();
                // retrieve basic info:
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM current WHERE ticker = ?");
                statement.setString(1, key);
                ResultSet rs = statement.executeQuery();
                if (! rs.next()) {
                    return;
                }
                int size = rs.getMetaData().getColumnCount();
                for (int i = 0; i < size; i++) {
                	String name = rs.getMetaData().getColumnName(i);
                	companyData.put(name, rs.getDouble(name));
                }
//                ticker = rs.getString("ticker");
//                companyData.put("Price Per Earnings", rs.getDouble("pricePerEarnings"));
//                companyData.put("Year High", rs.getDouble("yearHigh"));
//                name = rs.getString("name");
//                companyData.put("Price Sales", rs.getDouble("priceSales"));
//                companyData.put("Price", rs.getDouble("price"));
//                companyData.put("Exhange", rs.getDouble("exchange"));
//                companyData.put("Dividend Yield", rs.getDouble("dividendYield"));
//                companyData.put("Earnings Per Share",rs.getDouble("earningsPerShare"));
//                companyData.put("Volume", rs.getDouble("volume"));
//                companyData.put("Daily High", rs.getDouble("dailyHigh"));
//                companyData.put("50 Day Moving Average", rs.getDouble("fiftyDayMovingAverage"));
//                companyData.put("200 Day Moving Average", rs.getDouble("200DayMovingAverage"));
//                companyData.put("Divedend Per Share", rs.getDouble("divedendPerShare"));
//                companyData.put("Market Cap", rs.getDouble("marketCap"));
//                companyData.put("Open", rs.getDouble("open"));
//                companyData.put("Average Daily Volume", rs.getDouble("avgDailyVolume"));
                rs.close();
                statement.close();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public static CompanyInfo getCompanyInfo(String key) {
        return new CompanyInfo(key);
    }

    public static CompanyInfo getTestDrinker(String key) {
        return new CompanyInfo(key, "help");
    }
    
    public static List<String> getColumnNames() {
    	List<String> columns = new ArrayList<String>();
    	//Uncomment Later
//        Connection connection = null;
//        try {
//            connection = DB.getConnection();
//            PreparedStatement statement = connection
//                    .prepareStatement("SELECT * FROM current WHERE name = Amazon");
//            ResultSet rs = statement.executeQuery();
//            if (! rs.next()) {
//                return columns;
//            }
//            int size = rs.getMetaData().getColumnCount();
//            for (int i = 0; i < size; i++) {
//            	String name = rs.getMetaData().getColumnName(i);
//            	columns.add(name);
//            }
//            statement.close();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (Exception e) {
//                }
//            }
//        }
    	columns.add("test one");
    	columns.add("Jun");
    	columns.add("fun");
        return columns;
    }
    
}