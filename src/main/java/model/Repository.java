package model;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    private Map<Integer, GitObject> objects = new HashMap<>();

    public Map<Integer, GitObject> getObjects() {
        return objects;
    }

    public void setObjects(Map<Integer, GitObject> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Repository representation \n");
        for (Integer index : objects.keySet()) {
            sb.append(objects.get(index).toString());
        }
        return sb.toString();
    }
}
