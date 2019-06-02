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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
  private var state = State(0, Date())
  private val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    savedInstanceState?.let {
      state = it.getParcelable("state")
    }

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    val plusButton = findViewById<View>(R.id.add_button)
    plusButton.setOnClickListener {
      state = state.copy(count = state.count + 1, modificationDate =  Date())
      updateText()
    }

    val subtractButton = findViewById<View>(R.id.subtract_button)
    subtractButton.setOnClickListener {
      state = state.copy(count = state.count - 1, modificationDate = Date())
      updateText()
    }

    updateText()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable("state", state)
  }

  @SuppressLint("SetTextI18n")
  private fun updateText() {
    val counter = findViewById<TextView>(R.id.counter)
    counter.text = "${state.count} (updated at ${dateFormat.format(state.modificationDate)})"
  }
}
