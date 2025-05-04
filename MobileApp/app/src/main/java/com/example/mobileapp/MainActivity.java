package com.example.mobileapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private PieChart humidityPieChart;
    private Timer timer;
    private SeekBar humiditySeekBar;
    private TextView desiredHumidityLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        humidityPieChart = findViewById(R.id.humidityPieChart);
        humiditySeekBar = findViewById(R.id.humiditySeekBar);
        desiredHumidityLabel = findViewById(R.id.desiredHumidityLabel);
        setupPieChart();
        setupSeekBar();
        fetchDesiredHumidity();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
    public class HumidityResponse {
        private int humidity;

        public int getHumidity() {
            return humidity;
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GetCurrentHumidity();
            }
        }, 0, 3000);
    }
    private void setupSeekBar() {
        humiditySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredHumidityLabel.setText("Заданная влажность: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendChangeHumidityRequest(String.valueOf(seekBar.getProgress()));
            }
        });
    }
    private void setupPieChart() {
        humidityPieChart.getDescription().setEnabled(false);
        humidityPieChart.setRotationEnabled(false);
        humidityPieChart.setNoDataText("Синхронизация...");
        humidityPieChart.setRotationAngle(90f);
    }

    private void updatePieChart(int humidityValue) {
        int remainingHumidity = 100 - humidityValue;
        PieDataSet dataSet = new PieDataSet(getPieEntries(humidityValue, remainingHumidity), "");
        dataSet.setColors(new int[]{Color.parseColor("#3498db"), Color.parseColor("#e74c3c")});
        dataSet.setValueTextSize(18f);
        PieData data = new PieData(dataSet);
        humidityPieChart.setData(data);
        humidityPieChart.setCenterTextSize(25f);
        humidityPieChart.getLegend().setEnabled(false);
        dataSet.setDrawValues(false);
        humidityPieChart.setCenterText("Влажность: \n" + humidityValue + "%");
        humidityPieChart.invalidate();
    }

    private List<PieEntry> getPieEntries(int humidityValue, int remainingHumidity) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(humidityValue, ""));
        entries.add(new PieEntry(remainingHumidity, ""));
        return entries;
    }

    private void fetchDesiredHumidity() {
        String url = "http://26.102.70.137:5000/getDesiredHumidity/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    HumidityResponse desiredHumidityResponse = gson.fromJson(responseBody, HumidityResponse.class);
                    final int desiredHumidity = desiredHumidityResponse.getHumidity();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            humiditySeekBar.setProgress(desiredHumidity);
                            desiredHumidityLabel.setText("Заданная влажность: " + desiredHumidity + "%");
                        }
                    });
                }
            }
        });
    }

    private void sendChangeHumidityRequest(String desiredHumidity) {
        String url = "http://26.102.70.137:5000/sendDesiredHumidity/" + desiredHumidity;
        RequestBody requestBody = new FormBody.Builder()
                .add("humidity", desiredHumidity)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private void GetCurrentHumidity() {
        String url = "http://26.102.70.137:5000/getCurrentHumidity/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    if (!responseBody.isEmpty()) {
                        Gson gson = new Gson();
                        HumidityResponse humidityResponse = gson.fromJson(responseBody, HumidityResponse.class);
                        final int humidityValue = humidityResponse.getHumidity();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updatePieChart(humidityValue);
                            }
                        });
                    }
                }
            }
        });
    }
}


