package com.example.apptienda

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.apptienda.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    private lateinit var mBinding:FragmentEditStoreBinding
    private var mActivity : MainActivity?=null
    private var mIsEditMode:Boolean=false
    private var mStoreEntity:StoreEntity?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding= FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)

        if(id!=null && id!=0L) {
            mIsEditMode=true
            getSrote(id)
        }else{
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", website = "", photoUrl = "")
        }

        setupActionBar()

        setupTextFields()

    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title= if(!mIsEditMode) getString(R.string.edit_store_title_add)
                                            else getString(R.string.edit_store_title_edit)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {

        with(mBinding){
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhoto)
                loadImage(it.toString().trim())
            }
        }

    }

    private fun loadImage(url : String){
        Glide.with(this).
        load(url).
        diskCacheStrategy(DiskCacheStrategy.ALL).
        centerCrop().
        into(mBinding.imgPhoto)
    }

    private fun getSrote(id: Long) {
        doAsync {
            mStoreEntity=StoreApplication.database.storeDao().getStoreById(id)
            uiThread {
                if(mStoreEntity!=null) setUIStore(mStoreEntity!!)
            }
        }
    }

    private fun setUIStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable():Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                hideKeyboard()
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save->{
                if (mStoreEntity != null && validateFields(mBinding.tilPhoto, mBinding.tilPhone, mBinding.tilName)){
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etPhotoUrl.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }
                    doAsync {
                        if(mIsEditMode)StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)

                        uiThread {
                            hideKeyboard()
                            if (mIsEditMode) {
                                mActivity?.updateStore(mStoreEntity!!)
                                Snackbar.make(mBinding.root, R.string.store_message_update_success, Snackbar.LENGTH_SHORT).show()
                            }else{
                                mActivity?.addStore(mStoreEntity!!)
                                hideKeyboard()
                                Toast.makeText(mActivity, getString(R.string.edit_store_message_success), Toast.LENGTH_SHORT).show()
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textfields : TextInputLayout) : Boolean{
        var isValid=true
        for (textField in textfields){
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.herlper_require)
                isValid=false
            }else{
                textField.error = null
            }
            if(!isValid) Snackbar.make(mBinding.root, R.string.store_message_valid, Snackbar.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid=true
        if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()) {
            mBinding.tilPhoto.error=getString(R.string.herlper_require)
            mBinding.etPhotoUrl.requestFocus()
            isValid=false
        }
        if (mBinding.etPhone.text.toString().trim().isEmpty()) {
            mBinding.tilPhone.error=getString(R.string.herlper_require)
            mBinding.etPhone.requestFocus()
            isValid=false
        }
        if(mBinding.etName.text.toString().trim().isEmpty()) {
            mBinding.tilName.error=getString(R.string.herlper_require)
            mBinding.etName.requestFocus()
            isValid=false
        }
        return isValid
    }

    private fun hideKeyboard(){
        val imm=mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title=getString(R.string.app_name)
        setHasOptionsMenu(false)
        mActivity?.hideFab(true)
        super.onDestroy()
    }

}