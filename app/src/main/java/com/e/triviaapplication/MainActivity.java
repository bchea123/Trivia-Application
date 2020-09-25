package com.e.triviaapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.e.triviaapplication.data.AnswerListAsyncResponse;
import com.e.triviaapplication.data.QuestionBank;
import com.e.triviaapplication.model.Question;
import com.e.triviaapplication.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String HIGH_SCORE_ID = "high_score";
    private CardView cardView;
    private TextView counterText;
    private TextView questionText;
    private TextView scoreText;
    private TextView highScoreText;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentQuestionIndex = 0;
    private int score;
    private int highScore;
    private List<Question> questionList;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView = findViewById(R.id.card_view);
        counterText = findViewById(R.id.counter_text);
        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        highScoreText = findViewById(R.id.high_score_text);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        prevButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        preferences = new Preferences(MainActivity.this);

        //CardView Attributes
        cardView.setRadius(25);
        cardView.setCardElevation(50);
        cardView.setPadding(5, 5, 5, 5);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                updateQuestion();
            }
        });

        highScore = preferences.getHighScore();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.true_button:
                questionList.get(currentQuestionIndex).setQuestionAnswered(true);
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                questionList.get(currentQuestionIndex).setQuestionAnswered(true);
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.previous_button:
                if (currentQuestionIndex > 0){
                    currentQuestionIndex --;
                    updateQuestion();
                }
                break;
            case R.id.next_button:
                if (currentQuestionIndex < questionList.size() - 1){
                    currentQuestionIndex++;
                    updateQuestion();
                }
                break;
        }
    }

    private void updateQuestion(){
        if(questionList.get(currentQuestionIndex).isQuestionAnswered() == true){
            trueButton.setEnabled(false);
            falseButton.setEnabled(false);
        }
        else{
            trueButton.setEnabled(true);
            falseButton.setEnabled(true);
        }
        questionText.setText(questionList.get(currentQuestionIndex).getAnswer());
        counterText.setText((currentQuestionIndex + 1) + " / " + questionList.size());
        scoreText.setText("Current Score: " + score);
        highScoreText.setText("High Score: " + highScore);
    }

    private void checkAnswer(boolean userInput){
        boolean correctAnswer = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (userInput == correctAnswer){
            Toast.makeText(MainActivity.this, "That's Correct!", Toast.LENGTH_SHORT).show();
            fadeAnimation();
            score += 100;
            if (score > highScore){
                highScore = score;
            }
        }
        else {
            Toast.makeText(MainActivity.this, "That's Incorrect!", Toast.LENGTH_SHORT).show();
            shakeAnimation();
            if(score > 0){
                score -= 50;
            }
        }
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        shake.setDuration(250);
        shake.setRepeatCount(1);
        shake.setRepeatMode(2); //reverse

        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                }
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimation(){
        AlphaAnimation fade = new AlphaAnimation(1.0f, 0f);
        fade.setDuration(350);
        fade.setRepeatCount(1);
        fade.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(fade);

        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                }
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.saveHighScore(highScore);
    }
}
