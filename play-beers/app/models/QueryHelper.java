package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import play.*;


public class QueryHelper {
	
	public List<QueryObject> getCheckedQueries(Map<String, String> queries) {
		List<QueryObject> results = new ArrayList<QueryObject>();
		for (String fields : queries.keySet()) {
			String[] split = fields.split(" ");
			Logger.debug("split key");
			Logger.debug(split[0]);
			Logger.debug("help "+(split[0].equals("field")));
			if (split[0].equals("field")) {
				results.add(new QueryObject(split[1], queries.get("operator "+split[1]),
														queries.get("value "+split[1])));
			}
		}
		Logger.debug("size "+ results.size());
		return results;
	}
}
