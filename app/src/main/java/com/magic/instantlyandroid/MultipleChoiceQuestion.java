package com.magic.instantlyandroid;

/**
 * Created by manikant.upadhyay on 12/30/2015.
 */
public class MultipleChoiceQuestion {

    MCQItem options[];
    String questionTitle = "";

    public MultipleChoiceQuestion(String questionTitle,MCQItem[] options){

        this.questionTitle = questionTitle;
        this.options = options;
    }

}
