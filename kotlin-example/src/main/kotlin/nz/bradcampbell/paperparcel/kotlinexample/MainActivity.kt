package nz.bradcampbell.paperparcel.kotlinexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import nz.bradcampbell.paperparcel.generated.PaperParcels
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var state = State(0, Date())
    val dateFormat = SimpleDateFormat("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<StateParcel>("state").contents
            val state2parcel = savedInstanceState.getParcelable<StateParcel>("state_2")
            val state2 : State = PaperParcels.unwrap(state2parcel);
            if (!state2.equals(state)) {
                throw IllegalStateException("Got different object back from PaperParcels!")
            }
        }

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val plusButton = findViewById(R.id.add_button)
        plusButton.setOnClickListener {
            state = state.copy(state.count + 1, Date())
            updateText()
        }

        val subtractButton = findViewById(R.id.subtract_button)
        subtractButton.setOnClickListener {
            state = state.copy(state.count - 1, Date())
            updateText()
        }

        updateText()
    }

    fun updateText() {
        val counter = findViewById(R.id.counter) as TextView
        counter.text = state.count.toString() + " (updated at " + dateFormat.format(state.modificationDate) + ")"
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("state", StateParcel.wrap(state))
        outState?.putParcelable("state_2", PaperParcels.wrap(state))
    }
}
