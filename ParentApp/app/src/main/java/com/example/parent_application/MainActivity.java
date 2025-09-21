package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private Chatbot chatbot;
    private Stack<String> questionHistory;
    ConstraintLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        back=findViewById(R.id.Constraint);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, parent_settings.class);
                startActivity(intent);
                finish();
            }
        });

        questionHistory = new Stack<>();

        // Initialize chatbot with questions
        List<Question> questions = new ArrayList<>();
        Question mainQuestion = new Question("1", "What is your main issue?", null);

        Question marksIssue = new Question("1.1", "Issues with student marks", null);
        marksIssue.addSubQuestion(new Question("1.1.1", "How to view marks?", "To view marks, go to the 'Marks' section in the app."));
        marksIssue.addSubQuestion(new Question("1.1.2", "Marks are not updated", "Please ensure you have a stable internet connection. If the problem persists, contact support."));
        marksIssue.addSubQuestion(new Question("1.1.3", "Incorrect marks displayed", "Please contact the teacher to correct the marks."));

        mainQuestion.addSubQuestion(marksIssue);

        Question attendanceIssue = new Question("1.2", "Issues with attendance", null);
        attendanceIssue.addSubQuestion(new Question("1.2.1", "How to view attendance?", "To view attendance, go to the 'Attendance' section in the app."));
        attendanceIssue.addSubQuestion(new Question("1.2.2", "Attendance is incorrect", "Please contact the teacher to verify and correct the attendance record."));

        mainQuestion.addSubQuestion(attendanceIssue);

        Question resourcesIssue = new Question("1.3", "Issues with downloading resources", null);
        resourcesIssue.addSubQuestion(new Question("1.3.1", "How to download resources?", "To download resources, go to the 'Resources' section in the app."));
        resourcesIssue.addSubQuestion(new Question("1.3.2", "Unable to download resources", "Please check your internet connection and ensure you have enough storage space."));

        mainQuestion.addSubQuestion(resourcesIssue);

        Question messagingIssue = new Question("1.4", "Issues with messaging teacher", null);
        messagingIssue.addSubQuestion(new Question("1.4.1", "How to message a teacher?", "To message a teacher, go to the 'Messages' section in the app and select the teacher you want to contact."));
        messagingIssue.addSubQuestion(new Question("1.4.2", "Unable to send message", "Please ensure you have a stable internet connection. If the problem persists, contact support."));

        mainQuestion.addSubQuestion(messagingIssue);

        Question profileIssue = new Question("1.5", "Issues with profile details", null);
        profileIssue.addSubQuestion(new Question("1.5.1", "How to view profile details?", "To view profile details, go to the 'Profile' section in the app."));
        profileIssue.addSubQuestion(new Question("1.5.2", "Profile details are incorrect", "Please contact support to update your profile details."));

        mainQuestion.addSubQuestion(profileIssue);

        Question appDetailsIssue = new Question("1.6", "Issues with app details", null);
        appDetailsIssue.addSubQuestion(new Question("1.6.1", "How to view app details?", "To view app details, go to the 'App Details' section in the app."));

        mainQuestion.addSubQuestion(appDetailsIssue);

        Question feedbackIssue = new Question("1.7", "Feedback and support", null);
        feedbackIssue.addSubQuestion(new Question("1.7.1", "How to provide feedback?", "To provide feedback, go to the 'Feedback' section in the app and fill out the feedback form."));
        feedbackIssue.addSubQuestion(new Question("1.7.2", "How to contact support?", "To contact support, go to the 'Support' section in the app and follow the instructions to get help."));

        mainQuestion.addSubQuestion(feedbackIssue);

        questions.add(mainQuestion);

        chatbot = new Chatbot(questions);

        displayQuestion("1");

        // Set up the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!questionHistory.isEmpty()) {
                    displayQuestion(questionHistory.pop());
                }
            }
        });
    }

    private void displayQuestion(String questionId) {
        Question question = chatbot.getQuestionById(questionId);
        if (question != null) {
            LinearLayout layout = findViewById(R.id.chatbotLayout);
            layout.removeAllViews();

            TextView questionTextView = new TextView(this);
            questionTextView.setText(question.getText());
            layout.addView(questionTextView);

            if (question.getSubQuestions().isEmpty()) {
                TextView answerTextView = new TextView(this);
                answerTextView.setText(question.getAnswer());
                layout.addView(answerTextView);
            } else {
                for (Question subQuestion : question.getSubQuestions()) {
                    Button subQuestionButton = new Button(this);
                    subQuestionButton.setText(subQuestion.getText());
                    subQuestionButton.setOnClickListener(view -> {
                        questionHistory.push(question.getId());
                        displayQuestion(subQuestion.getId());
                    });
                    layout.addView(subQuestionButton);
                }
            }
        }
    }
}
