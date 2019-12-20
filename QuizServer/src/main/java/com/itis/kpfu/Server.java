package com.itis.kpfu;

import com.itis.kpfu.helpers.DataSource;
import com.itis.kpfu.models.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements ClientConnectionListener {

    private final int PORT = 1234;
    public final ArrayList<ClientConnection> connections = new ArrayList<>();

//    private static TestQuestionDAO questionDAO = new TestQuestionDAO();
//    private TestAnswerDAO answerDAO = new TestAnswerDAO();
    private DataSource dataSource;
    private static List<TestQuestion> questionList = new ArrayList<>();
    private List<TestAnswer> answers = new ArrayList<>();
    private TestAnswer rightAnswer;
    private int indexQuestion = 0;
    private int indexOfRightAnswer = 0;
    private int countConReq = 0;

    public Map<String, Integer> players = new HashMap<>();

    public static void main(String[] args) throws ClassNotFoundException {
        new Server();
    }

    public Server() {
        System.out.println("server is running");
        dataSource = DataSource.getInstance();
        questionList = dataSource.getTestQuestions();
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
        System.out.println("onready server " + connection.getName());
        connections.add(connection);
        updateListPlayers(connection);
        if (connections.size() == 1) {
            goToNextQuestion();
        }
        else {
            countConReq++;
            connection.sendString(new StringBuffer("new con\t").append(connections.size() - 1));
        }
    }

    public void updateListPlayers(ClientConnection connection) {
        if (players.containsKey(connection.getName())) {
            players.put(connection.getName(), players.get(connection.getName()) + 1);
        }
        else {
            players.put(connection.getName(), 0);
        }
        sendListPlayer();
    }

    private void sendListPlayer() {
        StringBuffer sb = new StringBuffer("players\t");
        for (String key : players.keySet()) {
            sb.append(key).append("\t").append(players.get(key)).append(" \t");
        }
        for (ClientConnection c : connections) {
            c.sendString(sb);
        }
    }

    private void goToNextQuestion() {
        StringBuffer data = new StringBuffer("question\t");
        data
                .append(indexQuestion)
                .append("\t")
                .append(questionList.get(indexQuestion).getQuestion())
                .append("\t");
        if (indexQuestion >= 10) {
            return;
        }
        answers = dataSource.getQuestionTestAnswers(questionList.get(indexQuestion).getId());
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
        else if (answer.equals("request")) {
            sendQuestionToClients();
        }
    }

    private void sendQuestionToClients() {
        if (indexQuestion == 9) {
            String winner = "";
            int scoreWin = 0;
            for (String key : players.keySet()) {
                if (players.get(key) > scoreWin) {
                    winner = key;
                    scoreWin = players.get(key);
                }
            }
            if (winner.equals("")) {
                for (ClientConnection c : connections) {
                    c.sendString(new StringBuffer("status\tnoone"));
                }
            }
            else {
                for (ClientConnection c : connections) {
                    if (c.getName().equals(winner)) {
                        c.sendString(new StringBuffer("status\twin"));
                    } else {
                        c.sendString(new StringBuffer("status\tlose\t").append(winner));
                    }
                }
            }
        }
        else {
            countConReq++;
            if (countConReq == connections.size()) {
                indexQuestion++;
                goToNextQuestion();
                countConReq = 0;
            }
        }
    }

    private void checkAnswer(ClientConnection connection, String answer) {
        String[] ans = answer.split("\t");
        if (ans[1].equals(rightAnswer.getAnswer())) {
            connection.sendString(new StringBuffer("correctness\t").append("true"));
            updateListPlayers(connection);
        }
        else {
            connection.sendString(new StringBuffer("correctness\t").append("false"));
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


