package com.endre.java.springquizgame.po;

import org.openqa.selenium.WebDriver;
import selenium.PageObject;

public class SignUpPO extends PageObject{


    public SignUpPO(WebDriver driver, String host, int port) {
        super(driver, host, port);
    }

    public SignUpPO(PageObject other) {
        super(other);
    }

    public boolean isOnPage() {


    }
}
