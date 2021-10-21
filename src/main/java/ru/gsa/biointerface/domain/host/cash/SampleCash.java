package ru.gsa.biointerface.domain.host.cash;

import ru.gsa.biointerface.domain.DomainException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class SampleCash implements Cash {
    private final Deque<Integer> data = new LinkedList<>();
    private DataListener listener;

    public void addListener(DataListener listener) {
        if (listener == null)
            throw new NullPointerException("Listener is null");

        this.listener = listener;
    }

    @Override
    public void add(int val) {
        data.add(val);
        if (data.size() > 15) {
            if (listener != null) {
                try {
                    listener.setNewSamples(data);
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
            data.clear();
        }
    }
}
