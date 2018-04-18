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

package nz.bradcampbell.kotlinexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
  var state = State(0, Date())
  val dateFormat = SimpleDateFormat("HH:mm")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    savedInstanceState?.let {
      state = it.getParcelable("state")
    }

    val toolbar:Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    val plusButton: Button = findViewById(R.id.add_button)
    plusButton.setOnClickListener {
      state = state.copy(count = state.count + 1, modificationDate = Date(), lastAction = Increment)
      updateText()
    }

    val subtractButton:Button = findViewById(R.id.subtract_button)
    subtractButton.setOnClickListener {
      state = state.copy(count = state.count - 1, modificationDate = Date(), lastAction = Decrement)
      updateText()
    }

    updateText()
  }

  fun updateText() {
      findViewById<TextView>(R.id.counter)
              .text = resources.getString(
              R.string.counter_text,
              state.count,
              dateFormat.format(state.modificationDate))

      findViewById<TextView>(R.id.status)
              .text = resources.getString(
              R.string.last_action_text,
              state.lastAction.name)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable("state", state)
  }
}
