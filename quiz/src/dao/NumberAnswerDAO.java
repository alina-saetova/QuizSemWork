package dao;

import helpers.ConnectionToDatabase;
import models.NumberAnswer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NumberAnswerDAO {

    private Connection connection;

    {
        try {
            connection = ConnectionToDatabase.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<NumberAnswer> getQuestionNumberAnswers(int question_id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from numberanswers where question_id = ?");
        ps.setInt(1, question_id);
        ResultSet rs = ps.executeQuery();
        List<NumberAnswer> numberAnswers = new ArrayList<>();
        while (rs.next()) {
            numberAnswers.add(new NumberAnswer(
                    rs.getInt("id"),
                    rs.getInt("answer"),
                    rs.getBoolean("correctness"),
                    rs.getInt("question_id")
            ));
        }
        return numberAnswers;
    }
}
