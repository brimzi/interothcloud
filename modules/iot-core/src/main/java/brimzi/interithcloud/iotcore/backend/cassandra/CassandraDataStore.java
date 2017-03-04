package brimzi.interithcloud.iotcore.backend.cassandra;

import brimzi.interithcloud.iotcore.backend.DataStore;
import brimzi.interithcloud.iotcore.backend.QueryResult;
import brimzi.interithcloud.iotcore.backend.WriteData;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class CassandraDataStore implements DataStore {

    private static final String TABLE_NAME = "";
    private static String INSERT_QUERY_STRING = String.format("INSERT INTO %s(id,bucket,time,value) " +
            "VALUES(?,?,?,?)", TABLE_NAME);

    final private Session session;
    final private BucketStrategy strategy;
    final private PreparedStatement insertStatement;
    final private int microBatchSize;

    public CassandraDataStore(Session session, BucketStrategy strategy,int microBatchSize) {
        this.session = session;
        this.strategy = strategy;
        this.microBatchSize =microBatchSize;
        insertStatement = session.prepare(INSERT_QUERY_STRING);
    }

    @Override
    public void save(WriteData data) throws Exception {
        final String id = data.getId();
        final Map<Long, Double> values = data.getValues();
        final List<ResultSetFuture> jobs = new ArrayList<>();

        values.forEach((time, value) -> {
            String bucket = strategy.getBucket(time);

            jobs.add(session.executeAsync(createInsertStatement(id, bucket, time, value)));

            if(jobs.size() > microBatchSize){
                for (ResultSetFuture job : jobs) {
                    job.getUninterruptibly();
                }
                jobs.clear();
            }
        });

        if(jobs.size()>0){
            for(ResultSetFuture job:jobs){
                job.getUninterruptibly();
            }
        }
    }

    @Override
    public QueryResult query(String id, String func, long start, long end) throws Exception {

        return null;
    }

    @Override
    public <T> List<T> queryForAll(List<String> idList, String func, long start, long end,
                                   Function<QueryResult, T> converter) throws Exception {
        return null;
    }

    private Statement createInsertStatement(String id, String bucket, Long time, Double value) {

        return insertStatement.bind(id, bucket, time, value);
    }
}
