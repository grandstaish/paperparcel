package nz.bradcampbell.java7example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  private State state = new State(0);
  private final DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      state = savedInstanceState.getParcelable("state");
    }

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    View plusButton = findViewById(R.id.add_button);
    plusButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        state = new State(state.getCount() + 1);
        updateText();
      }
    });

    View subtractButton = findViewById(R.id.subtract_button);
    subtractButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        state = new State(state.getCount() - 1);
        updateText();
      }
    });

    updateText();
  }

  private void updateText() {
    state.modificationDate = new Date();
    TextView counter = (TextView) findViewById(R.id.counter);
    counter.setText(
        state.getCount() + " (updated at " + dateFormat.format(state.modificationDate) + ")");
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable("state", state);
  }
}
