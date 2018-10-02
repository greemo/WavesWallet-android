package com.wavesplatform.wallet.v2.ui.home.quick_action.receive.cryptocurrency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.data.model.remote.response.AssetBalance
import com.wavesplatform.wallet.v2.data.model.remote.response.coinomat.GetTunnel
import com.wavesplatform.wallet.v2.ui.base.view.BaseFragment
import com.wavesplatform.wallet.v2.ui.home.quick_action.receive.address_view.ReceiveAddressViewActivity
import com.wavesplatform.wallet.v2.ui.home.wallet.your_assets.YourAssetsActivity
import com.wavesplatform.wallet.v2.util.launchActivity
import com.wavesplatform.wallet.v2.util.notNull
import kotlinx.android.synthetic.main.fragment_cryptocurrency.*
import pers.victor.ext.click
import pers.victor.ext.gone
import pers.victor.ext.visiable
import pers.victor.ext.visiableIf
import java.math.BigDecimal
import javax.inject.Inject

class CryptoCurrencyFragment : BaseFragment(), СryptocurrencyView {

    @Inject
    @InjectPresenter
    lateinit var presenter: СryptocurrencyPresenter

    @ProvidePresenter
    fun providePresenter(): СryptocurrencyPresenter = presenter

    override fun configLayoutRes(): Int = R.layout.fragment_cryptocurrency

    companion object {

        var REQUEST_SELECT_ASSET = 10001

        fun newInstance(assetBalance: AssetBalance?): CryptoCurrencyFragment {
            val fragment =  CryptoCurrencyFragment()
            if (assetBalance == null) {
                return fragment
            }
            val args = Bundle()
            args.putParcelable(YourAssetsActivity.BUNDLE_ASSET_ITEM, assetBalance)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        if (arguments == null) {
            edit_asset.click {
                launchActivity<YourAssetsActivity>(REQUEST_SELECT_ASSET) {
                    putExtra(YourAssetsActivity.CRYPTO_CURRENCY, true)
                }
            }
            container_asset.click {
                launchActivity<YourAssetsActivity>(REQUEST_SELECT_ASSET) { }
            }
        } else {
            val assetBalance = arguments!!.getParcelable<AssetBalance>(
                    YourAssetsActivity.BUNDLE_ASSET_ITEM)
            setAssetBalance(assetBalance)
        }

        button_continue.click {
            launchActivity<ReceiveAddressViewActivity> {
                putExtra(YourAssetsActivity.BUNDLE_ASSET_ITEM, presenter.assetBalance)
            }
        }
        button_continue.isEnabled = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            presenter.assetBalance.notNull {
                setAssetBalance(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_ASSET && resultCode == Activity.RESULT_OK) {
            val assetBalance = data?.getParcelableExtra<AssetBalance>(YourAssetsActivity.BUNDLE_ASSET_ITEM)
            setAssetBalance(assetBalance)
        }
    }

    override fun showTunnel(tunnel: GetTunnel?) {
        if (tunnel?.tunnel == null) {
            button_continue.isEnabled = false
            return
        }

        val min = BigDecimal(tunnel.tunnel?.inMin).toPlainString()
        limits.text = getString(R.string.receive_minimum_amount,
                min,
                tunnel.tunnel?.currencyFrom)
        warning.text = getString(R.string.receive_warning_will_send,
                min,
                tunnel.tunnel?.currencyFrom)
        warning_crypto.text = getString(R.string.receive_warning_crypto, tunnel.tunnel?.currencyFrom)
        button_continue.isEnabled = true
    }

    override fun showError(message: String?) {

    }

    private fun setAssetBalance(assetBalance: AssetBalance?) {
        presenter.assetBalance = assetBalance

        image_asset_icon.isOval = true
        image_asset_icon.setAsset(assetBalance)
        text_asset_name.text = assetBalance?.getName()
        text_asset_value.text = assetBalance?.getDisplayBalance()

        image_is_favourite.visiableIf {
            assetBalance?.isFavorite!!
        }

        edit_asset.gone()
        container_asset.visiable()
        container_info.visiable()

        button_continue.isEnabled = true

        if (assetBalance != null) {
            presenter.getTunnel(assetBalance.assetId!!)
        }
    }
}
