package com.endre.java.springquizgame;

import selenium.SeleniumDriverHandler;
import com.endre.java.springquizgame.po.IndexPO;
import com.endre.java.springquizgame.po.ui.MatchPO;
import com.endre.java.springquizgame.po.ui.ResultPO;
import com.endre.java.springquizgame.service.QuizService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = RANDOM_PORT)
public class SeleniumLocalIT {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private QuizService quizService;

    @BeforeClass
    public static void initClass(){
        driver = SeleniumDriverHandler.getChromeDriver();

        if (driver == null){
            throw new AssumptionViolatedException("Cannot find/initialize Chrome diver");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null){
            driver.close();
        }
    }

    private IndexPO home;

    @Before
    public void initTest() {
        driver.manage().deleteAllCookies();

        home = new IndexPO(driver, "localhost", port);

        home.toStartPage();

        assertTrue("Failed to start from Home Page", home.isOnPage());
    }

    @Test
    public void testNewMatch() {
        MatchPO po = home.startNewMatch();
        assertTrue(po.canSelectCategory());
    }


    @Test
    public void testFirstQuiz() {
        MatchPO po = home.startNewMatch();
        String ctgId = po.getCategoryIds().get(0);

        assertTrue(po.canSelectCategory());
        assertFalse(po.isQustionDisplayed());

        po.chooseCategory(ctgId);
        assertFalse(po.canSelectCategory());
        assertTrue(po.isQustionDisplayed());

        assertEquals(1, po.getQuestionCounter());
    }

    @Test
    public void testWrongAnswer() {
        MatchPO po = home.startNewMatch();
        String ctgId = po.getCategoryIds().get(0);

        po.chooseCategory(ctgId);

        long quizId = po.getQuizId();

        int rightAnswer = quizService.getQuiz(quizId).getIndexOfCorrectAnswer();
        int wrongAnswer = (rightAnswer + 1) % 4;

        ResultPO resultPO = po.answerQuestion(wrongAnswer);
        assertNotNull(resultPO);

        assertTrue(resultPO.haveLost());
        assertFalse(resultPO.haveWon());

    }


    @Test
    public void testWinAMatch() {

        MatchPO matchPO = home.startNewMatch();
        String ctgId = matchPO.getCategoryIds().get(0);
        matchPO.chooseCategory(ctgId);

        ResultPO resultPO = null;

        for (int i = 1; i <= 3; i++) {
            assertTrue(matchPO.isQustionDisplayed());
            assertEquals(i, matchPO.getQuestionCounter());

            long quizId = matchPO.getQuizId();
            int rightAnswer = quizService.getQuiz(quizId).getIndexOfCorrectAnswer();

            resultPO = matchPO.answerQuestion(rightAnswer);

            if(i != 3) {
                assertNull(resultPO);
            }
        }

        assertTrue(resultPO.haveWon());
        assertFalse(resultPO.haveLost());
    }
}
