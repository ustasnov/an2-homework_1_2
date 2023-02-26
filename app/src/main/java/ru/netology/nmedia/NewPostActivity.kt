package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent?.getStringExtra(Intent.EXTRA_TEXT)
        if (content == null) {
            binding.buttonOk.setImageResource(R.drawable.ic_add_24)
        } else {
            binding.buttonOk.setImageResource(R.drawable.ic_check_24)
        }

        binding.content.setText(content)
        binding.buttonOk.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_TEXT, text) })
            }
            finish()
        }

        binding.buttonCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    object NewPostContract : ActivityResultContract<String?, String?>() {
        override fun createIntent(context: Context, input: String?) =
            Intent(context, NewPostActivity::class.java).apply {
                putExtra(Intent.EXTRA_TEXT, input)
            }

        override fun parseResult(resultCode: Int, intent: Intent?) =
            intent?.getStringExtra(Intent.EXTRA_TEXT)
    }
}