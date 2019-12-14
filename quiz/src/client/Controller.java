package client;

import connection.ClientConnection;
import connection.ClientConnectionListener;
import dao.TestAnswerDAO;
import dao.TestQuestionDAO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable, ClientConnectionListener {
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
    public Button btn_choose;
    @FXML
    public Text questionCount;
    @FXML
    public Text warning;
    @FXML
    public Text score;
    @FXML
    public ProgressBar timeProgress;
    @FXML
    public Label labelProgress;
    @FXML
    public Text playerList;
    @FXML
    public VBox vbox_players;

    ClientConnection connection;

    private List<Button> buttons = new ArrayList<>();
    private String rightAnswer;
    private int indexQuestion = 0;
    private int indexOfRightAnswer = 0;
    private int scoreInt = 0;
    private Timeline flash;
    private StringProperty colorStringProperty;
    final ObjectProperty<Color> color
            = new SimpleObjectProperty<>(Color.GRAY);
    private static final Integer STARTTIME = 15;
    private IntegerProperty timeSeconds =
            new SimpleIntegerProperty(STARTTIME);
    private Timeline timeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = new ClientConnection(this, new Socket("localhost", 1234));
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
                .bind(timeSeconds.divide(STARTTIME * 100.0)
                        .subtract(1).multiply(-1));
        colorStringProperty = createWarningColorStringProperty(color);
    }

    @Override
    public void onConnectionReady(ClientConnection connection) {

    }

    @Override
    public void onReceive(ClientConnection connection, String answer) {
        System.out.println(answer);
        if (answer.startsWith("correctness")) {
            boolean flag = Boolean.parseBoolean(answer.split(" ")[1]);
            showCorrectAnswer(flag);
        }
        else if (answer.startsWith("question")){
            setQuestionAndAnswers(answer);
            setTimeAnimation(10);
        }
        else  if (answer.startsWith("players")) {
            updateUIListPlayers(answer);
        }
        else if (answer.equals("new con")) {
            Platform.runLater(() -> {
                text_question.setText("Следующий вопрос появится через несколько секунд :)");
                buttons.get(0).setText("раз");
                buttons.get(1).setText("раз");
                buttons.get(2).setText("это");
                buttons.get(3).setText("хардбасс");

            });
        }
    }

    private void updateUIListPlayers(String str) {
        Platform.runLater(() -> {
            vbox_players.getChildren().clear();
            vbox_players.getChildren().add(new Text("список игроков: "));
        });
        String[] tmp = str.split(" ");
        Platform.runLater(() -> {
            for (int i = 1; i < tmp.length - 1; i += 2) {
                vbox_players.getChildren().add(new Text(tmp[i] + ": " + tmp[i + 1]));
            }
        });

    }

    private void setQuestionAndAnswers(String answer) {
        String[] data = answer.split("\t");
        Platform.runLater(() -> {
            text_question.setText(data[1]);
            for (int i = 0; i < 4; i++) {
                buttons.get(i).setText(data[i + 2]);
            }
        });
        rightAnswer = data[6];
        indexOfRightAnswer = Integer.parseInt(data[7]);
        indexQuestion = Integer.parseInt(data[8]);
    }

    @Override
    public void onDisconnect(ClientConnection connection) {

    }

    private String chosenAnswer = "";
    private Button chosenButton = new Button();
    public void chooseAnswer(ActionEvent actionEvent) {
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
            connection.sendString(new StringBuffer("answer ").append(chosenAnswer));
            warning.setText("");
        }
    }

    private void showCorrectAnswer(boolean flag) {
        createTimeline(color, flag);
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
                connection.sendString(new StringBuffer("request"));
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
            questionCount.setText((indexQuestion + 1) + "/10");

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
        createTimeline(color, true);
        flash.play();
        flash.setOnFinished(event -> {
            buttons.get(indexOfRightAnswer).styleProperty().unbind();
            buttons.get(indexOfRightAnswer).getStyleClass().add("btn_defAnswer");
            warning.setText("");
            System.out.println("nochosen");
            confirm();
            connection.sendString(new StringBuffer("request"));
        });
    }

    private void createTimeline(ObjectProperty<Color> color, boolean flag) {
        if (flag) {
            flash = new Timeline(
                    new KeyFrame(Duration.seconds(0),    new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.25), new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(color, Color.GREEN,  Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.7), new KeyValue(color, Color.GREEN,  Interpolator.LINEAR))
            );
        }
        else {
            flash = new Timeline(
                    new KeyFrame(Duration.seconds(0),    new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.25), new KeyValue(color, Color.GRAY, Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.5),  new KeyValue(color, Color.RED,  Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.7),  new KeyValue(color, Color.RED,  Interpolator.LINEAR))
            );
        }
        flash.setCycleCount(6);
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
