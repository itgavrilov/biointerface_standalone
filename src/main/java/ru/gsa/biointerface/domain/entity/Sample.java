package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "sample")
@Table(name = "sample")
@IdClass(SampleID.class)
public class Sample implements Serializable, Comparable<Sample> {
    @Id
    private long id;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id"),
            @JoinColumn(name = "channel_id", referencedColumnName = "id")
    })
    private Channel channel;

    private int value;

    public Sample() {
    }

    public Sample(long id, Channel channel, int value) {
        this.id = id;
        this.channel = channel;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample that = (Sample) o;
        return id == that.id && Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel);
    }

    @Override
    public int compareTo(Sample o) {
        int result = channel.compareTo(o.channel);

        if (result == 0) {
            if (id > o.id) {
                result = 1;
            } else if (id < o.id) {
                result = -1;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", examination_id=" + channel.getExamination().getId() +
                ", numberOfChannel=" + channel.getId() +
                ", value=" + value +
                '}';
    }
}