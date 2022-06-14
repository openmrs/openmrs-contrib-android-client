package org.openmrs.mobile.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.OperationType.GeneralOperation
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import com.openmrs.android_sdk.library.models.Result

abstract class BaseViewModel<T> : ViewModel() {

    protected val _result: MutableLiveData<Result<T>> = MutableLiveData()
    val result: LiveData<Result<T>> get() = _result

    private var mSubscription: CompositeSubscription = CompositeSubscription()

    protected fun addSubscription(subscription: Subscription){
        mSubscription.add(subscription)
    }

    protected fun setLoading(){
        setLoading(GeneralOperation)
    }

    protected fun setLoading(operationType: OperationType){
        _result.value = Result.Loading(operationType)
    }

    protected fun setContent(data: T) {
        setContent(data, GeneralOperation)
    }

    protected fun setContent(data: T, operationType: OperationType) {
        _result.value = Result.Success(data, operationType)
    }

    protected fun setError(t: Throwable) {
        setError(t, GeneralOperation)
    }

    protected fun setError(t: Throwable, operationType: OperationType) {
        _result.value = Result.Error(t, operationType)
    }

    override fun onCleared() {
        mSubscription.clear()
        super.onCleared()
    }
}
