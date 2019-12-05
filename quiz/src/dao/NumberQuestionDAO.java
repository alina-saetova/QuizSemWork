package dao;

import helpers.ConnectionToDatabase;
import models.NumberQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NumberQuestionDAO {

    private Connection connection;

    {
        try {
            connection = ConnectionToDatabase.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public NumberQuestion getNumberQuestionById(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from numberquestions where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        NumberQuestion u = null;
        while (rs.next()) {
            u = new NumberQuestion(
                    rs.getInt("id"),
                    rs.getString("question"),
                    rs.getInt("answer")
            );
        }
        return u;
    }

    public List<NumberQuestion> getAllNumberQuestions() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from numberquestions");
        ResultSet rs = ps.executeQuery();
        List<NumberQuestion> questions = new ArrayList<>();
        while (rs.next()) {
            questions.add(new NumberQuestion(
                    rs.getInt("id"),
                    rs.getString("question"),
                    rs.getInt("answer")
            ));
        }
        return questions;
    }
}
