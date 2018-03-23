package com.endre.java.springquizgame.selenium;

import com.endre.java.springquizgame.Application;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = RANDOM_PORT)
public class SeleniumLocalIT extends SeleniumTestBase{

    private static WebDriver driver;

    @LocalServerPort
    private int port;



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

    @Override
    protected WebDriver getDriver(){
        return driver;
    }

    @Override
    protected String getServerHost() {
        return "localhost";
    }

    @Override
    protected int getServerPort() {
        return port;
    }
}
