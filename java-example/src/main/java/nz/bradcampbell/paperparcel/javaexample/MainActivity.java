package nz.bradcampbell.paperparcel.javaexample;

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
import nz.bradcampbell.paperparcel.PaperParcels;

public class MainActivity extends AppCompatActivity {
    private State state = new State(0, new Date(), null);
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            StateParcel wrapped = savedInstanceState.getParcelable("state");
            if (wrapped != null) {
                state = wrapped.getContents();
            }
            StateParcel state2parcel = savedInstanceState.getParcelable("state_2");
            State state2 = PaperParcels.unwrap(state2parcel);
            if (!state2.equals(state)) {
                throw new IllegalStateException("Got different object back from PaperParcels!");
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View plusButton = findViewById(R.id.add_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = new State(state.getCount() + 1, new Date(), null);
                updateText();
            }
        });

        View subtractButton = findViewById(R.id.subtract_button);
        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = new State(state.getCount() - 1, new Date(), null);
                updateText();
            }
        });

        updateText();
    }

    private void updateText() {
        TextView counter = (TextView) findViewById(R.id.counter);
        counter.setText(state.getCount() + " (updated at " + dateFormat.format(state.customGetterMethodName()) + ")");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state", StateParcel.wrap(state));
        outState.putParcelable("state_2", PaperParcels.wrap(state));
    }
}
