package edu.utexas.cryptos
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import edu.utexas.cryptos.databinding.ActivityMainBinding
import edu.utexas.cryptos.fragment.AllAssetsFragment
import edu.utexas.cryptos.fragment.FavoriteFragment
import edu.utexas.cryptos.model.Currency
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "sign in successful")
            viewModel.updateUser()
            viewModel.fetchAssets()
            //Check if the user has config record.
            //if no, default it.
            //Then call the fragment for showing favorites

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.signOut()
                viewModel.login(signInLauncher)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel.login(signInLauncher)

        GlobalScope.launch {
            while (coroutineContext.isActive) {
                delay(10000L)
                viewModel.fetchAssets()
            }
        }

        val spinnerArray: MutableList<String> = ArrayList()
        enumValues<Currency>().forEach {
            spinnerArray.add(it.name)
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner : Spinner = binding.contentMain.currency
        spinner.adapter = adapter

        viewModel.observeUserConfig().observe(this) {
            spinner.setSelection(adapter.getPosition(it.currency))
            viewModel.fetchAssets()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val curr = adapter.getItem(position)
                Log.d("LUKE", "On item selected invoked. $curr")
                if(viewModel.observeUserConfig().value != null && curr != viewModel.observeUserConfig().value?.currency) {
                    //update currency in firebase
                    Log.d("LUKE", "inside if condition. $curr")
                    viewModel.updateCurrencyPref(curr!!)
                    //TODO also refresh the prices in the views. But probably inside the fragments.
                }
            }
        }


        // No back stack for home
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_frame, FavoriteFragment.newInstance(), "mainFragTag")
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }

        binding.contentMain.tabLayout.addOnTabSelectedListener(
            object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    //do stuff here
//                    tab.position
                    Log.d("LUKE", "Tab selected is ${tab.position}")
                    if(tab.position == 0 ) {
//                        //select favorites fragment.
                            supportFragmentManager.commit {
                                replace(R.id.main_frame, FavoriteFragment.newInstance(), "mainFragTag")
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            }
                    } else {
                        //select all assets fragment.
                        supportFragmentManager.commit {
                            replace(R.id.main_frame, AllAssetsFragment.newInstance(), "mainFragTag")
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            }
        )


        binding.contentMain.search.doOnTextChanged { text, _, _, _ ->
            if (text!!.isEmpty()) hideKeyboard()
            viewModel.setSearchTerm(text.toString())
        }


    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }
}