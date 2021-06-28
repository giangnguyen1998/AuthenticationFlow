package edu.nuce.apps.newflowtypes.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import edu.nuce.apps.newflowtypes.R
import edu.nuce.apps.newflowtypes.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.isLoading.collect {
                        binding.loading.isVisible = it
                    }
                }

                launch {
                    mainViewModel.navigateActions.collect {
                        when (it) {
                            MainNavigateActions.NavigateToLogin -> {
                                binding.run {
                                    btnSignOut.isVisible = false
                                    btnSignIn.isVisible = true
                                    layoutData.isVisible = false
                                }
                            }
                            MainNavigateActions.NavigateToHome -> {
                                binding.run {
                                    btnSignOut.isVisible = true
                                    btnSignIn.isVisible = false
                                    layoutData.isVisible = true
                                }
                            }
                        }
                    }
                }

                launch {
                    mainViewModel.userInfo.collect {
                        binding.run {
                            id.text = it?.getUid()
                            fullName.text = it?.getDisplayName()
                            email.text = it?.getEmail()
                            phoneNumber.text = it?.getPhoneNumber()
                            Glide.with(avatar)
                                .load(it?.getPhotoUrl())
                                .into(avatar)
                        }
                    }
                }

                launch {
                    mainViewModel.errorMessage.collect {
                        Timber.e(it)
                    }
                }

                launch {
                    mainViewModel.networkState.collect {
                        Timber.e(it.toString())
                    }
                }
            }
        }
    }
}