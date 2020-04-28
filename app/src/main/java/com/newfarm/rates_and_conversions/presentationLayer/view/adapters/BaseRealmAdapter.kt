package com.newfarm.rates_and_conversions.presentationLayer.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newfarm.rates_and_conversions.BR
import com.newfarm.rates_and_conversions.presentationLayer.view.common.BaseRealmViewHolder
import com.newfarm.rates_and_conversions.presentationLayer.viewModel.common.RootViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.realm.*

open class BaseRealmAdapter<T: RealmObject>(val viewModel: RootViewModel, private val holderLayout: Int,
                                            data: RealmResults<T>,
                                            automaticUpdate: Boolean = true,
                                            updateOnModification: Boolean = true)
    : RealmRecyclerViewAdapter<T, BaseRealmViewHolder>(data, automaticUpdate, updateOnModification) {

    private val disposable = CompositeDisposable()

    var onItemClick: PublishSubject<T> = PublishSubject.create()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseRealmViewHolder {
        val itemView = LayoutInflater.from(viewModel.context).inflate(holderLayout, viewGroup, false)

        return BaseRealmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseRealmViewHolder, position: Int) {
        val model = getItemByPosition(position)

        holder.getBinding().setVariable(BR.viewModel, viewModel)
        holder.getBinding().setVariable(BR.model, model)
        holder.getBinding().executePendingBindings()

        holder.itemView.setOnClickListener { v ->
            v.isClickable = false
            onItemClick.onNext(model)
            v.postDelayed({ v.isClickable = true }, 500)
        }
    }

    open fun getItemByPosition(position: Int): T {
        return data!![position]
    }

    fun getPosition(item: T): Int {
        return data!!.indexOf(item)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        Log.d(this.javaClass.simpleName, String.format("Disposed %d items", disposable.size()))
        disposable.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}