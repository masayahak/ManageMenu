package masaya.release.manage_menu.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ユーザーの画面入力を同一アクティビティ内で保持するためのビューモデル
class UserInputViewModel : ViewModel() {

    private val _foodId = MutableLiveData(-1)
    val foodId: LiveData<Int> = _foodId

    fun setfoodId(foodId: Int) {
        _foodId.value = foodId
    }

    private val _foodName = MutableLiveData("")
    val foodName: LiveData<String> = _foodName

    fun setFoodName(foodName: String) {
        _foodName.value = foodName
    }

    // あくまでも画面入力の途中の状態なので数値ではなく文字列で良い
    private val _price = MutableLiveData("")
    val price: LiveData<String> = _price

    fun setPrice(price: String) {
        _price.value = price
    }

    private val _bmpName = MutableLiveData("")
    val bmpName: LiveData<String> = _bmpName

    fun setBmpName(bmpName: String) {
        _bmpName.value = bmpName
    }

    // あくまでも画面入力の途中の状態なので日付ではなく文字列で良い
    private val _startDate = MutableLiveData("")
    val startDate: LiveData<String> = _startDate

    fun setStartDate(startDate: String) {
        _startDate.value = startDate
    }

    private val _foodType = MutableLiveData("")
    val foodType: LiveData<String> = _foodType

    fun setFoodType(foodType: String) {
        _foodType.value = foodType
    }

    private val _winterOnly = MutableLiveData(false)
    val winterOnly: LiveData<Boolean> = _winterOnly

    fun setWinterOnly(winterOnly: Boolean) {
        _winterOnly.value = winterOnly
    }


}