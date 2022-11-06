package edu.utexas.cryptos

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import edu.utexas.cryptos.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    companion object {
        const val titleKey = "titleKey"
        const val descKey = "descKey"
        const val imageURLKey = "imageURLKey"
        const val thumbnailURLKey = "thumbnailURLKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentExtras = intent.extras
        val title = parentExtras?.getCharSequence(titleKey).toString()
        val desc = parentExtras?.getCharSequence(descKey).toString()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        setContentView(binding.root)

        binding.desc.text = desc
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == android.R.id.home) {
            // If user clicks "up", then it it as if they clicked OK.  We commit
            // changes and return to parent
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            this.finish()
            return true
        } else super.onOptionsItemSelected(item)
    }
}