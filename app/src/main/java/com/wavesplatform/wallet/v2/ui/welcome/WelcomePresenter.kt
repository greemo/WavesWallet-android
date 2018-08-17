package com.wavesplatform.wallet.v2.ui.welcome

import com.arellomobile.mvp.InjectViewState
import com.wavesplatform.wallet.v2.ui.base.presenter.BasePresenter
import javax.inject.Inject

@InjectViewState
class WelcomePresenter @Inject constructor() : BasePresenter<WelcomeView>() {
    var state = 0
    var nextItemPosition = 1
}