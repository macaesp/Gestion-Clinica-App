package com.example.gestionclinica.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestionclinica.model.Funcionario
import com.example.gestionclinica.model.RegistroTrazabilidad
import com.example.gestionclinica.model.RolUsuario

@Database(
    entities = [
        Funcionario::class,
        DocumentoEntity::class,
        RegistroTrazabilidad::class,
        UsuarioEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun funcionarioDao(): FuncionarioDao
    abstract fun documentoDao(): DocumentoDao
    abstract fun trazabilidadDao(): TrazabilidadDao
    abstract fun usuarioDao(): UsuarioDao
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
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(datosIniciales)
                    .build()
                    .also { instancia = it }
            }

        // v1 -> v2: se agrega la tabla de usuarios (con hash de contraseña).
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        rut TEXT NOT NULL,
                        rol TEXT NOT NULL,
                        activo INTEGER NOT NULL,
                        passwordHash TEXT NOT NULL,
                        salt TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_rut ON usuarios (rut)")
                sembrarUsuarios(db)
            }
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
                sembrarUsuarios(db)
            }
        }

        // Contraseña de las cuentas de demostración. Cambiarla antes de usar datos reales.
        private const val PASSWORD_DEMO = "Demo2026!"

        private val usuariosDemo = listOf(
            Triple("Macarena Espinoza S", "12345678-5", RolUsuario.ADMINISTRADOR_SISTEMA),
            Triple("Benjamin Gonzales", "22222222-2", RolUsuario.RECURSOS_HUMANOS),
            Triple("Usuario Ejemplo", "11111111-1", RolUsuario.FUNCIONARIO)
        )

        private fun sembrarUsuarios(db: SupportSQLiteDatabase) {
            usuariosDemo.forEach { (nombre, rut, rol) ->
                val sal = PasswordHasher.nuevaSal()
                val hash = PasswordHasher.hash(PASSWORD_DEMO, sal)
                db.execSQL(
                    "INSERT INTO usuarios (nombre, rut, rol, activo, passwordHash, salt) " +
                            "VALUES (?, ?, ?, 1, ?, ?)",
                    arrayOf<Any>(nombre, rut, rol.name, hash, sal)
                )
            }
        }
    }
}