package com.endre.java.springquizgame.selenium;

import com.endre.java.springquizgame.Application;
import com.endre.java.springquizgame.backend.service.QuizService;
import com.endre.java.springquizgame.selenium.po.IndexPO;
import com.endre.java.springquizgame.selenium.po.SignUpPO;
import com.endre.java.springquizgame.selenium.po.ui.MatchPO;
import com.endre.java.springquizgame.selenium.po.ui.ResultPO;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;

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


    private static final AtomicInteger counter = new AtomicInteger(0);

    private String getUniqueId(){
        return "foo_SeleniumLocalIT_" + counter.getAndIncrement();
    }





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

    private IndexPO createNewUser(String username, String password){

        home.toStartPage();

        SignUpPO signUpPO = home.toSignUp();

        IndexPO indexPO = signUpPO.createUser(username, password);
        assertNotNull(indexPO);

        return indexPO;
    }

    @Before
    public void initTest() {
        driver.manage().deleteAllCookies();

        home = new IndexPO(driver, "localhost", port);

        home.toStartPage();

        assertTrue("Failed to start from Home Page", home.isOnPage());
    }


    @Test
    public void testCreateAndLogoutUser() {

        assertFalse(home.isLoggedIn());

        String username = getUniqueId();
        String password = "234567324";
        home = createNewUser(username, password);

        assertTrue(home.isLoggedIn());
        assertTrue(home.getDriver().getPageSource().contains(username));

        home.doLogout();

        assertFalse(home.isLoggedIn());
        assertFalse(home.getDriver().getPageSource().contains(username));
    }

    @Test
    public void testNewMatch() {

        createNewUser(getUniqueId(), "123456");

        MatchPO po = home.startNewMatch();
        assertTrue(po.canSelectCategory());
    }


    @Test
    public void testFirstQuiz() {

        createNewUser(getUniqueId(), "123456");

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

        createNewUser(getUniqueId(), "123456");

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

        createNewUser(getUniqueId(), "123456");

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
