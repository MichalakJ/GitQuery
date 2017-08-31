package query.statements;

public enum Object {
    COMMIT("commit"),
    TREE("tree"),
    BLOB("blob"),
    OBJECT("object"),
    DATE("date"),
    AUTHOR("author"),
    SHA1("sha1"),
    MESSAGE("message"),
    EMAIL("email"),
    PARENT("parent"),
    TYPE("type");

    private String value;

    Object(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Object getObject(String value){
        for (Object object : Object.values()) {
            if(object.getValue().equals(value)){
                return object;
            }
        }
        return null;
    }
}
