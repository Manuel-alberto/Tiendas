package com.example.apptienda.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apptienda.R
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.databinding.ItemStoreBinding

class StoreListAdapter(private var listener: OnClickListener) :
    ListAdapter<StoreEntity, RecyclerView.ViewHolder>(StoreDiffCallback()) {

    private lateinit var mContext:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext= parent.context
        val view= LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store = getItem(position)
        with(holder as ViewHolder){
            setListener(store)
            binding.tvName.text=store.name
            binding.chFavorite.isChecked=store.isFasvorite
            Glide.with(mContext).load(store.photoUrl).centerCrop().into(binding.imgphoto)
        }
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val binding=ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity){
            with(binding.root){
                setOnClickListener { listener.onClick(storeEntity) }
                setOnLongClickListener{
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }
            binding.chFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }

    class StoreDiffCallback : DiffUtil.ItemCallback<StoreEntity>(){
        override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem == newItem
        }

    }

}