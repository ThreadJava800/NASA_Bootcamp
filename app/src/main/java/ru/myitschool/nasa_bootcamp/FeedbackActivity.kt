package ru.myitschool.nasa_bootcamp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import io.sentry.UserFeedback

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        findViewById<Button>(R.id.send_feedback).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_input).text.toString()
            val name = findViewById<EditText>(R.id.name_input).text.toString()
            val comment = findViewById<EditText>(R.id.comment_input).text.toString()
            sendFeedback(email, name, comment)
        }
    }

    private fun sendFeedback(email: String, name: String, comment: String) {
        val sentry = Sentry.captureMessage("Из формы.")
        val userFeedback = UserFeedback(sentry)
        userFeedback.email = email
        userFeedback.name = name
        userFeedback.comments = comment
        Sentry.captureUserFeedback(userFeedback)
    }
}