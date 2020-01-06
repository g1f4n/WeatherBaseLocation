package com.juaracoding.weatherbaselocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.juaracoding.weatherbaselocation.adapter.AdapterListSimple;
import com.juaracoding.weatherbaselocation.model.geolocation.GeolocationModel;
import com.juaracoding.weatherbaselocation.service.APIClient;
import com.juaracoding.weatherbaselocation.service.APIInterfacesRest;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostData extends AppCompatActivity {

    RecyclerView listPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_data);

        listPost = findViewById(R.id.listPost);

        getGeolocation();
    }

    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;
    public void getGeolocation(){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(PostData.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<GeolocationModel> call3 = apiInterface.getGeolocation();
        call3.enqueue(new Callback<GeolocationModel>() {
            @Override
            public void onResponse(Call<GeolocationModel> call, Response<GeolocationModel> response) {
                progressDialog.dismiss();
                GeolocationModel data = response.body();

                if (data !=null) {



                    AdapterListSimple adapter = new AdapterListSimple(PostData.this,data.getData().getGeolocation());

                    listPost.setLayoutManager(new LinearLayoutManager(PostData.this));
                    listPost.setItemAnimator(new DefaultItemAnimator());
                    listPost.setAdapter(adapter);




                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PostData.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(PostData.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GeolocationModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }
}
