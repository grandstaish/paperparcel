package nz.bradcampbell.benchmarkdemo;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import nz.bradcampbell.benchmarkdemo.model.autovalueparcel.AutoValueParcelResponse;
import nz.bradcampbell.benchmarkdemo.model.autovalueparcel.GsonAdapterFactory;
import nz.bradcampbell.benchmarkdemo.model.paperparcel.PaperParcelResponse;
import nz.bradcampbell.benchmarkdemo.model.parceler.ParcelerResponse;
import nz.bradcampbell.benchmarkdemo.model.serializable.SerializableResponse;
import nz.bradcampbell.benchmarkdemo.parceltasks.AutoValueParcelTask;
import nz.bradcampbell.benchmarkdemo.parceltasks.SerializableTask;
import nz.bradcampbell.benchmarkdemo.parceltasks.PaperParcelTask;
import nz.bradcampbell.benchmarkdemo.parceltasks.ParcelResult;
import nz.bradcampbell.benchmarkdemo.parceltasks.ParcelTask;
import nz.bradcampbell.benchmarkdemo.parceltasks.ParcelerTask;
import nz.bradcampbell.benchmarkdemo.widget.BarChart;

public class MainActivity extends AppCompatActivity {
  private static final int ITERATIONS = 100;
  private static final int RESPONSES = 4;

  private BarChart barChart;

  private Gson gson;

  private List<PaperParcelResponse> paperParcelModels = new ArrayList<>(RESPONSES);
  private List<ParcelerResponse> parcelerModels = new ArrayList<>(RESPONSES);
  private List<AutoValueParcelResponse> autoValueParcelModels = new ArrayList<>(RESPONSES);
  private List<SerializableResponse> serializableModels = new ArrayList<>(RESPONSES);

  private final PaperParcelTask.ParcelListener parcelListener = new ParcelTask.ParcelListener() {
    @Override public void onComplete(ParcelTask parcelTask, ParcelResult parcelResult) {
      addBarData(parcelTask, parcelResult);
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    gson = new GsonBuilder()
        .registerTypeAdapterFactory(GsonAdapterFactory.create())
        .create();

    try {
      readJsonFromFiles();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    barChart = (BarChart)findViewById(R.id.bar_chart);
    barChart.setColumnTitles(new String[] { "Serializable", "AutoValue Parcel", "Parceler", "PaperParcel" });

    findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        performTests();
      }
    });
  }

  private void performTests() {
    barChart.clear();
    barChart.setSections(new String[] {
        "Parcel 2 items", "Parcel 7 items", "Parcel 20 items", "Parcel 60 items" });

    List<ParcelTask> parcelTasks = new ArrayList<>();
    for (int response = 0; response < RESPONSES; response++) {
      for (int iteration = 0; iteration < ITERATIONS; iteration++) {
        parcelTasks.add(new ParcelerTask(parcelListener, parcelerModels.get(response)));
        parcelTasks.add(new PaperParcelTask(parcelListener, paperParcelModels.get(response)));
        parcelTasks.add(new AutoValueParcelTask(parcelListener, autoValueParcelModels.get(response)));
        parcelTasks.add(new SerializableTask(parcelListener, serializableModels.get(response)));
      }
    }

    for (ParcelTask<?> parcelTask : parcelTasks) {
      parcelTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
  }

  private void addBarData(ParcelTask parcelTask, ParcelResult parcelResult) {
    int section;
    switch (parcelResult.objectsParcelled) {
      case 2:
        section = 0;
        break;
      case 7:
        section = 1;
        break;
      case 20:
        section = 2;
        break;
      case 60:
        section = 3;
        break;
      default:
        section = -1;
        break;
    }

    if (parcelTask instanceof PaperParcelTask) {
      barChart.addTiming(section, 3, parcelResult.runDuration / 1000f);
    } else if (parcelTask instanceof ParcelerTask) {
      barChart.addTiming(section, 2, parcelResult.runDuration / 1000f);
    } else if (parcelTask instanceof AutoValueParcelTask) {
      barChart.addTiming(section, 1, parcelResult.runDuration / 1000f);
    } else if (parcelTask instanceof SerializableTask) {
      barChart.addTiming(section, 0, parcelResult.runDuration / 1000f);
    }
  }

  private void readJsonFromFiles() throws IOException {
    String tinyJson = readFile("tinysample.json");
    String smallJson = readFile("smallsample.json");
    String mediumJson = readFile("mediumsample.json");
    String largeJson = readFile("largesample.json");

    paperParcelModels.add(gson.fromJson(tinyJson, PaperParcelResponse.class));
    paperParcelModels.add(gson.fromJson(smallJson, PaperParcelResponse.class));
    paperParcelModels.add(gson.fromJson(mediumJson, PaperParcelResponse.class));
    paperParcelModels.add(gson.fromJson(largeJson, PaperParcelResponse.class));

    parcelerModels.add(gson.fromJson(tinyJson, ParcelerResponse.class));
    parcelerModels.add(gson.fromJson(smallJson, ParcelerResponse.class));
    parcelerModels.add(gson.fromJson(mediumJson, ParcelerResponse.class));
    parcelerModels.add(gson.fromJson(largeJson, ParcelerResponse.class));

    autoValueParcelModels.add(gson.fromJson(tinyJson, AutoValueParcelResponse.class));
    autoValueParcelModels.add(gson.fromJson(smallJson, AutoValueParcelResponse.class));
    autoValueParcelModels.add(gson.fromJson(mediumJson, AutoValueParcelResponse.class));
    autoValueParcelModels.add(gson.fromJson(largeJson, AutoValueParcelResponse.class));

    serializableModels.add(gson.fromJson(tinyJson, SerializableResponse.class));
    serializableModels.add(gson.fromJson(smallJson, SerializableResponse.class));
    serializableModels.add(gson.fromJson(mediumJson, SerializableResponse.class));
    serializableModels.add(gson.fromJson(largeJson, SerializableResponse.class));
  }

  private String readFile(String filename) {
    StringBuilder sb = new StringBuilder();

    try {
      InputStream json = getAssets().open(filename);
      BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

      String str;
      while ((str = in.readLine()) != null) {
        sb.append(str);
      }

      in.close();
    } catch (Exception e) {
      new AlertDialog.Builder(this)
          .setTitle("Error")
          .setMessage(
              "The JSON file was not able to load properly. These tests won't work until "
                  + "you completely kill this demo app and restart it.")
          .setPositiveButton("OK", null)
          .show();
    }

    return sb.toString();
  }
}
