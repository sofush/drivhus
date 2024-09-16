package org.example;

import org.example.protocol.Message;
import org.example.protocol.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class TcpClient implements Runnable, Closeable {
    private final Logger logger;
    private final Socket socket;
    private final SensorType type;

    public TcpClient(String host, int port, SensorType type) throws IOException {
        this.type = type;
        this.logger = LoggerFactory.getLogger(TcpClient.class);
        this.logger.info(String.format(
            "Client is attempting to connect to server on %s:%d.",
            host,
            port
        ));
        this.socket = new Socket(host, port);
        this.logger.info("Client has connected to the server.");
    }

    private void sleep() {
        try {
            long ms = (long)(Math.random() * 250.0);
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private float simulateMeasurement() {
        Random rng = new Random();

        switch (this.type) {
            case TEMPERATURE -> { return rng.nextFloat(5, 35); }
            case AIR_HUMIDITY -> { return rng.nextFloat(25, 75); }
            default -> { return rng.nextFloat(10, 65); }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            this.sleep();
            float measurement = this.simulateMeasurement();
            var message = new Message(this.type, measurement);

            try {
                message.send(this.socket);
            } catch (IOException e) {
                this.logger.error("Could not send message to server.", e);
                break;
            }
        }

        try {
            this.close();
        } catch (IOException e) {
            this.logger.error("Could not close TCP client.", e);
        }
    }

    @Override
    public void close() throws IOException {
        this.logger.info("Closing TCP client.");
        this.socket.close();
    }
}
