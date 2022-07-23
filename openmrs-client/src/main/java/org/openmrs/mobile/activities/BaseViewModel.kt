package org.openmrs.mobile.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.OperationType.GeneralOperation
import com.openmrs.android_sdk.library.models.Result
import rx.Subscription
import rx.subscriptions.CompositeSubscription

abstract class BaseViewModel<T> : ViewModel() {

    protected val _result: MutableLiveData<Result<T>> = MutableLiveData()
    val result: LiveData<Result<T>> get() = _result

    protected val mSubscription: CompositeSubscription = CompositeSubscription()

    protected fun addSubscription(subscription: Subscription) {
        mSubscription.add(subscription)
    }

    protected fun clearSubscriptions() {
        mSubscription.clear()
    }

    protected fun setLoading(operationType: OperationType = GeneralOperation) {
        _result.value = Result.Loading(operationType)
    }

    protected open fun setContent(data: T, operationType: OperationType = GeneralOperation) {
        _result.value = Result.Success(data, operationType)
    }

    protected open fun setError(t: Throwable, operationType: OperationType = GeneralOperation) {
        _result.value = Result.Error(t, operationType)
    }

    override fun onCleared() {
        mSubscription.clear()
        super.onCleared()
    }
}
