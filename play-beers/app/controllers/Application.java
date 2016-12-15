package controllers;

import java.sql.SQLException;
import java.util.Map;
import javax.swing.text.html.HTML;


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.Form.*;
import play.libs.*;
import play.twirl.api.Html;
import views.html.*;

import models.BeerDB;
import models.CompanyDB;
import models.CurrentDB;
import models.QueryHelper;
import models.QueryObject;
import models.QuarterlyReportObject;

public class Application extends Controller {

    public static Result index() throws SQLException {

    	List<String> names = new ArrayList<String>(
    			Arrays.asList("Sean Hudson",
    							"Andrew Bihl",
    							"Will Rollins",
    							"Ezra Lieblich",
    							"Diane Hadley"));
        return ok(index.render(names));

    }
    
    public static Result viewCompare() {
//        String[] results = new String[]{"Amazon", "Google", "Microsoft"};
        CurrentDB companyInfo = new CurrentDB("close","AMZN", "GOOGL");
        List<List<Object>> financialData = companyInfo.getGraphData();
        //String jsonData = Json.toJson(financialData);
        //Html chartData = new Html.apply(Json.toJson(financialData));
        return ok(compare.render(companyInfo, Html.apply(Json.toJson(financialData).toString())));
    }
    

//    public static Result viewDrinker(String name) throws SQLException {
//        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
//        if (drinkerInfo == null) {
//            return ok(error.render("No drinker named \"" + name + "\""));
//        } else{
//            return ok(drinker.render(drinkerInfo));
//        }
//    }

    public static Result viewCompany(String key) throws SQLException {
    	CompanyDB.CompanyInfo companyInfo = CompanyDB.getCompanyInfo(key);
    	if (companyInfo.name == "")
    		return ok(error.render("Not a valid company named. Enter companies ticker symbol"));
    	List<QuarterlyReportObject> quarterlyReports = CompanyDB.getCompanyQuarterlyReports(key);
    	
    	
        return ok(company.render(companyInfo, quarterlyReports));
    }    

   

//    public static Result editDrinker(String name) throws SQLException {
//        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
//        if (drinkerInfo == null) {
//            return ok(error.render("No drinker named \"" + name + "\""));
//        } else{
//            return ok(edit.render(drinkerInfo,
//                                  BeerDB.getAllBeerNames(),
//                                  BeerDB.getAllBarNames()));
//        }
//    }
//    
    public static Result interpretQuery() throws SQLException{
    	QueryHelper queryHelper = new QueryHelper();
        Map<String, String> data = Form.form().bindFromRequest().data();
        List<QueryObject> parameters = queryHelper.getCheckedQueries(data);
//        for (QueryObject param : parameters) {
//        	Logger.debug("result");
//        	Logger.debug(param.column);
//        	Logger.debug(param.operator);
//        	Logger.debug(param.value);
//        }
        List<String> answer = CompanyDB.queryResults(parameters);    	
    	
    	return ok(queryresult.render(answer));
    }
    
    public static Result viewSearch() throws SQLException {
        List<String> testList = CompanyDB.getColumnNames();
    	return ok(search.render(testList));
    }


//    public static Result updateDrinker() throws SQLException {
//        Map<String, String> data = Form.form().bindFromRequest().data();
//        String name = data.get("name");
//        String address = data.get("address");
//        if (name == null || address == null) {
//            return ok(error.render("Bad request"));
//        }
//        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
//        if (drinkerInfo == null) {
//            return ok(error.render("No drinker named \"" + name + "\""));
//        }
//        ArrayList<String> beersLiked = new ArrayList<String>();
//        ArrayList<String> barsFrequented = new ArrayList<String>();
//        ArrayList<Integer> timesFrequented = new ArrayList<Integer>();
//        for (Map.Entry<String, String> entry: data.entrySet()) {
//            if (entry.getKey().startsWith("BeersLiked/")) {
//                beersLiked.add(entry.getKey()
//                               .substring("BeersLiked/".length()));
//            } else if (entry.getKey().startsWith("BarsFrequented/")) {
//                int times = Integer.parseInt(entry.getValue());
//                if (times > 0) {
//                    barsFrequented.add(entry.getKey()
//                                       .substring("BarsFrequented/".length()));
//                    timesFrequented.add(Integer.parseInt(entry.getValue()));
//                }
//            }
//        }
//        boolean success = BeerDB.updateDrinkerInfo
//                (new BeerDB.DrinkerInfo(name, address,
//                        beersLiked, barsFrequented, timesFrequented));
//        if (success) {
//            return redirect(controllers.routes.Application
//                            .viewCompany(drinkerInfo.name));
//        } else {
//            return ok(error.render("No drinker named \"" + name + "\""));
//        }
//    }


    public static Result searchCompanies() throws SQLException {
        Map<String, String> data = Form.form().bindFromRequest().data();
        String value = data.get("company");
        return redirect(controllers.routes.Application.viewCompany(value));
    }
    
    public static Result searchTab() throws SQLException {
    	return ok(searchbartab.render("Enter a Company"));
    }
    
    public static Result setupCompare() throws SQLException {
        Map<String, String> data = Form.form().bindFromRequest().data();
    	Logger.debug("size " + data.size());

    	List<String> companies = new ArrayList<String>(data.keySet());
    	//return ok("price", compare.render(companies));
    	return ok(error.render("ARR"));
    }

//    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
//<script type='text/javascript' 
//src='@routes.Assets.at("javascripts/js/bootstrap.js")'></script>
//</body>

}
