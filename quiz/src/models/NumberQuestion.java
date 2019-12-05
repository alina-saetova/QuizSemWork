package models;

public class NumberQuestion {

    private int id;
    private String question;
    private int answer;

    public NumberQuestion(int id, String question, int answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
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

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "NumberQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer" + answer +
                '}';
    }
}
