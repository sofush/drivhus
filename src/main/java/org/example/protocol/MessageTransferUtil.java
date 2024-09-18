package org.example.protocol;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessageTransferUtil {
    private MessageTransferUtil() {}

    public static void send(Socket socket, Message message) throws IOException {
        var buffer = ByteBuffer.allocate(Message.SIZE);
        buffer.putInt(message.getType().toInt());
        buffer.putFloat(message.getMeasurement());
        socket.getOutputStream().write(buffer.array(), 0, buffer.capacity());
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
}
