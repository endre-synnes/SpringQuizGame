package com.endre.java.springquizgame.po;

import com.endre.java.springquizgame.po.ui.MatchPO;
import selenium.PageObject;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class IndexPO extends PageObject {

    public IndexPO(WebDriver driver, String host, int port){
        super(driver, host, port);
    }

    public void toStartPage(){
        getDriver().get(host + ":" + port);
    }


    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("Quiz Game");
    }

    public MatchPO startNewMatch(){
        clickAndWait("newMatchBtnId");
        MatchPO po = new MatchPO(this);
        assertTrue(po.isOnPage());

        return po;
    }
}
