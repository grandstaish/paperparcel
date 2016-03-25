package nz.bradcampbell.mixedexample;

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
  private static final String STATE_KEY = "state";

  private State state = State.create(new Test(0), new Date());
  private DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      state = savedInstanceState.getParcelable(STATE_KEY);
    }

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    View plusButton = findViewById(R.id.add_button);
    plusButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        state = State.create(state.count().copy(state.count().getI() + 1), new Date());
        updateText();
      }
    });

    View subtractButton = findViewById(R.id.subtract_button);
    subtractButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        state = State.create(state.count().copy(state.count().getI() - 1), new Date());
        updateText();
      }
    });

    updateText();
  }

  private void updateText() {
    TextView counter = (TextView) findViewById(R.id.counter);
    counter.setText(state.count() + " (updated at " + dateFormat.format(state.modificationDate()) + ")");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(STATE_KEY, state);
  }
}
