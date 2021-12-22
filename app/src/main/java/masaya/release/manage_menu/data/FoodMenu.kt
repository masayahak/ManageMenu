package masaya.release.manage_menu.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat
import java.util.*

@Entity
data class FoodMenu(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val foodName: String,
    @ColumnInfo(name = "price")
    val foodPrice: Double
)
fun FoodMenu.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance(Locale.JAPAN).format(foodPrice)

@Dao
interface FoodMenuDao {

    @Query("SELECT * from FoodMenu ORDER BY name")
    fun getItems(): Flow<List<FoodMenu>>

    @Query("SELECT * from FoodMenu WHERE id = :id")
    fun getItem(id: Int): Flow<FoodMenu>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FoodMenu)

    @Update
    suspend fun update(item: FoodMenu)

    @Delete
    suspend fun delete(item: FoodMenu)

}
