package models;

public class TestQuestion {

    private int id;
    private String question;
    private int right_answer_id;

    public TestQuestion(int id, String question, int right_answer_id) {
        this.id = id;
        this.question = question;
        this.right_answer_id = right_answer_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getRight_answer_id() {
        return right_answer_id;
    }

    public void setRight_answer_id(int right_answer_id) {
        this.right_answer_id = right_answer_id;
    }

    @Override
    public String toString() {
        return "TestQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", right_answer_id=" + right_answer_id +
                '}';
    }
}
