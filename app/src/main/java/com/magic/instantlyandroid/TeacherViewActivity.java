package com.magic.instantlyandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TeacherViewActivity extends AppCompatActivity {

    private TeacherViewActivity _this = this;
    private MultipleChoiceQuestion questionItems[];

    private TextView questionTitle;
    private TextView optionTitle1;
    private TextView optionTitle2;
    private TextView optionTitle3;
    private TextView optionTitle4;
    private ListView listView;

    private Button pushButton;

    private  Integer questionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            String mData = mBundle.getString("com.parse.Data");
            System.out.println("DATA : xxxxx : " + mData);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view);

        MCQItem options1[] = {new MCQItem("Mrityunjay","no"),new MCQItem("Acky","no"),new MCQItem("Harish","no"),new MCQItem("Vimanlendu","yes")};
        MultipleChoiceQuestion question1 = new MultipleChoiceQuestion("Who is the founder of Magic?",options1);

        MCQItem options2[] = {new MCQItem("Sunder picchai","no"),new MCQItem("Satya Nadela","yes"),new MCQItem("Azeem premji","no"),new MCQItem("Bill gates","no")};
        final MultipleChoiceQuestion question2 = new MultipleChoiceQuestion("Who is the CEO of google?",options2);

        MCQItem options3[] = {new MCQItem("Lal prasad","no"),new MCQItem("Nitish Kumar","no"),new MCQItem("Jaylalitha","no"),new MCQItem("Narendra Modi","yes")};
        MultipleChoiceQuestion question3 = new MultipleChoiceQuestion("Who is the PM of India?",options3);


        MCQItem options4[] = {new MCQItem("Bengaluru","no"),new MCQItem("Pune","no"),new MCQItem("New Delhi","yes"),new MCQItem("Mumbai","yes")};
        MultipleChoiceQuestion question4 = new MultipleChoiceQuestion("What is the Capital of India?",options4);

        MultipleChoiceQuestion __questions[] = {question1,question2,question3,question4};

        this.questionItems = __questions;

        this.questionTitle = (TextView)findViewById(R.id.questionTitle);
        this.optionTitle1= (TextView)findViewById(R.id.option1);
        this.optionTitle2= (TextView)findViewById(R.id.option2);
        this.optionTitle3= (TextView)findViewById(R.id.option3);
        this.optionTitle4= (TextView)findViewById(R.id.option4);
        this.listView = (ListView)findViewById(R.id.questionList);

        //String[] listQuestions = new String[] {question1.questionTitle, question2.questionTitle,question3.questionTitle,question4.questionTitle,question1.questionTitle, question2.questionTitle,question3.questionTitle,question4.questionTitle};

        ArrayList<String> listQuestions = new ArrayList<String>();

        listQuestions.add(question1.questionTitle);
        listQuestions.add(question2.questionTitle);
        listQuestions.add(question3.questionTitle);
        listQuestions.add(question4.questionTitle);
       // listQuestions.add(question1.questionTitle);
                //new String[] {question1.questionTitle, question2.questionTitle,question3.questionTitle,question4.questionTitle,question1.questionTitle, question2.questionTitle,question3.questionTitle,question4.questionTitle};

        //ArrayAdapter<String> questionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listQuestions);
       TeacherAdapter questionAdapter = new TeacherAdapter(this,listQuestions);

        ListView listQuestionsList = (ListView)findViewById(R.id.questionList);

        listQuestionsList.setAdapter(questionAdapter);

        listQuestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Log.v("I am here ","Ok stay there"+arg2);
                _this.fillQuestion(arg2);
            }
        });

        this.fillQuestion(0);
    }

    private  void  fillQuestion(Integer index){

        MultipleChoiceQuestion selectedQuestion = this.questionItems[index];
        MCQItem optionItems[] = selectedQuestion.options;

        //MCQItem opt1 = optionItems[0];

        //Assign
        this.questionTitle.setText(selectedQuestion.questionTitle);
        this.optionTitle1.setText(optionItems[0].optionTitle);
        this.optionTitle2.setText(optionItems[1].optionTitle);
        this.optionTitle3.setText(optionItems[2].optionTitle);
        this.optionTitle4.setText(optionItems[3].optionTitle);
    }
}
