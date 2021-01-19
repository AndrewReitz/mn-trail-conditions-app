package cash.andrew.mntrailconditions.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import cash.andrew.kotlin.common.retry
import cash.andrew.mntrailconditions.R
import cash.andrew.mntrailconditions.databinding.MarkdownActivityBinding
import cash.andrew.mntrailconditions.util.ComponentContainer
import cash.andrew.mntrailconditions.util.makeComponent
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.noties.markwon.Markwon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TITLE_RES_KEY = "TitleRes"
private const val MARKDOWN_URL_KEY = "MarkdownFile"

class MarkdownActivity : AppCompatActivity(), ComponentContainer<ActivityComponent> {

    @Inject lateinit var markwon: Markwon
    @Inject lateinit var client: HttpClient

    override val component by lazy { makeComponent() }

    private lateinit var binding: MarkdownActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component.inject(this)

        val markDownUrl = requireNotNull(intent.getStringExtra(MARKDOWN_URL_KEY))
        val title = getString(intent.getIntExtra(TITLE_RES_KEY, 0))

        binding = MarkdownActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorOnPrimarySurface, typedValue, true)
        val arrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back_white_24dp, theme)
        requireNotNull(arrow).setTint(typedValue.data)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(arrow)
        supportActionBar?.title = title

        MainScope().launch {
            runCatching {
                val text: String = retry {
                    client.get(markDownUrl)
                }
                markwon.setMarkdown(binding.markdownText, text)
            }.onFailure {
                binding.markdownText.text = getString(R.string.markdown_error)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

enum class MarkDownFile(val url: String) {
    ABOUT("https://morc-trail-conditions.firebaseapp.com/about.md"),
    WHY_ARE_TRAILS_CLOSED("https://morc-trail-conditions.firebaseapp.com/why-are-trails-closed.md")
}

fun Activity.navigateToMarkDownActivity(@StringRes titleRes: Int, markDownFile: MarkDownFile) {
    val intent = Intent(this, MarkdownActivity::class.java)
    intent.putExtra(TITLE_RES_KEY, titleRes)
    intent.putExtra(MARKDOWN_URL_KEY, markDownFile.url)
    startActivity(intent)
}
