package com.example.apptienda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apptienda.databinding.ItemStoreBinding

class StoreAdapter(private var stores:MutableList<StoreEntity>, private var listener: OnClickListener) :
    RecyclerView.Adapter<StoreAdapter.viewHolder>() {

    private lateinit var mContext:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        mContext= parent.context
        val view= LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val store=stores.get(position)
        with(holder){
            setListener(store)
            binding.tvName.text=store.name
            binding.chFavorite.isChecked=store.isFasvorite
            Glide.with(mContext).load(store.photoUrl).centerCrop().into(binding.imgphoto)
        }
    }

    override fun getItemCount(): Int = stores.size

    fun add(storeEntity: StoreEntity) {
        if(!stores.contains(storeEntity)){
            stores.add(storeEntity)
            notifyItemInserted(stores.size-1)
        }
    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores=stores
        notifyDataSetChanged()
    }

    fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if(index!=-1){
            stores.set(index, storeEntity)
            notifyItemChanged(index)
        }
    }

    fun Delete(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if(index!=-1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class viewHolder(view: View):RecyclerView.ViewHolder(view){
        val binding=ItemStoreBinding.bind(view)

        fun setListener(storeEntity:StoreEntity){
            with(binding.root){
                setOnClickListener { listener.onClick(storeEntity.id) }
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

}