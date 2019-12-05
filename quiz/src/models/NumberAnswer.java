package models;

public class NumberAnswer {

    private int id;
    private int answer;
    private boolean correctness;
    private int question_id;

    @Override
    public String toString() {
        return "NumberAnswer{" +
                "id=" + id +
                ", answer=" + answer +
                ", correctness=" + correctness +
                ", question_id=" + question_id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public boolean isCorrectness() {
        return correctness;
    }

    public void setCorrectness(boolean correctness) {
        this.correctness = correctness;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public NumberAnswer(int id, int answer, boolean correctness, int question_id) {
        this.id = id;
        this.answer = answer;
        this.correctness = correctness;
        this.question_id = question_id;
    }
}
