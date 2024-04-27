import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        @Volatile private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context).also {
                    INSTANCE = it
                }
            }
        const val DATABASE_NAME = "scores.db"
        const val DATABASE_VERSION = 1
        const val TABLE_SCORES = "scores"
        const val COLUMN_PLAYER_NAME = "player_name"
        const val COLUMN_SCORE = "score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_SCORES ($COLUMN_PLAYER_NAME TEXT PRIMARY KEY, $COLUMN_SCORE INTEGER)"
        db.execSQL(createTableQuery)
        Log.d("DatabaseHelper", "Table created successfully: $createTableQuery")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCORES")
        onCreate(db)
        Log.d("DatabaseHelper", "Database upgraded from version $oldVersion to $newVersion")
    }
}