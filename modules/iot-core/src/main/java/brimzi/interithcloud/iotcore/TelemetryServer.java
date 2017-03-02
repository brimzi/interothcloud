package brimzi.interithcloud.iotcore;

import brimzi.interithcloud.iotcore.backend.DataStore;
import brimzi.interithcloud.iotcore.backend.QueryResult;
import brimzi.interothcloud.iotcoreapi.*;
import com.google.common.base.Strings;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelemetryServer {
    private static Logger LOGGER = LoggerFactory.getLogger(TelemetryServer.class);
    private final int port;
    private final Server server;

    public TelemetryServer(int port) {
        this(port, new DataStore() {
            @Override
            public void save(TelemetryWrite request) throws Exception {

            }

            @Override
            public QueryResult query(String id, String func, long start, long end) throws Exception {
                return null;
            }

            @Override
            public List<QueryResult> queryForAll(List<String> idList, String func, long start, long
                    end) {
                return new ArrayList<>();
            }
        });
    }

    public TelemetryServer(int port, DataStore dataStore) {
        this(ServerBuilder.forPort(port), port, dataStore);
    }

    public TelemetryServer(ServerBuilder<?> serverBuilder, int port, DataStore dataStore) {
        this.port = port;
        this.server = serverBuilder.addService(new TelemetryService(dataStore)).build();
    }

    public void start() throws Exception {
        server.start();
        LOGGER.info("Server started, listening on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                TelemetryServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        TelemetryServer app = new TelemetryServer(8089);
        app.start();
        app.blockUntilShutdown();
    }

    private static class TelemetryService extends TelemetryApiGrpc.TelemetryApiImplBase {

        final private DataStore backendStore;

        TelemetryService(DataStore backendStore) {
            this.backendStore = backendStore;
        }

        @Override
        public void save(TelemetryWrite request, StreamObserver<Ok> responseObserver) {
            final String id = request.getId();
            if (Strings.isNullOrEmpty(id)) {
                responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT));
            }

            try {
                backendStore.save(request);
                responseObserver.onNext(Ok.newBuilder().build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(new StatusRuntimeException(Status.INTERNAL));
            }
        }

        @Override
        public void query(TelemetryQuery req, StreamObserver<TelemetryAggregateResult> responseObserver) {

            try {
                List<QueryResult> res = backendStore.queryForAll(req.getIdList(), req.getFunc(),
                        req.getStart(), req.getEnd());

                TelemetryAggregateResult result = convertToTelemetryAggregate(res);
                responseObserver.onNext(result);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(new StatusRuntimeException(Status.INTERNAL));
            }
        }

        private TelemetryAggregateResult convertToTelemetryAggregate(List<QueryResult> res) {

            return TelemetryAggregateResult.newBuilder().addAllAggregates(res.stream().map(
                    r -> Aggregate.newBuilder().setId(r.getId())
                            .setStart(r.getStart())
                            .setEnd(r.getEnd())
                            .setValue(r.getValue())
                            .build())
                    .collect(Collectors.toList()))
                    .build();
        }
    }
}
