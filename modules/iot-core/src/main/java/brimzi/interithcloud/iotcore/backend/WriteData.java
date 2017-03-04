package brimzi.interithcloud.iotcore.backend;

import java.util.Map;

public class WriteData {
    private String id;
    private Map<Long,Double> values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Long, Double> getValues() {
        return values;
    }

    public void setValues(Map<Long, Double> values) {
        this.values = values;
    }
}
