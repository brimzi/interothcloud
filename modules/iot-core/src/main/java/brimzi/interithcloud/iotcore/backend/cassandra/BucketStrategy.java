package brimzi.interithcloud.iotcore.backend.cassandra;

import java.util.List;

interface BucketStrategy {

    String getBucket(Long time);
    List<String> getBuckets(Long begin,Long end);
}
