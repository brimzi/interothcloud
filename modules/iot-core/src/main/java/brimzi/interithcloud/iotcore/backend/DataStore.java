package brimzi.interithcloud.iotcore.backend;

import brimzi.interothcloud.iotcoreapi.TelemetryWrite;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public interface DataStore {

    void save(TelemetryWrite request) throws Exception;

    QueryResult query(String id, String func, long start, long end)throws Exception;

    List<QueryResult> queryForAll(List<String> idList, String func, long start, long end)throws
            Exception;
}
