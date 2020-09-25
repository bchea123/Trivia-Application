package com.e.triviaapplication.data;

import com.e.triviaapplication.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Question> questionArrayList);

}
