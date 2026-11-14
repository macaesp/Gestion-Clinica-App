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
import com.example.gestionclinica.data.daos.DocumentoDao
import com.example.gestionclinica.data.daos.FuncionarioDao
import com.example.gestionclinica.data.daos.TrazabilidadDao
import com.example.gestionclinica.data.daos.UsuarioDao
import com.example.gestionclinica.data.daos.SolicitudCambioDao
import com.example.gestionclinica.data.daos.VersionDocumentoDao
import com.example.gestionclinica.data.SolicitudCambioEntity
import com.example.gestionclinica.data.VersionDocumentoEntity

@Database(
    entities = [
        Funcionario::class,
        DocumentoEntity::class,
        RegistroTrazabilidad::class,
        UsuarioEntity::class,
        SolicitudCambioEntity::class,
        VersionDocumentoEntity::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun funcionarioDao(): FuncionarioDao
    abstract fun documentoDao(): DocumentoDao
    abstract fun trazabilidadDao(): TrazabilidadDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun solicitudCambioDao(): SolicitudCambioDao
    abstract fun versionDocumentoDao(): VersionDocumentoDao
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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_4,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8
                    )
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

        // v2 -> v3: agrega el expediente de prueba vinculado al usuario FUNCIONARIO.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                insertarFuncionarioDemo(db)
            }
        }

        // v3 -> v4: permite limitar cuentas de jefatura y administrativas a un centro.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN centroAsignado TEXT")
            }
        }

        // v4 -> v5 fue una prueba de RF12. Conserva el esquema actual de RF12,
        // permitiendo que instalaciones con esa versión se actualicen sin perder datos.
        internal val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

        // Permite volver desde la prueba de RF12 a cuentas/permisos conservando
        // funcionarios, documentos, cuentas y trazabilidad existentes.
        private val MIGRATION_5_4 = object : Migration(5, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS solicitudes_cambio_documento")
            }
        }

        internal val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS solicitudes_cambio_documento (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentoId INTEGER NOT NULL,
                        solicitanteId INTEGER NOT NULL,
                        fechaSolicitud TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        motivo TEXT NOT NULL,
                        camposAfectados TEXT NOT NULL,
                        observacionRevision TEXT,
                        revisorId INTEGER,
                        fechaRevision TEXT,
                        solicitudAnteriorId INTEGER,
                        propuesta_funcionarioId INTEGER NOT NULL,
                        propuesta_usuarioCargaId INTEGER NOT NULL,
                        propuesta_fechaCarga TEXT NOT NULL,
                        propuesta_estado TEXT NOT NULL,
                        propuesta_tipo TEXT NOT NULL,
                        propuesta_subtipoLaboral TEXT,
                        propuesta_tipoSolicitud TEXT,
                        propuesta_respaldo TEXT,
                        propuesta_fechaInicio TEXT,
                        propuesta_fechaFin TEXT,
                        propuesta_estadoAprobacion TEXT,
                        propuesta_nombreCurso TEXT,
                        propuesta_certificadoRespaldo TEXT,
                        propuesta_cantidadHoras INTEGER,
                        propuesta_estadoValidacion TEXT,
                        propuesta_paginasEscaneadas TEXT NOT NULL,
                        propuesta_pdfEscaneado TEXT
                    )""".trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_solicitudes_cambio_documento_documentoId ON solicitudes_cambio_documento (documentoId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_solicitudes_cambio_documento_solicitanteId ON solicitudes_cambio_documento (solicitanteId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_solicitudes_cambio_documento_estado ON solicitudes_cambio_documento (estado)")

                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS versiones_documento (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentoId INTEGER NOT NULL,
                        numeroVersion INTEGER NOT NULL,
                        fechaRegistro TEXT NOT NULL,
                        usuarioCambioId INTEGER NOT NULL,
                        solicitudId INTEGER NOT NULL,
                        contenido_funcionarioId INTEGER NOT NULL,
                        contenido_usuarioCargaId INTEGER NOT NULL,
                        contenido_fechaCarga TEXT NOT NULL,
                        contenido_estado TEXT NOT NULL,
                        contenido_tipo TEXT NOT NULL,
                        contenido_subtipoLaboral TEXT,
                        contenido_tipoSolicitud TEXT,
                        contenido_respaldo TEXT,
                        contenido_fechaInicio TEXT,
                        contenido_fechaFin TEXT,
                        contenido_estadoAprobacion TEXT,
                        contenido_nombreCurso TEXT,
                        contenido_certificadoRespaldo TEXT,
                        contenido_cantidadHoras INTEGER,
                        contenido_estadoValidacion TEXT,
                        contenido_paginasEscaneadas TEXT NOT NULL,
                        contenido_pdfEscaneado TEXT
                    )""".trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_versiones_documento_documentoId_numeroVersion ON versiones_documento (documentoId, numeroVersion)")
            }
        }

        // v6 -> v7: permite registrar versiones originadas por una edición directa de RR. HH.
        internal val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE versiones_documento_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentoId INTEGER NOT NULL,
                        numeroVersion INTEGER NOT NULL,
                        fechaRegistro TEXT NOT NULL,
                        usuarioCambioId INTEGER NOT NULL,
                        solicitudId INTEGER,
                        contenido_funcionarioId INTEGER NOT NULL,
                        contenido_usuarioCargaId INTEGER NOT NULL,
                        contenido_fechaCarga TEXT NOT NULL,
                        contenido_estado TEXT NOT NULL,
                        contenido_tipo TEXT NOT NULL,
                        contenido_subtipoLaboral TEXT,
                        contenido_tipoSolicitud TEXT,
                        contenido_respaldo TEXT,
                        contenido_fechaInicio TEXT,
                        contenido_fechaFin TEXT,
                        contenido_estadoAprobacion TEXT,
                        contenido_nombreCurso TEXT,
                        contenido_certificadoRespaldo TEXT,
                        contenido_cantidadHoras INTEGER,
                        contenido_estadoValidacion TEXT,
                        contenido_paginasEscaneadas TEXT NOT NULL,
                        contenido_pdfEscaneado TEXT
                    )""".trimIndent()
                )
                db.execSQL(
                    """INSERT INTO versiones_documento_nueva (
                        id, documentoId, numeroVersion, fechaRegistro, usuarioCambioId, solicitudId,
                        contenido_funcionarioId, contenido_usuarioCargaId, contenido_fechaCarga,
                        contenido_estado, contenido_tipo, contenido_subtipoLaboral, contenido_tipoSolicitud,
                        contenido_respaldo, contenido_fechaInicio, contenido_fechaFin, contenido_estadoAprobacion,
                        contenido_nombreCurso, contenido_certificadoRespaldo, contenido_cantidadHoras,
                        contenido_estadoValidacion, contenido_paginasEscaneadas, contenido_pdfEscaneado
                    ) SELECT
                        id, documentoId, numeroVersion, fechaRegistro, usuarioCambioId, solicitudId,
                        contenido_funcionarioId, contenido_usuarioCargaId, contenido_fechaCarga,
                        contenido_estado, contenido_tipo, contenido_subtipoLaboral, contenido_tipoSolicitud,
                        contenido_respaldo, contenido_fechaInicio, contenido_fechaFin, contenido_estadoAprobacion,
                        contenido_nombreCurso, contenido_certificadoRespaldo, contenido_cantidadHoras,
                        contenido_estadoValidacion, contenido_paginasEscaneadas, contenido_pdfEscaneado
                    FROM versiones_documento""".trimIndent()
                )
                db.execSQL("DROP TABLE versiones_documento")
                db.execSQL("ALTER TABLE versiones_documento_nueva RENAME TO versiones_documento")
                db.execSQL(
                    "CREATE UNIQUE INDEX index_versiones_documento_documentoId_numeroVersion " +
                        "ON versiones_documento (documentoId, numeroVersion)"
                )
            }
        }

        // v7 -> v8: agrega las cuentas demo que no existían al crear instalaciones previas.
        internal val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                usuariosEjem.forEach { (nombre, rut, rol) ->
                    val credenciales = db.query(
                        "SELECT salt, passwordHash FROM usuarios WHERE rut = ? LIMIT 1",
                        arrayOf(rut)
                    ).use { cursor ->
                        if (cursor.moveToFirst()) {
                            cursor.getString(0) to cursor.getString(1)
                        } else {
                            null
                        }
                    }

                    if (credenciales == null) {
                        insertarUsuarioDemo(db, nombre, rut, rol, CONTRASEÑA_DEMO)
                    } else if (PasswordHasher.verificar(
                            CONTRASEÑA_DEMO_ANTERIOR,
                            credenciales.first,
                            credenciales.second
                        )
                    ) {
                        // Actualiza solo las cuentas que aún conservan la contraseña demo antigua.
                        val hashNuevo = PasswordHasher.hash(CONTRASEÑA_DEMO, credenciales.first)
                        db.execSQL(
                            "UPDATE usuarios SET passwordHash = ? WHERE rut = ?",
                            arrayOf<Any>(hashNuevo, rut)
                        )
                    }
                }
            }
        }

        // Datos de prueba
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
                insertarFuncionarioDemo(db)
                sembrarUsuarios(db)
            }
        }

        private fun insertarFuncionarioDemo(db: SupportSQLiteDatabase) {
            db.execSQL(
                "INSERT OR IGNORE INTO funcionarios " +
                    "(rut, nombre, centroSalud, cargo, profesion, tipoContrato, " +
                    "fechaIngreso, situacionContractual, estado) " +
                    "VALUES ('11111111-1', 'Usuario Ejemplo', 'CESFAM de prueba', " +
                    "'Funcionario de prueba', 'Administración', 'Contrata', " +
                    "'2026-01-01', 'Vigente', 'Activo')"
            )
        }

        // Contraseña de las cuentas ejemplo
        private const val CONTRASEÑA_DEMO = "Demo2026!"
        private const val CONTRASEÑA_DEMO_ANTERIOR = "Duoc2026!"

        // cuentas para entrar a la app
        private val usuariosEjem = listOf(
            Triple("Macarena Espinoza S", "12345678-5", RolUsuario.ADMINISTRADOR_SISTEMA),
            Triple("Benjamin Gonzales", "22222222-2", RolUsuario.RECURSOS_HUMANOS),
            Triple("Franchesco Ejistre", "11111111-1", RolUsuario.FUNCIONARIO),
            Triple("Alexandra Smith","33333333-3", RolUsuario.JEFATURA)
        )

        private fun sembrarUsuarios(db: SupportSQLiteDatabase) {
            usuariosEjem.forEach { (nombre, rut, rol) ->
                insertarUsuarioDemo(db, nombre, rut, rol, CONTRASEÑA_DEMO)
            }
        }

        private fun insertarUsuarioDemo(
            db: SupportSQLiteDatabase,
            nombre: String,
            rut: String,
            rol: RolUsuario,
            contraseña: String
        ) {
            val sal = PasswordHasher.nuevaSal()
            val hash = PasswordHasher.hash(contraseña, sal)
            db.execSQL(
                "INSERT OR IGNORE INTO usuarios (nombre, rut, rol, activo, passwordHash, salt) " +
                        "VALUES (?, ?, ?, 1, ?, ?)",
                arrayOf<Any>(nombre, rut, rol.name, hash, sal)
            )
        }
    }
}
