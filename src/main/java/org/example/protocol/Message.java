package org.example.protocol;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Message {
    private static final int SIZE = 8;
    private final SensorType type;
    private final float measurement;

    public Message(SensorType type, float measurement) {
        this.type = type;
        this.measurement = measurement;
    }

    public void send(Socket socket) throws IOException {
        var buffer = ByteBuffer.allocate(Message.SIZE);
        buffer.putInt(this.type.toInt());
        buffer.putFloat(this.measurement);
        socket.getOutputStream().write(buffer.array(), 0, Message.SIZE);
    }

    public static Message receive(Socket socket) throws IOException {
        var buffer = ByteBuffer.allocate(Message.SIZE);
        int read = socket.getInputStream().read(buffer.array());

        if (read == -1)
            throw new IOException("End of stream.");

        return new Message(
            SensorType.from(buffer.getInt()),
            buffer.getFloat()
        );
    }

    public SensorType getType() {
        return this.type;
    }

    public float getMeasurement() {
        return this.measurement;
    }
}
