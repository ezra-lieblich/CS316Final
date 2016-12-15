package models;

public class QueryObject {
        public String column;
        public String operator;
        public String value;
        
        public QueryObject(String column, String operator, String value) {
                this.column = column;
                this.operator = operator;
                this.value = value;
        }
}