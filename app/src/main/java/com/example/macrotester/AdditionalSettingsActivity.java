//package com.example.macrotester;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.ArrayList;
//
//import UtilGroup.Configuration;
//import UtilGroup.EventHistory;
//
//public class AdditionalSettingsActivity extends AppCompatActivity {
//
//    private TextView btnAdd;
//    private Context mContext;
//    private Configuration mConfig;
//    private EventHistory mEventHistory;
//    private Gson gson;
//
//    private ArrayList<keywordEvent> models;
//
//    private EditText txtIdx, txtSiteKeyword, txtKeyword, txtWaitTime;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_additional_settings);
//
//        InitControls();
//
//        LoadData();
//
//        AddListner();
//    }
//
//    private void LoadData() {
//        TypeToken<ArrayList<keywordEvent>> collectionType = new TypeToken<ArrayList<keywordEvent>>(){};
//        String content = mConfig.getItem("Keywords");
//        models = gson.fromJson(content, collectionType);
//
//        if(models == null)
//        {
//            models = new ArrayList<keywordEvent>();
//        }
//    }
//
//    private void AddListner() {
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int lastIdx = GetLastIdx() +1;
//                String siteKeyword, keyword;
//                siteKeyword = txtSiteKeyword.getText().toString();
//                keyword = txtKeyword.getText().toString();
//
//                keywordEvent model = new keywordEvent(lastIdx, siteKeyword, keyword, 1);
//                models.add(model);
//
//                String content = gson.toJson(models);
//                mConfig.setItem("Keywords", content);
//
//                ClearData();
//            }
//
//        });
//    }
//
//
//    private void ClearData() {
//        Toast.makeText(mContext,"추가되었습니다.", Toast.LENGTH_LONG).show();
//        txtIdx.setText("");
//        txtSiteKeyword.setText("");
//        txtKeyword.setText("");
//    }
//    private void SaveConfig()
//    {
//        mConfig.setItem("Keywords", gson.toJson(models));
//    }
//
//    private int GetLastIdx()
//    {
//        int max = 0;
//        if(models.size()>0)
//            max = models.get(0).getIdx();
//
//        // 배열의 모든 요소를 반복하여 최대값을 찾음
//        for (int i = 1; i < models.size(); i++) {
//            if (models.get(i).getIdx() > max) {
//                max = models.get(i).getIdx(); // 현재 요소가 현재까지의 최대값보다 크면 최대값을 갱신
//            }
//        }
//        return max;
//    }
//
//    private void InitControls() {
//        mContext = getApplicationContext();
//        mEventHistory = new EventHistory();
//        gson = new Gson();
//
//        mConfig = new Configuration(this.mContext, this.mEventHistory);
//
//        btnAdd = (TextView)findViewById(R.id.btnAdd);
//        txtIdx = (EditText) findViewById(R.id.txtIdx);
//        txtKeyword = (EditText) findViewById(R.id.txtKeyword);
//        txtSiteKeyword = (EditText) findViewById(R.id.txtSiteKeyword);
//        txtWaitTime = (EditText) findViewById(R.id.txtWaitTime);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SaveConfig();
//    }
//}