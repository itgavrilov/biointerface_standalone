package ru.gsa.biointerface.ui.window.examination;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.service.ExaminationService;
import ru.gsa.biointerface.ui.window.AbstractWindow;
import ru.gsa.biointerface.ui.window.AlertError;
import ru.gsa.biointerface.ui.window.WindowWithProperty;
import ru.gsa.biointerface.ui.window.channel.ChannelCheckBox;
import ru.gsa.biointerface.ui.window.channel.CompositeNode;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class ExaminationController extends AbstractWindow implements WindowWithProperty<Examination> {
    private final ExaminationService examinationService;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final List<CompositeNode<AnchorPane, ChannelController>> channelGUIs = new LinkedList<>();
    private final List<ChannelCheckBox> checkBoxesOfChannel = new LinkedList<>();
    private Examination examination;
    private double graphSize = 0;
    private double graphCapacity = 0;
    private double graphStart = 0;

    @FXML
    private AnchorPane anchorPaneControl;
    @FXML
    private Text patientRecordIdText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text patronymicText;
    @FXML
    private Text birthdayText;
    @FXML
    private Text icdText;
    @FXML
    private Slider allSliderZoom;
    @FXML
    private Text idDeviceText;
    @FXML
    private VBox checkBoxOfChannelVBox;
    @FXML
    private Text dateTimeText;
    @FXML
    private TextArea commentField;
    @FXML
    private VBox channelVBox;
    @FXML
    private ScrollBar timeScrollBar;

    public ExaminationController() throws Exception {
        examinationService = ExaminationService.getInstance();
    }

    public WindowWithProperty<Examination> setProperty(Examination examination) {
        if (examination == null)
            throw new NullPointerException("examinationService is null");

        this.examination = examination;

        return this;
    }

    @Override
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null) {
            throw new NullPointerException("" +
                    "resourceSource or transitionGUI is null. " +
                    "First call setResourceAndTransition()" +
                    "");
        }
        if (examination == null) {
            throw new NullPointerException("" +
                    "examinationService is null. " +
                    "First call setParameter()" +
                    "");
        }

        examination = examinationService.loadWithGraphsById(examination.getId());
        idDeviceText.setText(String.valueOf(examination.getDevice().getId()));
        dateTimeText.setText(dateTimeFormatter.format(examination.getStarttime()));
        Patient patient = examination.getPatient();
        patientRecordIdText.setText(String.valueOf(patient.getId()));
        secondNameText.setText(patient.getSecondName());
        firstNameText.setText(patient.getFirstName());
        patronymicText.setText(patient.getPatronymic());
        birthdayText.setText(dateFormatter.format(patient.getBirthday().getTime()));

        if (patient.getIcd() != null) {
            Icd icd = patient.getIcd();
            icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        timeScrollBar.setMin(0);
        timeScrollBar.setValue(0);
        timeScrollBar.setBlockIncrement(1);
        buildingChannelsGUIs();
        transitionGUI.show();
    }

    public void buildingChannelsGUIs() {
        List<Channel> channels = examination.getChannels();
        graphCapacity = allSliderZoom.getValue();

        for (Channel channel : channels) {
            CompositeNode<AnchorPane, ChannelController> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("fxml/Channel.fxml")));
            ChannelController channelController = node.getController();
            channelController.setGraph(channel);
            channelController.setStart(0);
            channelController.setCapacity((int) graphCapacity);

            if (graphSize > channelController.getLengthGraphic() || graphSize == 0) {
                graphSize = channelController.getLengthGraphic();
            }

            channelGUIs.add(node);
            ChannelCheckBox checkBox = new ChannelCheckBox(channel.getId().getNumber());
            checkBox.setText(channelController.getName());
            checkBox.setOnAction(event -> {
                node.getNode().setVisible(checkBox.isSelected());
                drawChannelsGUI();
            });
            checkBoxesOfChannel.add(checkBox);
        }

        allSliderZoom.setMax(graphSize);
        timeScrollBar.setMax(graphSize - graphCapacity);
        timeScrollBar.setVisibleAmount(timeScrollBar.getMax() * graphCapacity / graphSize);
        allSliderZoom.valueProperty().addListener((ov, old_val, new_val) -> {
            graphCapacity = new_val.intValue();
            graphStart = timeScrollBar.getValue();

            if (graphStart > graphSize - graphCapacity) {
                graphStart = graphSize - graphCapacity;
                timeScrollBar.setValue(graphStart);
            }

            timeScrollBar.setMax(graphSize - graphCapacity);
            timeScrollBar.setVisibleAmount(timeScrollBar.getMax() * graphCapacity / graphSize);
            channelGUIs.forEach(o -> {
                o.getController().setStart((int) graphStart);
                o.getController().setCapacity((int) graphCapacity);
            });
        });
        timeScrollBar.valueProperty().addListener((ov, old_val, new_val) -> {
            graphStart = new_val.intValue();
            channelGUIs.forEach(o -> o.getController().setStart((int) graphStart));
        });
        drawChannelsGUI();
    }

    public void drawChannelsGUI() {
        channelVBox.getChildren().clear();
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });

        checkBoxOfChannelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().addAll(checkBoxesOfChannel);

        resizeWindow(anchorPaneRoot.getHeight(), anchorPaneRoot.getWidth());
    }

    public void onBack() {
        try {
            //noinspection unchecked
            ((WindowWithProperty<Patient>) generateNewWindow("fxml/PatientOpen.fxml"))
                    .setProperty(examination.getPatient())
                    .showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient record: " + e.getMessage());
        }
    }

    public void commentFieldChange() {
        if (Objects.equals(examination.getComment(), commentField.getText())) {
            String comment = examination.getComment();
            examination.setComment(commentField.getText());
            try {
                examinationService.save(examination);
            } catch (Exception e) {
                examination.setComment(comment);
                commentField.setText(comment);
                new AlertError("Error change comment for examination: " + e.getMessage());
            }
        }
    }

    @Override
    public void resizeWindow(double height, double width) {
        long count = channelGUIs.stream().
                map(CompositeNode::getNode).
                filter(Node::isVisible).count();
        double heightChannelGUIs = (height - timeScrollBar.getHeight()) / count;

        channelVBox.setPrefHeight(heightChannelGUIs);
        channelVBox.setPrefWidth(width - anchorPaneControl.getWidth());

        for (CompositeNode<AnchorPane, ChannelController> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }

    }

    @Override
    public String getTitleWindow() {
        return ": examinationService";
    }
}

