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
import java.util.*;

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
    private int countConReq = 0;

    private Map<String, Integer> players;

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        System.out.println("server is running");
        try {
            questionList = questionDAO.getTestQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        players = new HashMap<>();
//        Timer timer = new Timer();
//        TimerTask tt = new Helper();
//        timer.schedule(tt, 0, 15000);
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

//    class Helper extends TimerTask {
//
//        @Override
//        public void run() {
//            goToNextQuestion();
//            if (++indexQuestion == 10) {
//                cancel();
//            }
//        }
//    }

    @Override
    public void onConnectionReady(ClientConnection connection) {
        connections.add(connection);
        System.out.println(connection.getName() + " connected");
        //отправить всем клиентам имя того кто присоединился в общий список игроков
        updateListPlayers(connection);
        if (connections.size() == 1) {
            goToNextQuestion();
        }
        else {
            countConReq++;
            connection.sendString(new StringBuffer("new con"));
        }
    }

    private void updateListPlayers(ClientConnection connection) {
        if (players.containsKey(connection.getName())) {
            players.put(connection.getName(), players.get(connection.getName()) + 1);
        }
        else {
            players.put(connection.getName(), 0);
        }
        sendListPlayer();
    }

    private void sendListPlayer() {
        StringBuffer sb = new StringBuffer("players ");
        for (String key : players.keySet()) {
            sb.append(key).append(" ").append(players.get(key)).append(" ");
        }
        System.out.println(sb.toString());
        for (ClientConnection c : connections) {
            c.sendString(sb);
        }
    }

    public void goToNextQuestion() {
        //перееход к следующему вопросу и отправка данных на клиент
        StringBuffer data = new StringBuffer("question\t");
        data.append(questionList.get(indexQuestion).getQuestion()).append("\t");
        if (indexQuestion >= 10) {
            return;
        }
        try {
            answers = answerDAO.getQuestionTestAnswers(questionList.get(indexQuestion).getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 4; i++) {
           data.append(answers.get(i).getAnswer()).append("\t");
            if (answers.get(i).isCorrectness()) {
                rightAnswer = answers.get(i);
                indexOfRightAnswer = i;
            }
        }
        data.append(rightAnswer.getAnswer())
                .append("\t").append(indexOfRightAnswer)
                .append("\t").append(indexOfRightAnswer);
        for (ClientConnection c : connections) {
            c.sendString(data);
        }

    }


    @Override
    public void onReceive(ClientConnection connection, String answer) {
        if (answer.startsWith("answer")) {
            String[] ans = answer.split(" ");
            if (ans[1].equals(rightAnswer.getAnswer())) {
                connection.sendString(new StringBuffer("correctness ").append("true"));
                updateListPlayers(connection);
            }
            else {
                connection.sendString(new StringBuffer("correctness ").append("false"));
            }
        }
        if (answer.equals("request")) {
            System.out.println("go to next");
            countConReq++;
            if (countConReq == connections.size()) {
                goToNextQuestion();
                indexQuestion++;
                countConReq = 0;
            }
        }
    }

    @Override
    public void onDisconnect(ClientConnection connection) {
        connections.remove(connection);
        players.remove(connection.getName());
        sendListPlayer();
        System.out.println(connection.getName() + " disconnected");
    }
}


