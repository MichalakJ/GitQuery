package model;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    private Map<Integer, Object> objects = new HashMap<>();

    public Map<Integer, Object> getObjects() {
        return objects;
    }

    public void setObjects(Map<Integer, Object> objects) {
        this.objects = objects;
    }
}
