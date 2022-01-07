package masaya.release.manage_menu.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalArgumentException
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Entity
data class FoodMenu(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val foodName: String = "",
    @ColumnInfo(name = "price")
    val foodPrice: Double = 0.0,
    @ColumnInfo(name = "bmpName")
    val bmpName: String = "",
    @ColumnInfo(name = "startDate")
    val startDate: Date? = null,
    @ColumnInfo(name = "foodType")
    val foodType: String = "",
    @ColumnInfo(name = "WinterOnly")
    val WinterOnly: Boolean = false
)

// 価格を通貨表示
fun FoodMenu.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance(Locale.JAPAN).format(foodPrice)

// 日付をフォーマット
fun FoodMenu.getFormattedStartDate(): String? {
    return if (startDate != null) {
        SimpleDateFormat("yyyy/MM/dd").format(startDate)
    } else {
        null
    }
}

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

    @Query("DELETE FROM FoodMenu")
    suspend fun deleteAll()

}

fun String.toDateorNull(pattern: String = "yyyy/MM/dd"): Date? {
    val sdFormat = try {
        SimpleDateFormat(pattern)
    } catch (e: IllegalArgumentException) {
        return null
    }
    val date = sdFormat.let {
        try {
            it.parse(this)
        } catch (e: ParseException){
            return null
        }
    }
    return date
}