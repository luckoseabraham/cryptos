package edu.utexas.cryptos.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utexas.cryptos.MainViewModel
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
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "onViewCreated")


        val rv = binding.assetListRV
        rv.itemAnimator = null
        val rvAdapter = AssetMetaAdapter(viewModel, false)
        rv.adapter = rvAdapter
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.layoutManager = LinearLayoutManager(binding.root.context)

        viewModel.observeSearchAssets().observe(viewLifecycleOwner) {
            rvAdapter.submitList(it)
            rvAdapter.notifyDataSetChanged()
        }
        viewModel.setSearchTerm(viewModel.getSearchTerm())
        viewModel.observeFavoriteAssets().observe(viewLifecycleOwner) {
            rvAdapter.notifyDataSetChanged()
        }
        viewModel.observeUserConfig().observe(viewLifecycleOwner) {
            rvAdapter.notifyDataSetChanged()
        }
        // Add to menu
    }
}