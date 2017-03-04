package brimzi.interithcloud.iotcore.backend;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.function.Function;

@ThreadSafe
public interface DataStore {

    void save(WriteData data) throws Exception;

    QueryResult query(String id, String func, long start, long end) throws Exception;

    <T> List<T> queryForAll(List<String> idList, String func, long start, long end,
                            Function<QueryResult, T> converter) throws Exception;
}
