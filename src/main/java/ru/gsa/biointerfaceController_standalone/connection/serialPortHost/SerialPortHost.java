package ru.gsa.biointerfaceController_standalone.connection.serialPortHost;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import ru.gsa.biointerfaceController_standalone.connection.serialPortHost.packets.*;
import ru.gsa.biointerfaceController_standalone.connection.serialPortHost.serverByPuchkov.AbstractServer;

import java.util.Arrays;

/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public class SerialPortHost extends AbstractServer<Packet, Packet, SerialPort> implements SerialPortDataListener {
    private final SerialPort serialPort;

    public SerialPortHost(SerialPort serialPort) {
        super();
        this.serialPort = serialPort;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    @Override
    protected void doStart() {
        try {
            super.doStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setNumDataBits(8);
        serialPort.setBaudRate(512000);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        serialPort.openPort();
        serialPort.addDataListener(this);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (serialPort.isOpen())
            serialPort.closePort();
    }

    @Override
    protected SerialPort getInterface() {
        return serialPort;
    }

    @Override
    protected void send(Packet packet) {
        if (packet == null)
            throw new NullPointerException("Packet is null");
        if (!serialPort.isOpen())
            throw new RuntimeException("Serial port is not open");

        serialPort.writeBytes(packet.getBytes(), packet.getBytes().length);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event == null)
            throw new NullPointerException("Event is null");
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        while (serialPort.isOpen() && serialPort.bytesAvailable() > 0) {
            byte[] bytesFromSerialPort = new byte[serialPort.bytesAvailable()];
            int indexByteArray = 0;

            serialPort.readBytes(bytesFromSerialPort, serialPort.bytesAvailable());

            while (indexByteArray < bytesFromSerialPort.length - 4) {
                if (bytesFromSerialPort[indexByteArray] == -1 && bytesFromSerialPort[indexByteArray + 1] == -1) {
                    PacketType packetType = PacketType.findById(bytesFromSerialPort[indexByteArray + 2]);
                    int msgSize = bytesFromSerialPort[indexByteArray + 3];
                    int startOfPackage = indexByteArray + 4;
                    indexByteArray = startOfPackage + msgSize;

                    if (indexByteArray <= bytesFromSerialPort.length) {
                        byte[] msg = Arrays.copyOfRange(bytesFromSerialPort, startOfPackage, indexByteArray);
                        Packet packet;

                        switch (packetType) {
                            case CONFIG -> packet = new ConfigPacket(msg);
                            case CONTROL -> packet = new ControlPacket(msg);
                            case DATA -> packet = new ChannelPacket(msg);
                            default -> throw new IllegalStateException("Unexpected value: " + packetType);
                        }

                        try {
                            readBuffer.put(packet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    indexByteArray++;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialPortHost serialPortHost = (SerialPortHost) o;
        return serialPort.equals(serialPortHost.serialPort);
    }

    @Override
    public int hashCode() {
        return serialPort.hashCode();
    }
}


