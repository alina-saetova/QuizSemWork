package server;

import connection.*;
import dao.*;
import models.*;

import java.io.IOException;
import java.net.ServerSocket;
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

    private Map<String, Integer> players = new HashMap<>();

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
        System.out.println(connection.getName() + " connected");
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

    private void goToNextQuestion() {
        //перееход к следующему вопросу и отправка данных на клиент
        StringBuffer data = new StringBuffer("question\t");
        data
                .append(indexQuestion)
                .append("\t")
                .append(questionList.get(indexQuestion).getQuestion())
                .append("\t");
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
        data.append(indexOfRightAnswer);
        for (ClientConnection c : connections) {
            c.sendString(data);
        }

    }


    @Override
    public void onReceive(ClientConnection connection, String answer) {
        if (answer.startsWith("answer")) {
            checkAnswer(connection, answer);
        }
        if (answer.equals("request")) {
            sendQuestionToClients();
        }
    }

    private void sendQuestionToClients() {
        System.out.println(indexQuestion);
        if (indexQuestion == 9) {
            String winner = "";
            int scoreWin = 0;
            for (String key : players.keySet()) {
                if (players.get(key) > scoreWin) {
                    winner = key;
                    scoreWin = players.get(key);
                }
            }
            for (ClientConnection c : connections) {
                if (c.getName().equals(winner)) {
                    c.sendString(new StringBuffer("status win"));
                }
                else {
                    c.sendString(new StringBuffer("status lose ").append(winner));
                }
            }
        }
        else {
            countConReq++;
            System.out.println("count requests " + countConReq);
            if (countConReq == connections.size()) {
                indexQuestion++;
                goToNextQuestion();
                countConReq = 0;
            }
        }
    }

    private void checkAnswer(ClientConnection connection, String answer) {
        String[] ans = answer.split("\t");
        System.out.println(ans[1] + " " + rightAnswer.getAnswer());
        if (ans[1].equals(rightAnswer.getAnswer())) {

            connection.sendString(new StringBuffer("correctness ").append("true"));
            updateListPlayers(connection);
        }
        else {
            connection.sendString(new StringBuffer("correctness ").append("false"));
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


