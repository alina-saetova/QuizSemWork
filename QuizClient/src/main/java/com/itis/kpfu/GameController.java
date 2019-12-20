package com.itis.kpfu;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class GameController implements Initializable, ClientConnectionListener {
    @FXML
    public Text text_question;
    @FXML
    public Button btn_answer1;
    @FXML
    public Button btn_answer2;
    @FXML
    public Button btn_answer3;
    @FXML
    public Button btn_answer4;
    @FXML
    public Text questionCount;
    @FXML
    public Text warning;
    @FXML
    public ProgressBar timeProgress;
    @FXML
    public Label labelProgress;
    @FXML
    public VBox vbox_players;

    private ClientConnection clientConnection;

    private List<Button> buttons = new ArrayList<>();
    private int indexOfRightAnswer = 0;
    public Timeline flash;
    private StringProperty colorStringProperty;
    private final ObjectProperty<Color> color
            = new SimpleObjectProperty<>(Color.GRAY);
    private static final Integer START_TIME = 10;
    private IntegerProperty timeSeconds =
            new SimpleIntegerProperty(START_TIME);
    private Timeline timeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientConnection = new ClientConnection(this, new Socket("localhost", 1234));
        } catch (IOException e) {
            e.printStackTrace();
        }
        buttons.add(btn_answer1);
        buttons.add(btn_answer2);
        buttons.add(btn_answer3);
        buttons.add(btn_answer4);
        labelProgress.textProperty().bind(
                timeSeconds.divide(100).asString());
        labelProgress.setTextFill(Color.RED);
        labelProgress.setStyle("-fx-font-size: 4em;");
        timeProgress.progressProperty()
                .bind(timeSeconds.divide(START_TIME * 100.0)
                        .subtract(1).multiply(-1));
        colorStringProperty = createWarningColorStringProperty(color);
    }

    @Override
    public void onConnectionReady(ClientConnection connection) {
        System.out.println("onready client" + connection.getName());
    }

    @Override
    public void onReceive(ClientConnection connection, String answer) {
        if (answer.startsWith("correctness")) {
            boolean flag = Boolean.parseBoolean(answer.split("\t")[1]);
            showCorrectAnswer(flag);
        }
        else if (answer.startsWith("question")){
            setQuestionAndAnswers(answer);
            setTimeAnimation(10);
        }
        else  if (answer.startsWith("players")) {
            updateUIListPlayers(answer);
        }
        else if (answer.startsWith("new con")) {
            Platform.runLater(() -> {
                text_question.setText("Следующий вопрос появится через несколько секунд :)");
                buttons.get(0).setText("раз");
                buttons.get(1).setText("раз");
                buttons.get(2).setText("это");
                buttons.get(3).setText("хардбасс");
            });
        }
        else if (answer.startsWith("status")) {
            System.out.println(answer);
            if (answer.split("\t")[1].equals("win")) {
                Platform.runLater(() -> text_question.setText("Ура! Вы выиграли!"));
            }
            else if (answer.split("\t")[1].equals("noone")) {
                Platform.runLater(() -> text_question.setText("Победила дружба :)"));
            }
            else {
                Platform.runLater(() -> text_question.setText("К вашему сожалению, выиграл " + answer.split("\t")[2]));
            }
        }

    }

    private void updateUIListPlayers(String str) {
        Platform.runLater(() -> {
            vbox_players.getChildren().clear();
            vbox_players.getChildren().add(new Text("Cписок игроков: "));
        });
        String[] tmp = str.split("\t");
        Platform.runLater(() -> {
            for (int i = 1; i < tmp.length - 1; i += 2) {
                Text text = new Text(tmp[i] + ": " + tmp[i + 1]);
                if (tmp[i].equals(clientConnection.getName())) {
                    text.setUnderline(true);
                }
                vbox_players.getChildren().add(text);
            }
        });

    }

    public void setQuestionAndAnswers(String answer) {
        String[] data = answer.split("\t");
        Platform.runLater(() -> {
            questionCount.setText((Integer.parseInt(data[1]) + 1) + "/10 вопрос");
            text_question.setText(data[2]);
            for (int i = 0; i < 4; i++) {
                buttons.get(i).setText(data[i + 3]);
            }
        });
        indexOfRightAnswer = Integer.parseInt(data[7]);
    }

    @Override
    public void onDisconnect(ClientConnection connection) {

    }

    private String chosenAnswer = "";
    private Button chosenButton;
    public void chooseAnswer(ActionEvent actionEvent) {
        chosenButton = new Button();
        chosenButton.getStyleClass().remove("btn_chosenAnswer");
        chosenButton.getStyleClass().remove("btn_defAnswer");
        chosenButton.getStyleClass().add("btn_defAnswer");
        chosenButton = ((Button)actionEvent.getSource());
        chosenButton.getStyleClass().remove("btn_defAnswer");
        chosenButton.getStyleClass().add("btn_chosenAnswer");
        chosenAnswer = chosenButton.getText();
    }

    private void confirm() {
        if (chosenAnswer.equals("")) {
            warning.setText("Вы не выбрали вариант ответа");
        }
        else {
            clientConnection.sendString(new StringBuffer("answer\t").append(chosenAnswer));
            warning.setText("");
        }
    }

    private void showCorrectAnswer(boolean flag) {
        createTimeline(flag);
        Platform.runLater(() -> {
            chosenButton.getStyleClass().remove("btn_chosenAnswer");
            chosenButton.styleProperty().bind(
                    new SimpleStringProperty("-fx-base: ")
                            .concat(colorStringProperty)
                            .concat(";")
            );
            flash.play();
            if (!flag) {
                buttons.get(indexOfRightAnswer).getStyleClass().remove("btn_defAnswer");
                buttons.get(indexOfRightAnswer).getStyleClass().add("btn_rightAnswer");
            }
            flash.setOnFinished(event -> {
                chosenButton.styleProperty().unbind();
                chosenButton.getStyleClass().add("btn_defAnswer");
                if (!flag) {
                    buttons.get(indexOfRightAnswer).getStyleClass().remove("btn_rightAnswer");
                    buttons.get(indexOfRightAnswer).getStyleClass().add("btn_defAnswer");
                }
                clientConnection.sendString(new StringBuffer("request"));
            });
       });

    }

    private void setTimeAnimation(int time) {
        if (timeline != null) {
            timeline.stop();
        }
        Platform.runLater(() -> {
            timeSeconds.set((time + 1) * 100);
            timeline = new Timeline();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(time + 1),
                            new KeyValue(timeSeconds, 0))
            );
            timeline.playFromStart();
            timeline.setOnFinished(event -> {
                if (chosenAnswer.equals("")) {
                    noConfirmedAnswer();
                }
                else {
                    confirm();
                }
            });
            chosenAnswer = "";
        });
    }

    private void noConfirmedAnswer() {
        warning.setText("Вы не выбрали вариант ответа");
        buttons.get(indexOfRightAnswer).getStyleClass().remove("btn_defAnswer");
        buttons.get(indexOfRightAnswer).styleProperty().bind(
                new SimpleStringProperty("-fx-base: ")
                        .concat(colorStringProperty)
                        .concat(";")
        );
        createTimeline(true);
        flash.play();
        flash.setOnFinished(event -> {
            buttons.get(indexOfRightAnswer).styleProperty().unbind();
            buttons.get(indexOfRightAnswer).getStyleClass().add("btn_defAnswer");
            confirm();
            warning.setText("");
            clientConnection.sendString(new StringBuffer("request"));
        });
    }

    public void createTimeline(boolean flag) {
        if (flag) {
            flash = new Timeline(
                        new KeyFrame(Duration.seconds(0),    new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.25), new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(color, Color.GREEN,  Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.7), new KeyValue(color, Color.GREEN,  Interpolator.LINEAR))
            );
            flash.setCycleCount(6);
        }
        else {
            flash = new Timeline(
                        new KeyFrame(Duration.seconds(0),    new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.25), new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.5),  new KeyValue(color, Color.RED,  Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.7),  new KeyValue(color, Color.RED,  Interpolator.LINEAR))
            );
            flash.setCycleCount(4);
        }
        flash.setAutoReverse(true);
    }

    private StringProperty createWarningColorStringProperty(final ObjectProperty<Color> color) {
        final StringProperty colorStringProperty = new SimpleStringProperty();
        setColorStringFromColor(colorStringProperty, color);
        color.addListener((observableValue, oldColor, newColor) -> setColorStringFromColor(colorStringProperty, color));
        return colorStringProperty;
    }

    private void setColorStringFromColor(StringProperty colorStringProperty, ObjectProperty<Color> color) {
        colorStringProperty.set(
                "rgba("
                        + ((int) (color.get().getRed()   * 255)) + ","
                        + ((int) (color.get().getGreen() * 255)) + ","
                        + ((int) (color.get().getBlue()  * 255)) + ","
                        + color.get().getOpacity() +
                        ")"
        );
    }
}
