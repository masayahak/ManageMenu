package masaya.release.manage_menu.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch
import masaya.release.manage_menu.toDateorNull

class FoodMenuViewModel(private val foodMenuDao: FoodMenuDao) : ViewModel() {

    // SELECT ALL ─────────────────────────────────────────────────
    fun allItems(): LiveData<List<FoodMenu>> {
        return foodMenuDao.getItems().asLiveData()
    }

    // SELECT by ID ─────────────────────────────────────────────────
    fun retrieveFood(id: Int): LiveData<FoodMenu> {
        return foodMenuDao.getItem(id).asLiveData()
    }

    // INSERT ─────────────────────────────────────────────────
    fun addNewFood(
        foodName: String,
        foodPrice: String,
        bmpName: String,
        startDate: String,
        foodType: String,
        WinterOnly: Boolean
    ) : Boolean {
        val newFood = getNewFoodEntry(foodName, foodPrice, bmpName, startDate, foodType, WinterOnly)
        try{
            insertFood(newFood)
        }catch(e: Exception){
            println(e)
            return false
        }
        return true
    }
    // 画面入力状態（価格もString型）で受け取ったものを、DBの型に変換
    private fun getNewFoodEntry(
        foodName: String,
        foodPrice: String,
        bmpName: String,
        startDate: String,
        foodType: String,
        WinterOnly: Boolean
    ): FoodMenu {
        return FoodMenu(
            foodName = foodName,
            foodPrice = foodPrice.toDouble(),
            bmpName = bmpName,
            startDate = startDate.toDateorNull(),
            foodType = foodType,
            WinterOnly = WinterOnly
        )
    }
    //コルーチンでinsertを実装
    private fun insertFood(food: FoodMenu) {
        viewModelScope.launch {
            foodMenuDao.insert(food)
        }
    }

    // UPDATE ─────────────────────────────────────────────────
    fun updateFood(
        foodId: Int,
        foodName: String,
        foodPrice: String,
        bmpName: String,
        startDate: String,
        foodType: String,
        WinterOnly: Boolean
    ) : Boolean {
        val updatedFood = getUpdatedFoodEntry(foodId, foodName, foodPrice, bmpName, startDate, foodType, WinterOnly)
        try{
            updateFood(updatedFood)
        }catch(e: Exception){
            println(e)
            return false
        }
        return true
    }
    private fun getUpdatedFoodEntry(
        foodId: Int,
        foodName: String,
        foodPrice: String,
        bmpName: String,
        startDate: String,
        foodType: String,
        WinterOnly: Boolean
    ): FoodMenu {
        return FoodMenu(
            id = foodId,
            foodName = foodName,
            foodPrice = foodPrice.toDouble(),
            bmpName = bmpName,
            startDate = startDate.toDateorNull(),
            foodType = foodType,
            WinterOnly = WinterOnly
        )
    }
    private fun updateFood(food: FoodMenu) {
        viewModelScope.launch {
            foodMenuDao.update(food)
        }
    }

    // DELETE ─────────────────────────────────────────────────
    fun deleteFood(food: FoodMenu) {
        viewModelScope.launch {
            foodMenuDao.delete(food)
        }
    }
}

class FoodMenuViewModelFactory(private val foodMenuDao: FoodMenuDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodMenuViewModel(foodMenuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}