package controllers;

import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.BeerDB;
import models.CompanyDB;
import models.CurrentDB;

public class Application extends Controller {

    public static Result index() throws SQLException {
        return ok(index.render(BeerDB.getAllDrinkerNames()));
    }
    
    public static Result viewCompare() {
//        String[] results = new String[]{"Amazon", "Google", "Microsoft"};
        CurrentDB companyInfo = new CurrentDB("Amazon", "Google", "Microsoft");
        return ok(compare.render(companyInfo));
    }
    
    public static Result viewSearch() {
        return ok(search.render());
    }

    public static Result viewDrinker(String name) throws SQLException {
        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
        if (drinkerInfo == null) {
            return ok(error.render("No drinker named \"" + name + "\""));
        } else{
            return ok(drinker.render(drinkerInfo));
        }
    }

    public static Result viewCompany(String key) throws SQLException {
    	CompanyDB.CompanyInfo companyInfo = CompanyDB.getTestDrinker(key);
        return ok(company.render(companyInfo));
    }

    public static Result editDrinker(String name) throws SQLException {
        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
        if (drinkerInfo == null) {
            return ok(error.render("No drinker named \"" + name + "\""));
        } else{
            return ok(edit.render(drinkerInfo,
                                  BeerDB.getAllBeerNames(),
                                  BeerDB.getAllBarNames()));
        }
    }

    public static Result updateDrinker() throws SQLException {
        Map<String, String> data = Form.form().bindFromRequest().data();
        String name = data.get("name");
        String address = data.get("address");
        if (name == null || address == null) {
            return ok(error.render("Bad request"));
        }
        BeerDB.DrinkerInfo drinkerInfo = BeerDB.getDrinkerInfo(name);
        if (drinkerInfo == null) {
            return ok(error.render("No drinker named \"" + name + "\""));
        }
        ArrayList<String> beersLiked = new ArrayList<String>();
        ArrayList<String> barsFrequented = new ArrayList<String>();
        ArrayList<Integer> timesFrequented = new ArrayList<Integer>();
        for (Map.Entry<String, String> entry: data.entrySet()) {
            if (entry.getKey().startsWith("BeersLiked/")) {
                beersLiked.add(entry.getKey()
                               .substring("BeersLiked/".length()));
            } else if (entry.getKey().startsWith("BarsFrequented/")) {
                int times = Integer.parseInt(entry.getValue());
                if (times > 0) {
                    barsFrequented.add(entry.getKey()
                                       .substring("BarsFrequented/".length()));
                    timesFrequented.add(Integer.parseInt(entry.getValue()));
                }
            }
        }
        boolean success = BeerDB.updateDrinkerInfo
                (new BeerDB.DrinkerInfo(name, address,
                        beersLiked, barsFrequented, timesFrequented));
        if (success) {
            return redirect(controllers.routes.Application
                            .viewDrinker(drinkerInfo.name));
        } else {
            return ok(error.render("No drinker named \"" + name + "\""));
        }
    }

    public static Result searchCompanies() throws SQLException {
        return ok(search.render());
    }


}
