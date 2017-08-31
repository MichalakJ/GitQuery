package query.statements;

public enum Expression {
    SELECT("SELECT"),
    WHERE("WHERE");

    private String value;

    Expression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
