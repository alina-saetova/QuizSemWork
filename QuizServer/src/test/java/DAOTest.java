import com.itis.kpfu.dao.*;
import org.mockito.Mockito;
import org.junit.*;

import java.sql.SQLException;
import java.util.List;

public class DAOTest {

    private TestQuestionDAO qDao;
    private TestAnswerDAO aDao;
//    private GameController controller;


    @Before
    public void init() {
        qDao = new TestQuestionDAO();
        aDao = new TestAnswerDAO();
//        controller = new GameController();
    }

    @Test
    public void getQuestionTest() throws SQLException {
        Assert.assertNotNull(qDao.getTestQuestionById(1));
    }

//    @Test
//    public void checkQuestionAnswersTest() throws SQLException {
//        List<TestAnswer> list = aDao.getQuestionTestAnswers(1);
//        Assert.assertEquals(1, list.get(0).getQuestion_id());
//    }
//
//    @Test
//    public void checkListPlayersTest() {
//        controller.createTimeline(true);
//        Assert.assertEquals(6, controller.flash.getCycleCount());
//    }

}
