package dao;

import helpers.ConnectionToDatabase;
import models.TestAnswer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestAnswerDAO {

    private Connection connection;

    {
        try {
            connection = ConnectionToDatabase.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<TestAnswer> getQuestionTestAnswers(int question_id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from testanswers where question_id = ?");
        ps.setInt(1, question_id);
        ResultSet rs = ps.executeQuery();
        List<TestAnswer> testAnswers = new ArrayList<>();
        while (rs.next()) {
            testAnswers.add(new TestAnswer(
                    rs.getInt("id"),
                    rs.getString("answer"),
                    rs.getBoolean("correctness"),
                    rs.getInt("question_id")
            ));
        }
        return testAnswers;
    }
}
