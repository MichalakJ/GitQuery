package model.factory;

import constants.ObjectType;
import model.Commit;
import model.GitObject;

public class ObjectFactory {
    public static GitObject createObject(ObjectType type, String sha1, int offset){
        if(type == ObjectType.COMMIT){
            return createCommit(type, sha1, offset);
        }else{
            return regularObject(type, sha1, offset);
        }
    }

    private static GitObject regularObject(ObjectType type, String sha1, int offset) {
        return new GitObject(type, sha1, offset);
    }

    private static Commit createCommit(ObjectType type, String sha1, int offset) {
        return new Commit(type, sha1, offset);
    }
}
