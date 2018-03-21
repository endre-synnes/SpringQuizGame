package com.endre.java.springquizgame.frontend.controller;

import com.endre.java.springquizgame.backend.entity.Category;
import com.endre.java.springquizgame.backend.entity.Quiz;
import com.endre.java.springquizgame.backend.service.CategoryService;
import com.endre.java.springquizgame.backend.service.MatchStatsService;
import com.endre.java.springquizgame.backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class MatchController implements Serializable {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private MatchStatsService statsService;

    @Autowired
    private UserInfoController infoController;

    private final int NUMBER_QUIZZES = 3;

    private boolean gameIsOn = false;
    private Long selectedCategoryId;
    private int counter;
    private List<Quiz> questions;


    public boolean isMatchOn(){
        return gameIsOn;
    }


    public String newMatch(){

        String username = infoController.getUserName();

        if (gameIsOn){
            statsService.reportDefeat(username);
        }

        gameIsOn = true;

        selectedCategoryId = null;

        return "/ui/match.jsf&faces-redirect=true";
    }


    public List<Category> getCategories(){
        return categoryService.getAllCategories(false);
    }


    public boolean isCategorySelected(){
        return selectedCategoryId != null;
    }


    public void selectCategory(long id){
        selectedCategoryId = id;
        counter = 0;
        questions = quizService.getRandomQuizzes(NUMBER_QUIZZES, selectedCategoryId);
    }


    public Quiz getCurrentQuiz(){
        return questions.get(counter);
    }

    public String answerQuiz(int index){

        String username = infoController.getUserName();

        Quiz quiz = getCurrentQuiz();
        if(index == quiz.getIndexOfCorrectAnswer()){
            counter++;
            if(counter == NUMBER_QUIZZES){
                gameIsOn = false;
                statsService.reportVictory(username);
                return "result.jsf?victory=true&faces-redirect=true";
            }
        } else {
            gameIsOn = false;
            statsService.reportDefeat(username);
            return "result.jsf?defeat=true&faces-redirect=true";
        }

        return null;
    }

    public int getIncreasedCounter(){
        return counter + 1;
    }

    public int getNumberOfQuizzes(){
        return NUMBER_QUIZZES;
    }




}
