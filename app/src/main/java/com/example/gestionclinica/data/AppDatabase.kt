package com.example.gestionclinica.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestionclinica.model.Funcionario
import com.example.gestionclinica.model.RegistroTrazabilidad

@Database(
    entities = [
        Funcionario::class,
        DocumentoEntity::class,
        RegistroTrazabilidad::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun funcionarioDao(): FuncionarioDao
    abstract fun documentoDao(): DocumentoDao
    abstract fun trazabilidadDao(): TrazabilidadDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_clinica.db"
                )
                    .addCallback(datosIniciales)
                    .build()
                    .also { instancia = it }
            }

        // Datos de prueba: solo se insertan cuando la BD se crea por primera vez.
        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    INSERT INTO funcionarios
                    (id, rut, nombre, centroSalud, cargo, profesion, tipoContrato,
                     fechaIngreso, situacionContractual, estado)
                    VALUES
                    (1, '12.345.678-9', 'María González', 'CESFAM', 'Enfermera', 'Enfermería',
                     'Planta', '2015-03-01', 'Vigente', 'Activo'),
                    (2, '15.678.901-2', 'Juan Pérez', 'CECOSF', 'Técnico en Enfermería', 'TENS',
                     'Contrata', '2018-06-15', 'Vigente', 'Activo')
                    """.trimIndent()
                )
            }
        }
    }
}