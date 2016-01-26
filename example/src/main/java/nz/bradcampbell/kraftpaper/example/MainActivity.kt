package nz.bradcampbell.kraftpaper.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var state = State(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val plusButton = findViewById(R.id.add_button)
        plusButton.setOnClickListener {
            state = state.copy(state.count + 1)
            updateText()
        }

        val subtractButton = findViewById(R.id.subtract_button)
        subtractButton.setOnClickListener {
            state = state.copy(state.count - 1)
            updateText()
        }

        updateText()
    }

    fun updateText() {
        val text = findViewById(R.id.counter) as TextView
        text.text = state.count.toString()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
//        outState?.putParcelable("state", StateParcel.wrap(state))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)


    }
}
