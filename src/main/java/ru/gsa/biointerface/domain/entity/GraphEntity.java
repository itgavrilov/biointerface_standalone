package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "graph")
@Table(name = "graph")
@IdClass(GraphEntityId.class)
public class GraphEntity {
    @Id
    int numberOfChannel;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", referencedColumnName = "id")
    private ExaminationEntity examinationEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_name", referencedColumnName = "name")
    private ChannelEntity channelEntity;

    @OneToMany(mappedBy = "graphEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SampleEntity> sampleEntities;

    public GraphEntity() {

    }

    public GraphEntity(int numberOfChannel, ExaminationEntity examinationEntity, ChannelEntity channelEntity, List<SampleEntity> sampleEntities) {
        this.numberOfChannel = numberOfChannel;
        this.examinationEntity = examinationEntity;
        this.channelEntity = channelEntity;
        this.sampleEntities = sampleEntities;
    }

    public int getNumberOfChannel() {
        return numberOfChannel;
    }

    public void setNumberOfChannel(int numberOfChannel) {
        this.numberOfChannel = numberOfChannel;
    }

    public ExaminationEntity getExaminationEntity() {
        return examinationEntity;
    }

    public void setExaminationEntity(ExaminationEntity examinationEntity) {
        this.examinationEntity = examinationEntity;
    }

    public ChannelEntity getChannelEntity() {
        return channelEntity;
    }

    public void setChannelEntity(ChannelEntity channelEntity) {
        this.channelEntity = channelEntity;
    }

    public List<SampleEntity> getSampleEntities() {
        return sampleEntities;
    }

    public void setSampleEntities(List<SampleEntity> sampleEntities) {
        this.sampleEntities = sampleEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEntity that = (GraphEntity) o;
        return numberOfChannel == that.numberOfChannel && Objects.equals(examinationEntity, that.examinationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel, examinationEntity);
    }

    @Override
    public String toString() {
        String channelName = "-";
        String examinationId = "-";

        if (channelEntity != null)
            channelName = channelEntity.getName();

        if (examinationEntity != null)
            examinationId = String.valueOf(examinationEntity.getId());

        return "GraphEntity{" +
                "numberOfChannel=" + numberOfChannel +
                ", examinationEntity_id=" + examinationId +
                ", channelEntity=" + channelName +
                '}';
    }
}

