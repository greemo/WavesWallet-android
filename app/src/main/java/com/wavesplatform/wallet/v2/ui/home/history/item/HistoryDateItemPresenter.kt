package com.wavesplatform.wallet.v2.ui.home.history.item

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import com.wavesplatform.wallet.v2.ui.base.presenter.BasePresenter
import com.wavesplatform.wallet.v2.ui.home.history.TestObject
import com.wavesplatform.wallet.v2.ui.home.history.adapter.HistoryItem
import java.util.*
import javax.inject.Inject

@InjectViewState
class HistoryDateItemPresenter @Inject constructor() : BasePresenter<HistoryDateItemView>() {
    fun loadBundle(arguments: Bundle?) {
        val type = arguments?.getString("type")

        var data = ArrayList<HistoryItem>()
        data.add(HistoryItem(true, "February 13, 2018"))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), 523.061350, Random().nextDouble())))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), 3.061350, Random().nextDouble())))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), 0.061350, Random().nextDouble())))
        data.add(HistoryItem(true, "February 12, 2018"))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), 363.5061350, Random().nextDouble())))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), Random().nextDouble(), Random().nextDouble())))
        data.add(HistoryItem(true, "February 11, 2018"))
        data.add(HistoryItem(TestObject("Waves", Random().nextBoolean(), Random().nextBoolean(), Random().nextDouble(), Random().nextDouble())))

        viewState.showData(data, type!!)
    }

}
