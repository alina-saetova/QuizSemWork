package server;

import com.sun.security.ntlm.Client;
import connection.ClientConnection;
import connection.ClientConnectionListener;
import dao.TestAnswerDAO;
import dao.TestQuestionDAO;
import models.TestAnswer;
import models.TestQuestion;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server implements ClientConnectionListener {

    private final int PORT = 1234;
    private final ArrayList<ClientConnection> connections = new ArrayList<>();

    private static TestQuestionDAO questionDAO = new TestQuestionDAO();
    private TestAnswerDAO answerDAO = new TestAnswerDAO();
    private static List<TestQuestion> questionList = new ArrayList<>();
    private List<TestAnswer> answers = new ArrayList<>();
    private TestAnswer rightAnswer;
    private int indexQuestion = 0;
    private int indexOfRightAnswer = 0;

    public static void main(String[] args) {
        new Server();
        try {
            questionList = questionDAO.getTestQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Server() {
        System.out.println("server is running");
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(PORT);
            while (true) {
                new ClientConnection(this, ss.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionReady(ClientConnection connection) {
        connections.add(connection);
        //отправить всем клиентам имя того кто присоединился в общий список игроков
//        sendNamePlayerToAll(connection);
    }

//    private void sendNamePlayerToAll(String value) {
//        System.out.println(value);
//        final int lh = connections.size();
//        for (int i = 0; i < lh; i++){
//            connections.get(i).sendString(value);
//        }
//    }

    public void goToNextQuestion() {
        StringBuffer data = new StringBuffer();
        data.append(questionList.get(indexQuestion).getQuestion()).append(" ");
        if (indexQuestion >= 10) {
            return;
        }
        try {
            answers = answerDAO.getQuestionTestAnswers(questionList.get(indexQuestion).getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 4; i++) {
           data.append(answers.get(i).getAnswer()).append(" ");
            if (answers.get(i).isCorrectness()) {
                rightAnswer = answers.get(i);
                indexOfRightAnswer = i;
            }
        }
        data.append(rightAnswer).append(" ").append(indexOfRightAnswer);
        for (ClientConnection c : connections) {
            c.sendString(data);
        }
    }

    @Override
    public void onReceive(ClientConnection connection, String answer) {
        //Обрабатываем полученный ответ от игрока
        //метод 1 = отправляем на клиент инфу - о том правильный ли ответ
        //метод 2 = отправляем другим клиентам инфу - обновляем счет других игроков
        //

        if (true) {

        }
    }

    @Override
    public void onDisconnect(ClientConnection connection) {

    }
}
