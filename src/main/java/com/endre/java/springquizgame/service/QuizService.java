package com.endre.java.springquizgame.service;

import com.endre.java.springquizgame.entity.Quiz;
import com.endre.java.springquizgame.entity.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

@Service
@Transactional
public class QuizService {

    @Autowired
    private EntityManager em;

    public long createQuiz(long subCategoryId, String question, String firstAnswer,
                           String secondAnswer, String thirdAnswer, String fourthAnswer,
                           int indexOfCorrectAnswer){

        SubCategory subCategory = em.find(SubCategory.class, subCategoryId);
        if (subCategory == null){
            throw new IllegalArgumentException("Could not find matching Subcategory!");
        }


        Quiz quiz = new Quiz();
        quiz.setQuestion(question);
        quiz.setFirstAnswer(firstAnswer);
        quiz.setSecondAnswer(secondAnswer);
        quiz.setThirdAnswer(thirdAnswer);
        quiz.setFourthAnswer(fourthAnswer);
        quiz.setIndexOfCorrectAnswer(indexOfCorrectAnswer);
        quiz.setSubCategory(subCategory);

        em.persist(quiz);

        return quiz.getId();
    }


    public List<Quiz> getQuizzes(){
        TypedQuery<Quiz> query = em.createQuery("select q from Quiz q", Quiz.class);
        return query.getResultList();
    }

    public Quiz getQuiz(long id){
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null){
            throw new IllegalArgumentException("Could not find Quiz matching this id");
        }
        return quiz;
    }


    public List<Quiz> getRandomQuizzes(int n, long categoryId) {

        TypedQuery<Long> sizeQuery = em.createQuery(
                "select count(q) from Quiz q where q.subCategory.parent.id=?1", Long.class);
        sizeQuery.setParameter(1, categoryId);
        long size = sizeQuery.getSingleResult();

        if (n > size) {
            throw new IllegalArgumentException("Cannot choose " + n + " unique quizzes out of the " + size + " existing");
        }

        Random random = new Random();

        List<Quiz> quizzes = new ArrayList<>();
        Set<Integer> chosen = new HashSet<>();

        while (chosen.size() < n) {

            int k = random.nextInt((int) size);
            if (chosen.contains(k)) {
                continue;
            }
            chosen.add(k);

            TypedQuery<Quiz> query = em.createQuery(
                    "select q from Quiz q where q.subCategory.parent.id=?1", Quiz.class);
            query.setParameter(1, categoryId);
            query.setMaxResults(1);
            query.setFirstResult(k);

            quizzes.add(query.getSingleResult());
        }

        return  quizzes;
    }
}
