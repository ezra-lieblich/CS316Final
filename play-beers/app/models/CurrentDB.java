package models;

// import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.lang.Exception;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import play.db.*;
import play.Logger;


public class CurrentDB {
    public Map<String, Map<String, Double>> companyDataMap;
    public Map<String, List<Double>> overTimeData;
    public Set<String> fields;
    public Set<String> overTimeFields;
    public String graphKey;

    public CurrentDB (String graphKey, String ... companies) {
        // List<String> companyList = Stream.of(companies).collect(Collectors.toList()); //toMap(e
        // -> (String) e.getKey(), e -> (Map<String, Map<String, Double>>)new HashMap<String,
        // Double>())); //.collect(Collectors.toList());
        this.graphKey = graphKey;
        this.companyDataMap = new LinkedHashMap<String, Map<String, Double>>();
        this.overTimeData = new LinkedHashMap<String, List<Double>>();
        this.fields = new LinkedHashSet<String>();
        this.overTimeFields = new LinkedHashSet<String>();
        fields.add("Ticker");
        // companyList.forEach(a -> this.companies.put(a, new LinkedHashMap<String, Double>()) );
        for (String company : companies) {
            this.companyDataMap.put(company, new LinkedHashMap<String, Double>());
        }

        setUpTable(companies);
        setUpOverTimeData(companies, graphKey);
    }

    public void setUpOverTimeData(String[] companies, String key) {
        Logger.debug("Start overTime");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DB.getConnection();
            statement = connection.prepareStatement("SELECT * FROM past WHERE symbol = 'AMZN'");
            resultSet = statement.executeQuery();
            int colNum = resultSet.getMetaData().getColumnCount();
            if(resultSet.next()) {
                for (int i = 1; i < colNum+1; i++) {
                    Logger.debug(resultSet.getMetaData().getColumnName(i));
                    overTimeFields.add(resultSet.getMetaData().getColumnName(i));
                }
            }
            for (String company : companies) {
                overTimeData.put(company, new ArrayList<Double>());
                String queryString = "SELECT symbol, date, " + key + " FROM past Where symbol = ? ORDER BY date DESC";
                statement = connection
                        .prepareStatement(queryString); // ORDER BY date");
       
//                statement.setString(1, key);
                statement.setString(1, company);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String name = resultSet.getString("symbol");
                    double result;
                    if(resultSet.getMetaData().getColumnType(3) == Types.INTEGER) {
                        result = (double)resultSet.getInt(3);
                    }
                    else if (resultSet.getMetaData().getColumnType(3) == Types.FLOAT) {
                        result = (double)resultSet.getFloat(3);
                    }
                    else if (resultSet.getMetaData().getColumnType(3) == Types.DOUBLE) {
                        result = resultSet.getDouble(3);
                    }
                    else {
                        //Logger.debug(Types.DOUBLE + " " + Types.INTEGER + " " + Types.NUMERIC + " " + Types.FLOAT + " " + Types.VARCHAR + " " + Types.DISTINCT + " " + Types.DECIMAL + " " + Types.NVARCHAR);
//                        Logger.debug(resultSet.getMetaData().getColumnType(3) + "");
//                        Logger.debug(resultSet.getString(3));
//                        try {
//                            Logger.debug(resultSet.getString(3));
//                            result = Double.valueOf(resultSet.getString(3));
//                        }catch(Exception e) {
//                            continue;
//                        }
                        continue;
                    }
                    //Logger.debug(name + " " + result);
                    Logger.debug(company + " " + resultSet.getDate(2) + " " + result);
                    this.overTimeData.get(company).add(result);
                    //Logger.debug(name);
                }
            }
            Logger.debug("End overtime");
        }
        catch (Exception e) {
            Logger.debug(e.getMessage());
        }
        finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                }
                catch (SQLException ignore) {
                }
            if (statement != null)
                try {
                    statement.close();
                }
                catch (SQLException ignore) {
                }
            if (connection != null)
                try {
                    connection.close();
                }
                catch (SQLException ignore) {
                }
        }
    }
    
    double runningAverage (double average, double new_sample) {

        average -= average / (companyDataMap.size() - 1);
        average += new_sample / (companyDataMap.size() - 1);

        return average;
    }

    public void setUpTable (String[] companies) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //Logger.debug("God help me");
            connection = DB.getConnection();
            for (String company : companies) {
                statement = connection
                        .prepareStatement("SELECT * FROM current Where ticker = ?");
       
                statement.setString(1, company);
                resultSet = statement.executeQuery();
                //Logger.debug("HERE");
                while (resultSet.next()) {
                    String name = resultSet.getString("ticker");
                    Logger.debug(name);
                    for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
                        double result;
                        if(resultSet.getMetaData().getColumnType(i) == Types.INTEGER) {
                            result = (double)resultSet.getInt(i);
                        }
                        else if (resultSet.getMetaData().getColumnType(i) == Types.FLOAT) {
                            result = (double)resultSet.getFloat(i);
                        }
                        else if (resultSet.getMetaData().getColumnType(i) == Types.DOUBLE) {
                            result = resultSet.getDouble(i);
                        }
                        else {
                            continue;
                        }
                        String columnName = resultSet.getMetaData().getColumnName(i);
                        fields.add(columnName);
                        //Logger.debug(columnName);
                        this.companyDataMap.get(name).put(columnName, result);
                    }
                }
            }
        }
        catch (Exception e) {
            Logger.debug(e.getMessage());
        }
        finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                }
                catch (SQLException ignore) {
                }
            if (statement != null)
                try {
                    statement.close();
                }
                catch (SQLException ignore) {
                }
            if (connection != null)
                try {
                    connection.close();
                }
                catch (SQLException ignore) {
                }
        }
    }
    // statement = connection.createStatement(); //connection.prepareStatement("SELECT * FROM
    // current Where ticker = ?");
    // //statement.setString(1, "AMZN");
    // Logger.debug("THERE");
    // resultSet = statement.executeQuery("SELECT * FROM current Where ticker = 'AMZN'");

    // while (!rs.next()) {
    // Logger.debug("blh");
    // for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
    // String columnName = rs.getMetaData().getColumnName(i);
    // Double value = rs.getDouble(i);
    // Logger.debug(columnName);
    // this.companyDataMap.get("AMZN").put(columnName, value);
    // this.companyDataMap.get("Aggregate").put(columnName,
    // this.companyDataMap
    // .get("Aggregate")
    // .get(columnName) /
    // this.companyDataMap
    // .size());
    // }
    // }
    // }
    // // }
    // catch(
    //
    // Exception e)
    // {
    // }

    public Map<String, Map<String, Double>> getCompanyInfo () {
        return companyDataMap;
    }

    public Collection<String> getFields () {
        Logger.debug(fields.toArray().toString());
        return fields;
    }

    public List<List<Object>> getGraphData () {
        List<List<Object>> graphData = new ArrayList<List<Object>>();
        Calendar calendar = Calendar.getInstance();
        int month;
        int day;
        int year;
        List<Object> header = new ArrayList<Object>();
        String dateHeader = "Day";
//        Map<String, String> dateHeader = new HashMap<String, String>();
//        dateHeader.put("type", "datetime");
//        dateHeader.put("id", "Year");
        header.add(dateHeader);
        for(String company : overTimeData.keySet()) {
            header.add(company);
        }
        int count = 0;
        for(int i = 0; count < 10; i++) {
            month = calendar.getTime().getMonth();
            day = calendar.getTime().getDate();
            year = calendar.getTime().getYear();
            Date date = new Date(year, month, day);
            if (calendar.getTime().getDay() != 0 &&
                calendar.getTime().getDay() != 6) {
                List<Object> dateList = new ArrayList<Object>();
                dateList.add(date);
                graphData.add(dateList);
                count++;
            }
            int monthEnum = Calendar.DAY_OF_MONTH;
            calendar.add(monthEnum, -1);
        }
        int index = 0;
        for(Entry<String, List<Double>> company : overTimeData.entrySet()) {
//            int rowCount = 0;
            for(Double value : company.getValue()) {
               graphData.get(index).add(value);
               index++;
//               rowCount++;
               if(index >= 10) {
                   break;
               }
//               int amount = 10 - rowCount;
//               for(int i = 0; i < amount; i++) {
//                   graphData.get(amount).add(0);
//               }
            }
            index = 0;
        }
        Collections.reverse(graphData);
        Collections.reverse(header);
        graphData.add(0, header);


        Logger.debug("did this finish");
        List<List<Object>> testData = new ArrayList<List<Object>>();

        testData.add(new ArrayList<Object>(Arrays.asList("Year", "AMZN", "GOOGL"))); // Year, Company...
        testData.add(new ArrayList<Object>(Arrays.asList(new Date(2016, 0, 1), 1000, 400)));
        testData.add(new ArrayList<Object>(Arrays.asList(new Date(2015, 0, 2), 1170, 460)));
        
        for(List<Object> values : graphData) {
            StringBuilder sb = new StringBuilder();
            for(Object val : values) {
                sb.append(val.toString() + " ");
            }
            Logger.debug(sb.toString());
        }
        
        return graphData;
    }

    public Collection<String> getOverTimeFields () {
        return overTimeFields;
    }

    public String getGraphKey () {
        return graphKey;
    }
    
}

