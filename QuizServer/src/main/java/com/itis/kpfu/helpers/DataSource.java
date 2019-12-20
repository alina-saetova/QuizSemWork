package com.itis.kpfu.helpers;

import com.itis.kpfu.models.TestAnswer;
import com.itis.kpfu.models.TestQuestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataSource {

    private static DataSource instance;
    List<TestQuestion> questions = new ArrayList<>();
    List<TestAnswer> answers = new ArrayList<>();

    private DataSource() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream q = classLoader.getResourceAsStream("questions.txt");
        Scanner scan = new Scanner(q);
        while (scan.hasNextLine()) {
            String[] tmp = scan.nextLine().split("\t");
            questions.add(
                    new TestQuestion(
                            Integer.parseInt(tmp[0]),
                            tmp[1])
            );
        }
        InputStream a = classLoader.getResourceAsStream("answers.txt");
        assert a != null;
        scan = new Scanner(a);
        while (scan.hasNextLine()) {
            String[] tmp = scan.nextLine().split("\t");
            answers.add(
                    new TestAnswer(
                        Integer.parseInt(tmp[0]),
                        tmp[1],
                        Boolean.parseBoolean(tmp[2]),
                        Integer.parseInt(tmp[3])
                    )
            );
        }

    }

    public List<TestQuestion> getTestQuestions() {
        List<TestQuestion> randomQuestions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomQuestions.add(questions.get((int) (Math.random() * questions.size())));
        }
        return randomQuestions;
    }

    public List<TestAnswer> getQuestionTestAnswers(int question_id) {
        List<TestAnswer> testAnswers = new ArrayList<>();
        for (TestAnswer ta : answers) {
            if (ta.getQuestion_id() == question_id) {
                testAnswers.add(ta);
            }
        }
        return testAnswers;
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }
}
