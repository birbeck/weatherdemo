package com.birbeck.android.weatherdemo.ui.main

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.birbeck.android.weatherdemo.R
import com.birbeck.android.weatherdemo.databinding.FragmentMainBinding

class MainActivityFragment : Fragment() {

    private lateinit var mDataBinding: FragmentMainBinding
    private lateinit var mViewModel: MainActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        mDataBinding.setLifecycleOwner(this)
        mDataBinding.viewModel = mViewModel

        return mDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PackageManager.PERMISSION_GRANTED !=
                activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            mViewModel.setViewLoaded(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mViewModel.setLocationPermissionGranted()
                }
                mViewModel.setViewLoaded(true)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
