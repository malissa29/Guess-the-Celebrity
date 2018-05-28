package com.example.malissafiger.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenceleb= 0;
    int locationofcorrectanswer = 0;
    String [] answers= new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;


    ImageView imageView;

    public void celebChosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationofcorrectanswer))){
            Toast.makeText(getApplicationContext(), "Correct Answer!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Wrong Answer!! It was " + celebnames.get(chosenceleb), Toast.LENGTH_SHORT).show();
        }
        createnewquestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url= new URL(urls[0]);
                HttpURLConnection connection= (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream= connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(inputStream);
                return  mybitmap;
            }
            catch (MalformedURLException e) {


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection= null;
            try{
                url= new URL(urls[0]);
                urlConnection =(HttpURLConnection)url.openConnection();
                InputStream inputStream= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(inputStream);
                int data= reader.read();
                while (data!= -1){
                    char current = (char) data;
                    result += current;
                    data= reader.read();

                }
                return result;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.imageView);
         button0 = (Button)findViewById(R.id.button0);
         button1 = (Button)findViewById(R.id.button1);
         button2 = (Button)findViewById(R.id.button2);
         button3 = (Button)findViewById(R.id.button3);



        DownloadTask task= new DownloadTask();
        String result =null;
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            String [] splitResult  = result.split(" <div class=\"sidebarContainer\"> ");

            Pattern p= Pattern.compile("<img src=\"(.*?)\"");
            Matcher m= p.matcher(splitResult[0]);

            while(m.find()){
                celeburls.add(m.group(1));
            }

            p= Pattern.compile("alt=\"(.*?)\"/");
            m= p.matcher(splitResult[0]);

            while(m.find()){
                celebnames.add(m.group(1));
            }


        }
        catch (InterruptedException e) {

            e.printStackTrace();
        }
        catch (ExecutionException e)

            e.printStackTrace();
        }
        createnewquestion();

    }

    public void createnewquestion()
    {
        Random random= new Random();
        chosenceleb =random.nextInt(celeburls.size());

        ImageDownloader imagetask = new ImageDownloader();
        Bitmap celebimg;
        try {
            celebimg =imagetask.execute(celeburls.get(chosenceleb)).get();
            imageView.setImageBitmap(celebimg);
            locationofcorrectanswer = random.nextInt(4);

            int incorrectanswerlocation;

            for (int i=0; i <4; i++)
            {
                if (i ==locationofcorrectanswer)
                {
                    answers[i]= celebnames.get(chosenceleb);

                }else
                {
                    incorrectanswerlocation= random.nextInt(celeburls.size());
                    while (incorrectanswerlocation==chosenceleb){

                        incorrectanswerlocation= random.nextInt(celeburls.size());

                    }
                    answers[i]=celebnames.get(incorrectanswerlocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (InterruptedException e) {


        } catch (ExecutionException e) {

            e.printStackTrace();
        }


    }
}
