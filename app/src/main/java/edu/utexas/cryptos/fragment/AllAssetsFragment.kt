package edu.utexas.cryptos.fragment

import android.R.attr.data
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utexas.cryptos.MainViewModel
import edu.utexas.cryptos.R
import edu.utexas.cryptos.adapter.AssetMetaAdapter
import edu.utexas.cryptos.databinding.FragmentRvBinding


class AllAssetsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): AllAssetsFragment {
            return AllAssetsFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate( inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_rv, container, false);
//        _binding = FragmentRvBinding.inflate(inflater,container,true)
//        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "onViewCreated")
        // XXX Write me
        // Setting itemAnimator = null on your recycler view might get rid of an annoying
        // flicker

        val rv = binding.assetListRV
        rv.itemAnimator = null
        val rvAdapter = AssetMetaAdapter(viewModel, false)
        rv.adapter = rvAdapter
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.layoutManager = LinearLayoutManager(binding.root.context)
        rvAdapter.submitList(viewModel.observeAssets().value)
//        viewModel.observeAssets().observe(viewLifecycleOwner) {
//            rvAdapter.submitList(it)
//        }

        viewModel.observeSearchAssets().observe(viewLifecycleOwner) {
            rvAdapter.submitList(it)
        }
        viewModel.observeFavoriteAssets().observe(viewLifecycleOwner) {
            rvAdapter.notifyDataSetChanged()
        }
        viewModel.observeUserConfig().observe(viewLifecycleOwner) {
            rvAdapter.notifyDataSetChanged()
        }
        // Add to menu
    }

    override fun onDestroyView() {
        // XXX Write me
        // Don't let back to home button stay when we exit favorites
        super.onDestroyView()
    }

}