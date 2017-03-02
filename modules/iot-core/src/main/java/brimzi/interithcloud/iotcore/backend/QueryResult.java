package brimzi.interithcloud.iotcore.backend;

/**
 * Created by brimzy on 02/03/2017.
 */
public class QueryResult {
    private String id;
    private long start;
    private long end;
    private long value;

    public String getId() {
        return id;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getValue() {
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

    public void setValue(long value) {
        this.value = value;
    }
}
