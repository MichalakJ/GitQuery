package query.statements;

public enum Condition {
    IS("is"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUAL("="),
    STARTS_WITH("startsWith"),
    CONTAINS("contains");

    private String value;

    Condition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Condition getCondition(String value){
        for (Condition filter : Condition.values()) {
            if(filter.getValue().equals(value)){
                return filter;
            }
        }
        return null;
    }
}
