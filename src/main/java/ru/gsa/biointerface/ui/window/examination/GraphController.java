package ru.gsa.biointerface.ui.window.examination;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Graph;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.graph.ContentForWindow;

import java.net.URL;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class GraphController implements ContentForWindow {
    private final ArrayList<XYChart.Data<Long, Integer>> samples = new ArrayList<>();
    private final ObservableList<XYChart.Data<Long, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private Graph graph;
    private int start = 0;
    private int capacity = 0;

    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private Text nameText;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Long, Integer> graphic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int min = -2048,
                max = 2047,
                tickUnit = max / 4,
                tickCount = (max - min) / tickUnit;

        axisY.setTickUnit(tickUnit);
        axisY.setMinorTickCount(tickCount);
        axisY.setUpperBound(max);
        axisY.setLowerBound(min);
        graphic.getData().add(new XYChart.Series<>(dataLineGraphic));
    }

    public void setGraph(Graph graph) {
        if (graph == null)
            throw new NullPointerException("graph is null");

        if (graph.getChannelEntity() != null) {
            Channel channel = graph.getChannelEntity();
            nameText.setText(channel.getName());
        } else {
            nameText.setText("ServiceChannel " + (graph.getNumberOfChannel() + 1));
        }

        List<Sample> sampleEntities = new LinkedList<>();
        try {
            sampleEntities = SampleDAO.getInstance().getAllByGraph(graph);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        for (Sample sample : sampleEntities) {
            samples.add(new XYChart.Data<>(sample.getId(), sample.getValue()));
        }

        Platform.runLater(() -> {
            dataLineGraphic.clear();
            dataLineGraphic.addAll(samples);
        });

        this.graph = graph;
    }

    public String getName() {
        return nameText.getText();
    }

    public int getLengthGraphic() {
        return samples.size();
    }

    public void setStart(int start) {
        if (this.start != start) {
            this.start = start;
            setAxisXSize();
        }
    }

    public void setCapacity(int capacity) {
        if (this.capacity != capacity) {
            this.capacity = capacity;
            setAxisXSize();
        }
    }

    private void setAxisXSize() {
        axisX.setTickUnit(capacity >> 3);
        axisX.setLowerBound(start);
        axisX.setUpperBound(start + capacity - 1);
    }

    @Override
    public void resizeWindow(double height, double width) {
        anchorPaneRoot.setPrefHeight(height);
        anchorPaneRoot.setPrefWidth(width);

        graphic.setPrefHeight(height);
        graphic.setPrefWidth(width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphController graphController = (GraphController) o;
        return Objects.equals(graph, graphController.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }
}
