import com.itis.kpfu.GameController;
import com.itis.kpfu.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControllerTest {

    private GameController gc;

    @Before
    public void init() {
        gc = new GameController();
    }

    @Test
    public void checkCycleCountInFlash() {
        gc.createTimeline(true);
        Assert.assertEquals(6, gc.flash.getCycleCount());
    }

    @Test
    public void settingQuestionTest() {
        Main.main(new String[]{"1"});
        gc.setQuestionAndAnswers("question\t2\tСколько дней в феврале в високосном году?\t27\t28\t29\t30\t2");
        Assert.assertEquals("3/10", gc.questionCount.getText());
    }

}
