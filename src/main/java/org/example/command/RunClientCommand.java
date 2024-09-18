package org.example.command;

import org.example.TcpClient;
import org.example.protocol.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RunClientCommand implements Runnable {
    private final Logger logger;
    private final String host;
    private final int port;

    public RunClientCommand(String host, int port) {
        this.logger = LoggerFactory.getLogger(RunServerCommand.class);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        var types = Arrays.stream(SensorType.values())
                .filter((type) -> type != SensorType.UNKNOWN)
                .collect(Collectors.toSet());

        try (var pool = Executors.newFixedThreadPool(types.size())) {
            for (var t : types) {
                pool.execute(() -> {
                    try (TcpClient client = new TcpClient(this.host, this.port, t)) {
                        client.run();
                    } catch (IOException e) {
                        this.logger.error(String.format(
                                "Client could not connect to TCP server at %s:%d.",
                                this.host,
                                this.port
                        ));
                    }
                });
            }

            try {
                var ignored = pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                this.logger.error("Thread was interrupted while waiting for termination.", e);
            }
        }
    }
}
