package constants;

public enum ObjectType {
    EXT(0),
    COMMIT(1),
    TREE(2),
    BLOB(3),
    TAG(4),
    TYPE_5(5),
    OFS_DELTA(6),
    REF_DELTA(7);

    private int value;
    ObjectType(int value) {
        this.value = value;
    }

    public static ObjectType getName(int value){
        for (ObjectType objectType : ObjectType.values()) {
            if(objectType.value == value){
                return objectType;
            }
        }
        return null;
    }
}
