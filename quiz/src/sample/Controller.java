package sample;

import dao.TestAnswerDAO;
import dao.TestQuestionDAO;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.TestAnswer;
import models.TestQuestion;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Controller implements Initializable {
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

    private TestQuestionDAO questionDAO = new TestQuestionDAO();
    private TestAnswerDAO answerDAO = new TestAnswerDAO();
    private List<TestQuestion> questionList = new ArrayList<>();
    private List<TestAnswer> answers = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private TestAnswer rightAnswer;
    private int indexQuestion = 0;
    private int indexOfRightAnswer = 0;
    private int scoreInt = 0;
    private Timeline flash;
    private StringProperty colorStringProperty;
    final ObjectProperty<Color> color
            = new SimpleObjectProperty<>(Color.GRAY);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttons.add(btn_answer1);
        buttons.add(btn_answer2);
        buttons.add(btn_answer3);
        buttons.add(btn_answer4);
        try {
            questionList = questionDAO.getTestQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        goToNextQuestion();
        colorStringProperty = createWarningColorStringProperty(color);
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

    public void confirm(ActionEvent actionEvent) {
        boolean flag = false;
        if (chosenAnswer.equals(rightAnswer.getAnswer())) {
            score.setText(++scoreInt + "/10");
            flag = true;
        }
        if (chosenAnswer.equals("")) {
            warning.setText("Вы не выбрали вариант ответа");
            return;
        }
        else {
            warning.setText("");
        }
        chosenButton.getStyleClass().remove("btn_chosenAnswer");
//        chosenButton.getStyleClass().remove("btn_defAnswer");
        chosenButton.styleProperty().bind(
                new SimpleStringProperty("-fx-base: ")
                        .concat(colorStringProperty)
                        .concat(";")
        );
        createTimeline(color, flag);
        flash.play();
        if (!flag) {
            buttons.get(indexOfRightAnswer).getStyleClass().remove("btn_defAnswer");
            buttons.get(indexOfRightAnswer).getStyleClass().add("btn_rightAnswer");
        }
        boolean finalFlag = flag;
        flash.setOnFinished(event -> {
            chosenButton.styleProperty().unbind();
            chosenButton.getStyleClass().add("btn_defAnswer");
            if (!finalFlag) {
                buttons.get(indexOfRightAnswer).getStyleClass().remove("btn_rightAnswer");
                buttons.get(indexOfRightAnswer).getStyleClass().add("btn_defAnswer");
            }
            indexQuestion++;
            goToNextQuestion();
        });
    }

    private void goToNextQuestion() {
        System.out.println(indexQuestion);
        if (indexQuestion >= 10) {
            return;
        }
        try {
            answers = answerDAO.getQuestionTestAnswers(questionList.get(indexQuestion).getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(answers);
        for (int i = 0; i < 4; i++) {
            buttons.get(i).setText(answers.get(i).getAnswer());
            if (answers.get(i).isCorrectness()) {
                rightAnswer = answers.get(i);
                indexOfRightAnswer = i;
            }
        }
        chosenAnswer = "";
        questionCount.setText((indexQuestion + 1) + "/10");
        text_question.setText(questionList.get(indexQuestion).getQuestion());
    }

    public void createTimeline(ObjectProperty<Color> color, boolean flag) {
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
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(color, Color.RED,  Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.7), new KeyValue(color, Color.RED,  Interpolator.LINEAR))
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
