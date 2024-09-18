package org.example;

import org.example.protocol.Message;
import org.example.protocol.MessageTransferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable, Closeable {
    private final Socket client;
    private final Logger logger;

    public ClientHandler(Socket client) {
        this.client = client;
        this.logger = LoggerFactory.getLogger(ClientHandler.class);
    }

    public void handleMessage(Message message) {
        switch (message.getType()) {
            case TEMPERATURE -> {
                if (message.getMeasurement() > 30)
                    this.logger.warn(String.format(
                            "ALARM: Temperature exceeds high threshold! (%.1f°C)",
                            message.getMeasurement()
                    ));

                if (message.getMeasurement() < 10)
                    this.logger.warn(String.format(
                            "ALARM: Temperature below low threshold! (%.1f°C)",
                            message.getMeasurement()
                    ));
            }
            case AIR_HUMIDITY -> {
                if (message.getMeasurement() > 70)
                    this.logger.warn(String.format(
                            "ALARM: Humidity exceeds high threshold! (%.1f%%)",
                            message.getMeasurement()
                    ));

                if (message.getMeasurement() < 30)
                    this.logger.warn(String.format(
                            "ALARM: Humidity below low threshold! (%.1f%%)",
                            message.getMeasurement()
                    ));

            }
            case SOIL_MOISTURE -> {
                if (message.getMeasurement() < 20)
                    this.logger.warn(String.format(
                            "ALARM: Soil moisture below low threshold! (%.1f%%)",
                            message.getMeasurement()
                    ));
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && client.isConnected() && !client.isClosed()) {
            try {
                Message message = MessageTransferUtil.receive(client);
                this.handleMessage(message);
            } catch (IOException e) {
                this.logger.info("Client disconnected.");
                break;
            }
        }

        try {
            this.close();
        } catch (IOException e) {
            this.logger.error("Could not close ClientHandler.", e);
        }
    }

    @Override
    public void close() throws IOException {
        this.logger.info("Closing client handler.");
        this.client.close();
    }
}
