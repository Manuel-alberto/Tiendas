package com.example.apptienda.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apptienda.*
import com.example.apptienda.common.utils.MainAux
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.databinding.ActivityMainBinding
import com.example.apptienda.editModule.EditStoreFragment
import com.example.apptienda.editModule.viewModel.EditStoreViewModel
import com.example.apptienda.mainModule.adapter.OnClickListener
import com.example.apptienda.mainModule.adapter.StoreAdapter
import com.example.apptienda.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var gridLayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener{
            launchEditFragment()
        }

        setupRecyclerview()

        setupViewModel()

    }

    private fun setupViewModel(){
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this,) { stores ->
            mAdapter.setStores(stores)
        }
        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this) { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }
        mEditStoreViewModel.getStoreSelected().observe(this){storeEntity ->
            mAdapter.add(storeEntity)
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {

        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment= EditStoreFragment()

        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun setupRecyclerview() {
        mAdapter= StoreAdapter(mutableListOf(), this)
        gridLayout=GridLayoutManager(this, resources.getInteger(R.integer.main_colums))
        //getStores()

        mBinding.recyclerViwe.apply {
            setHasFixedSize(true)
            layoutManager=gridLayout
            adapter=mAdapter
        }
    }

    //INTERFACE ONCLICKLISTENER
    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }


    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_option_title)
            .setItems(items) { dialogInterface, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWeb(storeEntity.website)
                }
            }
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { dialogInterface, i ->
                mMainViewModel.deleteStore(storeEntity)
            }
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

}