package com.example.cafemoa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//리뷰작성

public class ReviewActivity extends AppCompatActivity {
    String name;
    String loginID;
    String loginSort;
    float rate;
    EditText mEditTextreview, mEditTexttitle;
    TextView mTextViewResult;
    private   AlertDialog dialog;

    private static String IP_ADDRESS = "203.237.179.120:7003";
    private static String TAG = "phpreviewup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        loginID = intent.getExtras().getString("loginID");
        loginSort = intent.getExtras().getString("loginSort");


        mTextViewResult = (TextView)findViewById(R.id.textView_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        TextView reviewText=(TextView) findViewById(R.id.reviewText);
        EditText titleEdit=(EditText) findViewById(R.id.titleEdit);
        EditText reviewEdit=(EditText) findViewById(R.id.reviewEdit);
        Button cancelButton=(Button) findViewById(R.id.cancelButton);
        Button okButton=(Button)findViewById(R.id.okButton);
        RatingBar reviewRating=(RatingBar)findViewById(R.id.reviewRating);

        mEditTexttitle = (EditText) findViewById(R.id.titleEdit);
        mEditTextreview = (EditText) findViewById(R.id.reviewEdit);





        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( ReviewActivity.this,InformationActivity.class );
                startActivity( intent );
            }
        });

        reviewRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/ReviewUpload.php", name, Float.toString(rate), loginID, mEditTexttitle.getText().toString(), mEditTextreview.getText().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                dialog = builder.setMessage("리뷰작성이 완료되었습니다.")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            finish();
                            }
                        })

                        .create();
                dialog.show();


                return;
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String)params[1];
            String rate = (String)params[2];
            String userID = (String)params[3];
            String title = (String)params[4];
            String review = (String)params[5];


            String serverURL = (String)params[0];
            String postParameters = "name=" + name + "&rate=" + rate + "&userID=" + userID + "&title=" + title + "&review=" + review;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}
