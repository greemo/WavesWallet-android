package com.wavesplatform.wallet.v2.ui.home.profile.addresses

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.ui.auth.choose_account.ChooseAccountActivity.Companion.REQUEST_EDIT_ACCOUNT_NAME
import com.wavesplatform.wallet.v2.ui.auth.choose_account.ChooseAccountActivity.Companion.REQUEST_ENTER_PASSCODE
import com.wavesplatform.wallet.v2.ui.auth.fingerprint.FingerprintAuthenticationDialogFragment
import com.wavesplatform.wallet.v2.ui.auth.passcode.enter.EnterPasscodeActivity
import com.wavesplatform.wallet.v2.ui.base.view.BaseActivity
import com.wavesplatform.wallet.v2.ui.home.MainActivity
import com.wavesplatform.wallet.v2.ui.home.profile.address_book.AddressTestObject
import com.wavesplatform.wallet.v2.ui.home.profile.addresses.adapter.AliasesAdapter
import com.wavesplatform.wallet.v2.util.copyToClipboard
import com.wavesplatform.wallet.v2.util.launchActivity
import com.wavesplatform.wallet.v2.util.notNull
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import kotlinx.android.synthetic.main.activity_profile_addresses_and_keys.*
import pers.victor.ext.click
import pers.victor.ext.gone
import pers.victor.ext.visiable
import pyxis.uzuki.live.richutilskt.utils.runDelayed
import pyxis.uzuki.live.richutilskt.utils.toast
import javax.inject.Inject

class AddressesAndKeysActivity : BaseActivity(), ProfileAddressesView, BaseFingerprint.FingerprintIdentifyListener  {

    @Inject
    @InjectPresenter
    lateinit var presenter: ProfileAddressesPresenter

    private lateinit var mFingerprintIdentify: FingerprintIdentify
    private lateinit var mFingerprintDialog: FingerprintAuthenticationDialogFragment

    @ProvidePresenter
    fun providePresenter(): ProfileAddressesPresenter = presenter

    @Inject
    lateinit var adapter: AliasesAdapter

    override fun configLayoutRes(): Int = R.layout.activity_profile_addresses_and_keys

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupToolbar(toolbar_view, View.OnClickListener { onBackPressed() }, true, getString(R.string.addresses_and_keys_toolbar_title), R.drawable.ic_toolbar_back_black)

        mFingerprintIdentify = FingerprintIdentify(this)
        mFingerprintDialog = FingerprintAuthenticationDialogFragment()
        mFingerprintDialog.setFingerPrintDialogListener(object : FingerprintAuthenticationDialogFragment.FingerPrintDialogListener{
            override fun onPinCodeButtonClicked(dialog: Dialog, button: AppCompatTextView) {
                super.onPinCodeButtonClicked(dialog, button)
                mFingerprintIdentify.cancelIdentify()
                launchActivity<EnterPasscodeActivity>(requestCode = REQUEST_ENTER_PASSCODE) {  }
            }

            override fun onCancelButtonClicked(dialog: Dialog, button: AppCompatTextView) {
                super.onCancelButtonClicked(dialog, button)
                mFingerprintIdentify.cancelIdentify()
            }
        })

        relative_alias.click {
            val bottomSheetFragment = AddressesAndKeysBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        image_address_copy.click{
            text_address.copyToClipboard(it)
        }

        image_public_key_copy.click {
            text_public_key.copyToClipboard(it)
        }

        image_private_key_copy.click {
            text_private_key.copyToClipboard(it)
        }

        button_show.click {
            if (mFingerprintIdentify.isFingerprintEnable){
                mFingerprintDialog.isCancelable = false;
                mFingerprintDialog.show(fragmentManager, "fingerprintDialog");

                mFingerprintIdentify.startIdentify(EnterPasscodeActivity.MAX_AVAILABLE_TIMES, this@AddressesAndKeysActivity);
            }else{
                launchActivity<EnterPasscodeActivity>(requestCode = REQUEST_ENTER_PASSCODE) {  }
            }
        }
    }

    override fun onSucceed() {
        mFingerprintDialog.onSuccessRecognizedFingerprint()
        runDelayed(1500, {
            mFingerprintDialog.dismiss()
            mFingerprintIdentify.cancelIdentify()
            button_show.gone()
            relative_private_key_block.visiable()
        })
    }

    override fun onFailed(isDeviceLocked: Boolean) {
        if (isDeviceLocked) mFingerprintDialog.onFingerprintLocked()
    }

    override fun onNotMatch(availableTimes: Int) {
        mFingerprintDialog.onFingerprintDoNotMatchTryAgain()
    }

    override fun onStartFailedByDeviceLocked() {
        mFingerprintDialog.onFingerprintLocked();
    }

    override fun onPause() {
        super.onPause()
        mFingerprintIdentify.cancelIdentify()
    }

    override fun onDestroy() {
        super.onDestroy()
        mFingerprintIdentify.cancelIdentify()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENTER_PASSCODE -> {
                button_show.gone()
                relative_private_key_block.visiable()
            }
        }
    }
}