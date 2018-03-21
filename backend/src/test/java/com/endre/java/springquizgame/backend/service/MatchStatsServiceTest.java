package com.endre.java.springquizgame.backend.service;

import com.endre.java.springquizgame.backend.StubApplication;
import com.endre.java.springquizgame.backend.entity.MatchStats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StubApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MatchStatsServiceTest extends ServiceTestBase{


    @Autowired
    MatchStatsService matchStatsService;

    @Autowired
    UserService userService;

    @Test
    public void testDefaultStats() {

        String username = "foo";

        userService.createUser(username, "123");

        MatchStats stats = matchStatsService.getMatchStats(username);

        assertEquals(0, (int) stats.getVictories());
        assertEquals(0, (int) stats.getDefeats());

    }


    @Test
    public void testStats() {

        String username = "foo";

        userService.createUser(username, "123");
        matchStatsService.reportVictory(username);
        matchStatsService.reportVictory(username);
        matchStatsService.reportDefeat(username);

        MatchStats stats = matchStatsService.getMatchStats(username);

        assertEquals(2, (int) stats.getVictories());
        assertEquals(1, (int) stats.getDefeats());
    }
}
