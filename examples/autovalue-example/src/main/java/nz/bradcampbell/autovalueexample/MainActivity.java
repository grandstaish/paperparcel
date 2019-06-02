/*
 * Copyright (C) 2016 Bradley Campbell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.bradcampbell.autovalueexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
  private static final String STATE_KEY = "state";

  private State state = State.create(0, new Date());
  private DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      state = savedInstanceState.getParcelable(STATE_KEY);
    }

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    View plusButton = findViewById(R.id.add_button);
    plusButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        state = State.create(state.count() + 1, new Date());
        updateText();
      }
    });

    View subtractButton = findViewById(R.id.subtract_button);
    subtractButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        state = State.create(state.count() - 1, new Date());
        updateText();
      }
    });

    updateText();
  }

  private void updateText() {
    TextView counter = (TextView) findViewById(R.id.counter);
    counter.setText(
        state.count() + " (updated at " + dateFormat.format(state.modificationDate()) + ")");
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(STATE_KEY, state);
  }
}
