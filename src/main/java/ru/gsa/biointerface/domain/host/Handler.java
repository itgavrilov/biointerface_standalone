package ru.gsa.biointerface.domain.host;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.host.packets.ChannelPacket;
import ru.gsa.biointerface.domain.host.packets.ConfigPacket;
import ru.gsa.biointerface.domain.host.packets.Packet;
import ru.gsa.biointerface.domain.host.serverByPuchkov.ChannelHandler;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public class Handler implements ChannelHandler<Packet, Packet, SerialPort> {
    private final DataCollector dataCollector;

    public Handler(DataCollector dataCollector) {
        if (dataCollector == null)
            throw new NullPointerException("Devise is null");

        this.dataCollector = dataCollector;
    }

    @Override
    public void channelRead(Packet message, LinkedBlockingQueue<Packet> sendBuffer, SerialPort context) {
        switch (message.getPackageType()) {
            case CONFIG -> {
                ConfigPacket msg = (ConfigPacket) message;
                dataCollector.setDevice(new Device(msg.getSerialNumber(), msg.getCountOfChannels(), null));
            }
            case CONTROL -> {

            }
            case DATA -> {
                if (dataCollector.getGraphs() != null && dataCollector.getGraphs().size() > 0) {
                    ChannelPacket msg = (ChannelPacket) message;
                    for (char i = 0; i < msg.getCountChannelInPacket(); i++) {
                        int scale = msg.getScale(i);
                        int simple = msg.getSample(i);

                        if (dataCollector.getGraphs().get(i) != null) {
                            dataCollector.getGraphs().get(i).add(simple);
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected packageType value: " + message.getPackageType());
        }
    }
}