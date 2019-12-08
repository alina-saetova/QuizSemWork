package dao;

import helpers.ConnectionToDatabase;
import models.TestQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestQuestionDAO {

    private Connection connection;

    {
        try {
            connection = ConnectionToDatabase.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TestQuestion getTestQuestionById(int idUser) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from testquestions where id = ?");
        ps.setInt(1, idUser);
        ResultSet rs = ps.executeQuery();
        TestQuestion q = null;
        while (rs.next()) {
            q = new TestQuestion(
                    rs.getInt("id"),
                    rs.getString("question")
            );
        }
        return q;
    }

    public List<TestQuestion> getTestQuestions() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from testquestions");
        ResultSet rs = ps.executeQuery();
        List<TestQuestion> questions = new ArrayList<>();
        while (rs.next()) {
            questions.add(new TestQuestion(
                    rs.getInt("id"),
                    rs.getString("question")
            ));
        }
        List<TestQuestion> randomQuestions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomQuestions.add(questions.get((int) (Math.random() * questions.size())));
        }
        return randomQuestions;
    }
}
