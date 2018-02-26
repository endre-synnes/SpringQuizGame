package com.endre.java.springquizgame.service;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceTestBase {

    @Autowired
    private ResetService deleteService;

    @Before
    public void cleanDatabase(){
        deleteService.resetDatabase();
    }
}
