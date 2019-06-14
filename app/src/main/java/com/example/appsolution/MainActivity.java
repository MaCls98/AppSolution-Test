package com.example.appsolution;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String HOST = "http://www.demo.appsolution.co";
    private EditText etCorreo;
    private EditText etPass;
    private Button btnLogin;

    private JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCorreo = findViewById(R.id.et_correo);
        etPass = findViewById(R.id.et_pass);

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }


    private void loginUser(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result;
                    String inputLine;

                    URL url = new URL(HOST + "/api/login/user");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonUser = new JSONObject();
                    jsonUser.put("email", etCorreo.getText().toString());
                    jsonUser.put("password", etPass.getText().toString());


                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonUser.toString());

                    os.flush();
                    os.close();

                    InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    while((inputLine = reader.readLine()) != null){
                        stringBuilder.append(inputLine);
                    }
                    reader.close();
                    streamReader.close();
                    result = stringBuilder.toString();

                    jsonResponse = new JSONObject(result);
                    if (result != null){
                        showUser(jsonResponse);
                    }else {
                        Toast t = Toast.makeText(getBaseContext(), "Datos erroneos", Toast.LENGTH_SHORT);
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void showUser(JSONObject jsonResponse) {
        Intent userProfile = new Intent(getBaseContext(), UserProfile.class);
        userProfile.putExtra("json", jsonResponse.toString());
        startActivity(userProfile);
    }
}
