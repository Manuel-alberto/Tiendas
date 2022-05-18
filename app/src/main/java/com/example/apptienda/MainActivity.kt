package com.example.apptienda

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apptienda.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux{

    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mAdapter:StoreAdapter
    private lateinit var gridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener{
            val store=StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread{
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)

        }*/


        mBinding.fab.setOnClickListener{
            launchEditFragment()
        }

        setupRecyclerview()
    }

    private fun launchEditFragment(args : Bundle?=null) {
        val fragment=EditStoreFragment()
        if(args!=null) fragment.arguments = args
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
    }

    private fun setupRecyclerview() {
        mAdapter= StoreAdapter(mutableListOf(), this)
        gridLayout=GridLayoutManager(this, resources.getInteger(R.integer.main_colums))
        getStores()

        mBinding.recyclerViwe.apply {
            setHasFixedSize(true)
            layoutManager=gridLayout
            adapter=mAdapter
        }
    }

    private fun getStores(){
        doAsync {
            val stores=StoreApplication.database.storeDao().getAllStore()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    //INTERFACE ONCLICKLISTENER
    override fun onClick(storeId : Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }


    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFasvorite = !storeEntity.isFasvorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_option_title)
            .setItems(items, { dialogInterface, i ->
                when (i){
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWeb(storeEntity.website)
                }
            })
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm, { dialogInterface, i ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.Delete(storeEntity)
                    }
                }
            })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply{
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWeb(url: String){
        if(!url.isEmpty()){
            val websiteInent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startIntent(websiteInent)

        }else{
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
    }

    //MainAux
    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    //mainAux añadir store a room
    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    //mainAux Actualizar store en room
    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}