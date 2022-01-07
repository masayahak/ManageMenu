package masaya.release.manage_menu.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListOrderViewModel : ViewModel() {

    private val _lastOrderKey = MutableLiveData("")
    val lastOrderKey: LiveData<String> = _lastOrderKey

    fun setLastOrderKey(lastOrderKey: String) {
        _lastOrderKey.value = lastOrderKey
    }

    private val _lastOrderAsce = MutableLiveData(true)
    val lastOrderAsce: LiveData<Boolean> = _lastOrderAsce

    fun setLastOrderAsce(lastOrderAsce: Boolean) {
        _lastOrderAsce.value = lastOrderAsce
    }


    private val _foodNameAsce = MutableLiveData(true)
    val foodNameAsce: LiveData<Boolean> = _foodNameAsce

    fun setFoodNameAsce(foodNameAsce: Boolean) {
        _foodNameAsce.value = foodNameAsce
    }

    private val _foodPriceAsce = MutableLiveData(true)
    val foodPriceAsce: LiveData<Boolean> = _foodPriceAsce

    fun setFoodPriceAsce(foodPriceAsce: Boolean) {
        _foodPriceAsce.value = foodPriceAsce
    }
}