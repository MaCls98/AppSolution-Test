package com.example.appsolution;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserProfile extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvCompania;
    private TextView tvDireccionComp;
    private TextView tvCelular;
    private TextView tvCorreo;
    private ImageView ivUser;

    private String token;
    private JSONObject jsonUser;
    public String host = "http://www.demo.appsolution.co";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle bundle = getIntent().getExtras();
        try {
            JSONObject json = new JSONObject(bundle.getString("json"));
            token = json.get("token").toString();
            getJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvNombre = findViewById(R.id.tv_nombre);
        tvCompania = findViewById(R.id.tv_compania);
        tvDireccionComp = findViewById(R.id.tv_dir_comp);
        tvCelular = findViewById(R.id.tv_telefono);
        tvCorreo = findViewById(R.id.tv_correo);
        ivUser = findViewById(R.id.iv_user);
    }

    private void loadInfo(JSONObject jsonUser, String imgPath) throws JSONException {
        if (jsonUser != null){
            tvNombre.setText(jsonUser.get("name").toString());
            tvCompania.setText(jsonUser.get("name_company").toString());
            tvDireccionComp.setText(jsonUser.get("address_company").toString());
            tvCelular.setText(jsonUser.get("phone").toString());
            tvCorreo.setText(jsonUser.get("email").toString());
            String url = "http://www.demo.appsolution.co/" + imgPath;
            Log.d("Url: ", url);
            Picasso.get().load(url).into(ivUser);
        }
    }

    public void getJson() throws JSONException {
        HttpGetRequest request = new HttpGetRequest();
        request.execute();
    }

    public class HttpGetRequest extends AsyncTask<Void, Void, String> {

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(Void... params){
            String result;
            String inputLine;

            try {
                URL myUrl = new URL(host + "/api/user/profile");
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.connect();


                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();
                result = stringBuilder.toString();

                try {
                    jsonUser = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch(IOException e) {
                e.printStackTrace();
                result = "error";
            }

            return result;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                String imgPath = jsonUser.get("thumbnail").toString();
                loadInfo(jsonUser, imgPath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //No back button action
    }
}
