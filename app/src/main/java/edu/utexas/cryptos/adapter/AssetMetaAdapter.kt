package edu.utexas.cryptos.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utexas.cryptos.MainViewModel
import edu.utexas.cryptos.R
import edu.utexas.cryptos.databinding.RowAssetBinding
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.Currency


class AssetMetaAdapter(private val viewModel: MainViewModel, private val isFavorite : Boolean)
    : ListAdapter<Asset, AssetMetaAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.id == newItem.id
                    && oldItem.change_1h == newItem.change_1h
                    && oldItem.change_7d == newItem.change_7d
                    && oldItem.change_24h == newItem.change_24h
                    && oldItem.updated_at == newItem.updated_at
        }
    }

    inner class VH(private val rowBinding: RowAssetBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            Log.d("LUKE", "Bind invoked for asset id ${position}")
            var asset : Asset
            if(isFavorite){
                asset = viewModel.getFavoriteAt(position)
            } else {
                asset = viewModel.getAssetAt(position)
            }
            holder.rowBinding.name.text = asset.id + " - " + asset.name

            var price = asset.price.toFloat()
            if(viewModel.observeUserConfig().value?.currency != null ) {
                Log.d("LUKE", "Obtained currency update.")
                val curr = viewModel.observeUserConfig().value?.currency!!
                price = asset.quote.get(Currency.valueOf(curr))?.price?.toFloat()!!
            }
            holder.rowBinding.price.text = String.format("%.5f", price)
            Log.d("LUKE", "Bind invoked for asset id ${asset.id}")
            if(viewModel.observeUserConfig().value?.favorites != null && viewModel.observeUserConfig().value?.favorites!!.contains(asset.id)){
                holder.rowBinding.actionBut.setImageResource(R.drawable.ic_delete)
                holder.rowBinding.actionBut.setOnClickListener{
                    viewModel.removeFavorite(asset.id)
                }
            } else {
                holder.rowBinding.actionBut.setImageResource(R.drawable.ic_favorite)
                holder.rowBinding.actionBut.setOnClickListener{
                    viewModel.setFavorite(asset.id)
                }
            }

            holder.rowBinding.name.setOnClickListener{
                MainViewModel.doDetails(itemView.context, asset)
            }
            holder.rowBinding.price.setOnClickListener{
                MainViewModel.doDetails(itemView.context, asset)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RowAssetBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}