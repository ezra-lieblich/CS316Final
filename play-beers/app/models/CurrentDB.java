package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import play.db.*;


public class CurrentDB {
    public Map<String, Map<String, Double>> companyDataMap;
    public Set<String> fields;

    public CurrentDB (String ... companies) {
        // List<String> companyList = Stream.of(companies).collect(Collectors.toList()); //toMap(e
        // -> (String) e.getKey(), e -> (Map<String, Map<String, Double>>)new HashMap<String,
        // Double>())); //.collect(Collectors.toList());
        this.companyDataMap = new LinkedHashMap<String, Map<String, Double>>();
        this.fields = new HashSet<String>();
        // companyList.forEach(a -> this.companies.put(a, new LinkedHashMap<String, Double>()) );
        for (String company : companies) {
            this.companyDataMap.put(company, new LinkedHashMap<String, Double>());
        }

        // setUpTable(companies);

        this.companyDataMap.get("Amazon").put("stockPrice", 300.0);
        this.companyDataMap.get("Google").put("stockPrice", 200.0);
        this.companyDataMap.get("Microsoft").put("stockPrice", 100.0);
        this.companyDataMap.get("Amazon").put("MCAP", 330.0);
        this.companyDataMap.get("Google").put("MCAP", 220.0);
        this.companyDataMap.get("Microsoft").put("MCAP", 110.0);
        fields.add("CompanyPrice");
        fields.add("StockPrice");
        fields.add("MCAP");
//        Map<String, Double> agg = new LinkedHashMap<String, Double>();
//        agg.put("stockPrice", 0.0);
//        agg.put("MCAP", 0.0);
//        this.companyDataMap.put("Aggregate", agg);

//        for (Entry<String, Map<String, Double>> entry : companyDataMap.entrySet()) {
//            if (!entry.getKey().equals("Aggregate")) {
//                for (Entry<String, Double> values : entry.getValue().entrySet()) {
//                    Double currentAverage =
//                            this.companyDataMap.get("Aggregate").get(values.getValue()) == null ? 0
//                                                                                                : this.companyDataMap
//                                                                                                        .get("Aggregate")
//                                                                                                        .get(values
//                                                                                                                .getValue());
//                    this.companyDataMap.get("Aggregate")
//                            .put(values.getKey(), runningAverage(currentAverage, this.companyDataMap
//                                    .get(values.getKey()).get(values.getValue())));
//                }
//            }
//        }
    }

    double runningAverage (double average, double new_sample) {

        average -= average / (companyDataMap.size() - 1);
        average += new_sample / (companyDataMap.size() - 1);

        return average;
    }

    // public void setUpTable(String[] companies) {
    // Connection connection = null;
    // try {
    // connection = DB.getConnection();
    // PreparedStatement statement = connection
    // .prepareStatement("SELECT * FROM ? WHERE company = ?");
    // for(String company : companies) {
    // String companyOverTimeData = company + "OverTimeData";
    // statement.setString(1, company);
    // ResultSet rs = statement.executeQuery();
    // while (!rs.next()) {
    // for (int i = 1; i< rs.getMetaData().getColumnCount(); i++) {
    // String columnName = rs.getMetaData().getColumnName(i);
    // Double value = rs.getDouble(i);
    // this.companyDataMap.get(company).put(columnName, value);
    // this.companyDataMap.get("Aggregate").put(columnName,
    // this.companyDataMap.get("Aggregate").get(columnName)/this.companyDataMap.size());
    // }
    // }
    // }
    // }
    // catch (Exception e) {
    // }
    // }

    public Map<String, Map<String, Double>> getCompanyInfo () {
        return companyDataMap;
    }

    public Collection<String> getFields() {
        return fields;
    }
}
