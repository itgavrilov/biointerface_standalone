package ru.gsa.biointerface.ui.window.metering;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.ui.window.graph.CheckBoxOfGraph;
import ru.gsa.biointerface.ui.window.graph.ContentForWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class GraphForMeteringController implements GraphUpdater, ContentForWindow {
    private final ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private Graph graph;
    private final StringConverter<Channel> converter = new StringConverter<>() {
        @Override
        public String toString(Channel channel) {
            String str = "Channel " + (graph.getNumberOfChannel() + 1);
            if (channel != null)
                str = channel.toString();
            return str;
        }

        @Override
        public Channel fromString(String string) {
            return null;
        }
    };
    private CheckBoxOfGraph checkBox;
    private Boolean isReady = false;
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private ComboBox<Channel> nameComboBox;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Integer, Integer> graphic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int min = -2048,
                max = 2047,
                tickUnit = max / 4,
                tickCount = (max - min) / tickUnit;
        setCapacity(10);


        axisY.setTickUnit(tickUnit);
        axisX.setMinorTickCount(tickCount);
        axisY.setUpperBound(max);
        axisY.setLowerBound(min);
        graphic.getData().add(new XYChart.Series<>(dataLineGraphic));
        nameComboBox.setConverter(converter);
    }

    public void onNameComboBoxShowing() {
        ObservableList<Channel> list = FXCollections.observableArrayList();
        try {
            list.addAll(Channel.getAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        nameComboBox.getItems().clear();
        nameComboBox.getItems().addAll(list);
    }

    public void nameComboBoxSelect() {
        graph.setChannel(nameComboBox.getValue());

        nameComboBox.getEditor().setText(graph.getName());
        checkBox.setText(graph.getName());
    }

    public void setGraph(Graph graph) {
        if (graph == null)
            throw new NullPointerException("graph is null");

        this.graph = graph;
        checkBox.setText(graph.getName());
    }

    public void setCheckBox(CheckBoxOfGraph checkBox) {
        if (checkBox == null)
            throw new NullPointerException("checkBox is null");

        this.checkBox = checkBox;
    }

    @Override
    public void update(List<Integer> data) {
        Platform.runLater(() -> {
            for (int i = 0; i < data.size(); i++) {
                dataLineGraphic.get(i).setYValue(data.get(i));
            }
            setReady(true);
        });
    }

    @Override
    public void setCapacity(int capacity) {
        if (capacity < 7)
            capacity = 7;
        int capacity1 = 1 << capacity;
        if (dataLineGraphic.size() > capacity1) {
            int tmpDelta = dataLineGraphic.size() - capacity1 - 1;
            for (int i = 0; i < capacity1; i++) {
                dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i + tmpDelta).getYValue());
            }
            dataLineGraphic.remove(capacity1, dataLineGraphic.size());
        } else if (dataLineGraphic.size() < capacity1) {
            ArrayList<XYChart.Data<Integer, Integer>> tmp = new ArrayList<>();

            while (tmp.size() < capacity1 - dataLineGraphic.size()) {
                tmp.add(new XYChart.Data<>(tmp.size(), 0));
            }

            dataLineGraphic.forEach(o -> tmp.add(new XYChart.Data<>(tmp.size(), o.getYValue())));

            Platform.runLater(() -> {
                dataLineGraphic.clear();
                dataLineGraphic.addAll(tmp);
            });
        }

        setAxisXSize(capacity1);
    }

    private void setAxisXSize(int countPoint) {
        axisX.setTickUnit(countPoint >> 3);
        axisX.setUpperBound(countPoint - 1);
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void setReady(boolean Ready) {
        isReady = Ready;
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
        GraphForMeteringController channelController = (GraphForMeteringController) o;
        return Objects.equals(graph, channelController.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }
}
