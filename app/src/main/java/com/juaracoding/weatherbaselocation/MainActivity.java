package com.juaracoding.weatherbaselocation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juaracoding.weatherbaselocation.modelWeather.WeatherModel;
import com.juaracoding.weatherbaselocation.service.APIClient;
import com.juaracoding.weatherbaselocation.service.APIInterfacesRest;
import com.robin.locationgetter.EasyLocation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView txtKota, txtSuhu, txtSunset, txtSunrise, txtDescription, txtTanggal;
    ImageView imgCuaca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtKota = findViewById(R.id.txtKota);
        txtSuhu = findViewById(R.id.txtLongitude);
        txtSunrise = findViewById(R.id.txtSunrise);
        txtSunset = findViewById(R.id.txtSunset);
        imgCuaca = findViewById(R.id.imgCuaca);
        txtDescription = findViewById(R.id.txtDescription);
        txtTanggal = findViewById(R.id.txtTanggal);


        new EasyLocation(MainActivity.this, new EasyLocation.EasyLocationCallBack() {
            @Override
            public void permissionDenied() {

            }

            @Override
            public void locationSettingFailed() {

            }

            @Override
            public void getLocation(Location location) {

                callWeatherBasedLocation(location.getLatitude(),location.getLongitude());


            }
        });
    }

    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;

    public void callWeatherBasedLocation(Double lat, Double lon){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<WeatherModel> call3 = apiInterface.getWeatherBasedLocation(lat,lon,"6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<WeatherModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                progressDialog.dismiss();
                WeatherModel dataWeather = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (dataWeather !=null) {
                    txtKota.setText(dataWeather.getName()+ ", " + dataWeather.getSys().getCountry());
                    txtSuhu.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp() - 273.15) + " °C");
                    txtDescription.setText(dataWeather.getWeather().get(0).getDescription());
//                  get date
                    Date currentDate = Calendar.getInstance().getTime();
                    txtTanggal.setText(new SimpleDateFormat("hh:mm MMM dd",Locale.getDefault()).format(new Date()));

                    String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                    Picasso.get().load(image).into(imgCuaca);

                    txtSunrise.setText(new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date(dataWeather.getSys().getSunrise() * 1000 * 1000 * 1 )));
                    txtSunset.setText(new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date(dataWeather.getSys().getSunset() * 1000 * 1000 *100 * 100 * 100 * 100 * 100 *99)));

//                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm",Locale.getDefault());
//                    String currentTime = sdf.format(dataWeather.getDt());



//                    Date currentTime = Calendar.getInstance().getTime();

//                    txtWind.setText(dataWeather.getWeather().get(0).getDescription() + " , " + dataWeather.getWind().getSpeed().toString() + " m/s");
//                    txtClouds.setText(dataWeather.getWeather().get(0).getMain());
////                    txtSunrise.setText(dataWeather.getName());
////                    Calendar calendar = Calendar.getInstance();
////                    txtTanggal.setText(new SimpleDateFormat("dd mm yyyy", Locale.ENGLISH));
//                    txtPreasure.setText(dataWeather.getMain().getPressure() + " hpa");
//                    txtHumadity.setText(dataWeather.getMain().getHumidity() + " %");
//                    txtSunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunset() * 1000 * (60 * 60 * 7 ) )));
//                    txtSunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunrise() * 1000 * (60 * 60 * 7))));
//                    txtGeoCoords.setText("[ " +dataWeather.getCoord().getLat().toString() + " , " + dataWeather.getCoord().getLon().toString() + " ]");
//                    txtSuhu.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp()-273.15) + "°C");









                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }
}
