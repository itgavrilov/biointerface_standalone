package ru.gsa.biointerface.domain;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    Device getDevice();

    List<Graph> getGraphs();

    boolean isConnected();

    void disconnect();

    void controllerTransmissionStart() throws DomainException;

    void controllerTransmissionStop() throws DomainException;

    boolean isControllerTransmission();

    void controllerReboot();

    void recordingStart(String comment);

    void recordingStop();

    boolean isRecording();

    void changeCommentOnExamination(String comment);
}