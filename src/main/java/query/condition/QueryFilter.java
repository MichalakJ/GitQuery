package query.condition;

import query.statements.Condition;
import query.statements.Object;

public class QueryFilter {
    private Condition filter;
    private Object object;
    private String value;
    private LogicType logicType;

    public QueryFilter(Condition filter, Object object, String value) {
        this.filter = filter;
        this.object = object;
        this.value = value;
    }

    public QueryFilter() {

    }

    public Condition getFilter() {
        return filter;
    }

    public void setFilter(Condition filter) {
        this.filter = filter;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LogicType getLogicType() {
        return logicType;
    }

    public void setLogicType(LogicType logicType) {
        this.logicType = logicType;
    }
}
