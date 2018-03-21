package com.endre.java.springquizgame.selenium.po;

import com.endre.java.springquizgame.selenium.PageObject;
import com.endre.java.springquizgame.selenium.po.ui.MatchPO;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class IndexPO extends LayoutPO {

    public IndexPO(WebDriver driver, String host, int port){
        super(driver, host, port);
    }

    public IndexPO(PageObject other) {
        super(other);
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
