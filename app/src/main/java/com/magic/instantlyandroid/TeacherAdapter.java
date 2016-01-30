package com.magic.instantlyandroid;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by manikant.upadhyay on 1/4/2016.
 */
public class TeacherAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> questionList;

    //Constructor
    public TeacherAdapter(Context context,ArrayList<String> questionList){
        this.questionList = questionList;
        this.context = context;
    }

    @Override
    public int getCount() {

        Log.v("Teacher ","Teachers ");
        return this.questionList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RelativeLayout relativeLayout = new RelativeLayout(this.context);

        relativeLayout.setMinimumHeight(40);
        relativeLayout.setMinimumWidth(140);



        TextView tv = new TextView(this.context);
        tv.setText("Question");
        tv.setHeight(80);
        tv.setWidth(140);
        relativeLayout.addView(tv);

        return relativeLayout;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
