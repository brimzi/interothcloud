package brimzi.interithcloud.iotcore.backend;


public class QueryResult {
    private String id;
    private long start;
    private long end;
    private double value;

    public String getId() {
        return id;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public double getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
