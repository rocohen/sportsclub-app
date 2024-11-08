package com.mediadocena.clubdeportivo.database

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mediadocena.clubdeportivo.ActivityModel
import com.mediadocena.clubdeportivo.dataclasses.MemberDetails
import com.mediadocena.clubdeportivo.dataclasses.Usuario
import com.mediadocena.clubdeportivo.entities.Cliente
import java.time.LocalDate

class ClubDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "sportsclub.db"
        private const val CREATE_TABLE_ROLES = """
            CREATE TABLE IF NOT EXISTS roles (
                RolUsu INTEGER PRIMARY KEY AUTOINCREMENT,
                NomRol TEXT
            );
        """
        private const val CREATE_TABLE_USER = """
            CREATE TABLE IF NOT EXISTS usuario (
                CodUsu INTEGER PRIMARY KEY AUTOINCREMENT,
                NombreUsu TEXT,
                PassUsu TEXT,
                RolUsu INTEGER,
                Activo INTEGER DEFAULT 1,
                FOREIGN KEY(RolUsu) REFERENCES roles(RolUsu)
            );
        """
        private const val CREATE_TABLE_CLIENTS = """
            CREATE TABLE IF NOT EXISTS clientes (
                idCliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreC TEXT,
                apellidoC TEXT,
                DNIC TEXT,
                telC TEXT,
                correoC TEXT,
                tipoC TEXT,
                aptoFisico INTEGER DEFAULT 0,
                estadoC INTEGER DEFAULT 1
            );
        """
        private const val CREATE_TABLE_FEES = """
            CREATE TABLE IF NOT EXISTS cuotas (
                idCuota INTEGER PRIMARY KEY AUTOINCREMENT,
                idCliente INTEGER,
                fecha TEXT,
                monto REAL CHECK(monto > 0),
                formaPago TEXT,
                fechaVence TEXT,
                tienePromo INTEGER DEFAULT 0,
                detalle TEXT,
                FOREIGN KEY(idCliente) REFERENCES clientes(idCliente)
            );
        """
        private const val CREATE_TABLE_ACTIVITIES = """
            CREATE TABLE IF NOT EXISTS actividades (
                idActividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreA TEXT,
                cupoMax INTEGER,
                cupoDisp INTEGER,
                precio REAL CHECK(precio > 0),
                estadoA INTEGER DEFAULT 1
            );
        """
        private const val CREATE_TABLE_CLIENACTIV = """
            CREATE TABLE IF NOT EXISTS clienactiv (
                idClientactiv INTEGER PRIMARY KEY AUTOINCREMENT,
                idCliente INTEGER NOT NULL,
                idActividad INTEGER NOT NULL,
                fechaInscripcion TEXT,
                FOREIGN KEY(idCliente) REFERENCES clientes(idCliente),
                FOREIGN KEY(idActividad) REFERENCES actividades(idActividad)
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Creación de tablas
        db?.execSQL(CREATE_TABLE_ROLES)

        db?.execSQL(CREATE_TABLE_USER)

        db?.execSQL(CREATE_TABLE_CLIENTS)

        db?.execSQL(CREATE_TABLE_FEES)

        db?.execSQL(CREATE_TABLE_ACTIVITIES)

        db?.execSQL(CREATE_TABLE_CLIENACTIV)

        // Inserciones iniciales
        db?.execSQL("""
            INSERT INTO roles VALUES (120, 'Administrador'), (121, 'Empleado');
        """)

        db?.execSQL("""
            INSERT INTO usuario (CodUsu, NombreUsu, PassUsu, RolUsu) VALUES
            (26, 'mediadocena', '123456', 120),
            (27, 'UsuarioPrueba', 'Usu2024', 120),
            (28, 'Guille2024', '654321', 121);
        """)

        db?.execSQL("""
            INSERT INTO actividades VALUES
            (20, 'Yoga', 20, 5, 2000, 1),
            (21, 'Pilates', 15, 10, 1500, 1),
            (22, 'Spinning', 25, 20, 1800, 1),
            (23, 'Zumba', 30, 25, 1000, 1),
            (24, 'CrossFit', 20, 12, 2000, 1),
            (25, 'Entrenamiento Funcional', 18, 8, 1800, 1),
            (26, 'Kickboxing', 20, 10, 8000, 1),
            (27, 'Natación', 10, 5, 2500, 1),
            (28, 'Aeróbicos', 25, 20, 1200, 1),
            (29, 'Taekwondo', 20, 10, 1500, 1),
            (30, 'Entrenamiento de Resistencia', 15, 10, 1900, 1),
            (31, 'Taekwondo', 20, 10, 1500, 1),
            (32, 'Capoeira', 20, 15, 1500, 1),
            (33, 'Gimnasia', 20, 12, 1300, 1),
            (34, 'Tai Chi', 20, 18, 1100, 1),
            (35, 'Entrenamiento en Circuito', 15, 0, 2100, 1);
        """)

        db?.execSQL("""
            INSERT INTO clientes VALUES
            (452, "Juan", "Gómez", "25234567", "97551234", "juan.gomez@example.com", "Socio",1, 1),
            (453, "María", "Pérez", "26345678", "89562345", "maria.perez@example.com", "No Socio",0, 1),
            (454, "Carlos", "López", "27456789", "42573456", "carlos.lopez@example.com", "Socio",0, 1),
            (455, "Ana", "Martínez", "28567890", "79584567", "ana.martinez@example.com", "No Socio",0, 1),
            (456, "José", "García", "29678901", "27595678", "jose.garcia@example.com", "Socio",0, 1),
            (457, "Lucas", "Rodríguez", "30789012", "89606789", "lucas.rodriguez@example.com", "No Socio",0, 1),
            (458, "Miguel", "Hernández", "31890123", "57617890", "miguel.hernandez@example.com", "Socio",1, 1),
            (459, "Laura", "González", "32901234", "53628901", "laura.gonzalez@example.com", "No Socio",0, 1),
            (460, "Fernando", "Torres", "33012345", "43639012", "fernando.torres@example.com", "Socio",1, 1),
            (461, "Sofía", "Ramírez", "34123456", "48640123", "sofia.ramirez@example.com", "No Socio",0, 1),
            (462, "Pedro", "Flores", "35234567", "21651234", "pedro.flores@example.com", "Socio",1, 0),
            (463, "Marta", "Ruiz", "36345678", "76662345", "marta.ruiz@example.com", "No Socio",0, 1),
            (464, "Diego", "Vargas","37456789","80673456", "diego.vargas@example.com", "Socio",1, 1),
            (465, "Elena", "Acosta", "38567890", "55684567", "elena.acosta@example.com", "No Socio",0, 1),
            (466, "Hugo", "Mendoza", "25678901", "40695678", "hugo.mendoza@example.com", "Socio",1, 1),
            (467, "Patricia", "Ortiz", "26789012", "20706789", "patricia.ortiz@example.com", "Socio",1, 1),
            (468, "Ricardo", "Cruz", "27890123", "39717890", "ricardo.cruz@example.com", "Socio",1, 0),
            (469, "Carolina", "Morales", "28901234", "31728901", "carolina.morales@example.com", "No Socio",0, 1),
            (470, "Luis", "Vera", "29012345", "24739012", "luis.vera@example.com", "Socio",1, 1),
            (471, "Valeria", "Ramos", "30123456", "54740123", "valeria.ramos@example.com", "No Socio",0, 1),
            (472, "Gustavo", "Ríos", "31234567", "70751234", "gustavo.rios@example.com", "Socio",1, 1),
            (473, "Florencia", "Romero", "32345678", "90762345", "florencia.romero@example.com", "No Socio",0, 1),
            (474, "Roberto", "Medina", "33456789", "78773456", "roberto.medina@example.com", "Socio",1, 1),
            (475, "Daniela", "Chávez", "34567890", "23784567", "daniela.chavez@example.com", "No Socio",0, 1),
            (476, "Javier", "Silva", "35678901", "35795678", "javier.silva@example.com", "Socio",1, 1),
            (477, "Gabriela", "Soto", "36789012", "55806789", "gabriela.soto@example.com", "No Socio",0, 1),
            (478, "Felipe", "Castro", "37890123", "15817890", "felipe.castro@example.com", "Socio",1, 1),
            (479, "Verónica", "Peña", "38901234", "45588901", "veronica.pena@example.com", "No Socio",0, 1),
            (480, "Francisco", "Luna", "25012345", "95839012", "francisco.luna@example.com", "Socio",0, 1),
            (481, "Julio", "Escobar", "26123456", "75840123", "julio.escobar@example.com", "No Socio",0, 1),
            (482, "Agustín", "Molina", "27234567", "65851234", "agustin.molina@example.com", "Socio",1, 1),
            (483, "Camila", "Suárez", "28345678", "86234554","camila.suarez@example.com", "No Socio",0, 1),
            (484, "Rodrigo", "Santos", "29456789", "87345678", "rodrigo.santos@example.com", "Socio",1, 0),
            (485, "Nicolás", "Figueroa", "30567890", "35884567", "nico.figueroa@example.com", "No Socio",0, 1),
            (486, "Tomás", "Herrera", "31678901", "45895678", "tomas.herrera@example.com", "No Socio",0, 1),
            (487, "Lola", "Guzmán", "32789012", "42906789", "lola.guzman@example.com", "Socio",1, 1),
            (488, "Andrés", "Peralta", "33890123", "55917890", "andres.peralta@example.com", "Socio",1, 1),
            (489, "Mónica", "Sosa", "34901234", "25928901", "monica.sosa@example.com", "No Socio",0, 1),
            (490, "Facundo", "Rivera", "35123456", "55939012", "facundo.rivera@example.com", "Socio",1, 1),
            (491, "Adriana", "Paz", "36234567", "94012312","adriana.paz@example.com", "Socio",0, 1),
            (492, "Santiago", "Núñez", "37345678", "95123434","santiago.nunez@example.com", "Socio",1, 1),
            (493, "Melina", "Cáceres", "38456789", "55962345", "melina.caceres@example.com", "No Socio",0, 1),
            (494, "Lucas", "Cabrera", "25567890", "97345648","lucas.cabrera@example.com", "Socio",1, 1),
            (495, "Paula", "Santana", "26678901", "98454567", "paula.santana@example.com", "Socio",1, 1),
            (496, "Nicolás", "Pineda", "27789012", "99455678", "nicolas.pineda@example.com", "Socio",1, 1),
            (497, "Andrea", "Gallo", "28890123", "56046789", "andrea.gallo@example.com", "Socio",1, 1),
            (498, "Matías", "Salinas", "29901234", "74589084","matias.salinas@example.com", "Socio",1, 1),
            (499, "Antonella", "Ojeda", "30012345", "72866901", "antonella.ojeda@example.com", "No Socio",0, 1),
            (500, "Hernán", "Navarro", "31123456", "63569012", "hernan.navarro@example.com", "Socio",1, 1);
        """)

        db?.execSQL("""
            INSERT INTO clienactiv VALUES 
            (20, 453, 20, '2024-11-05'), (21, 453, 21, '2024-11-05'), (22, 485, 29, '2024-11-05'), (23, 457, 32, '2024-11-05'), (24, 457, 34, '2024-11-05');
        """)

        val execSQL = db?.execSQL(
            """
            INSERT INTO cuotas VALUES
            (200,453,'2024-11-05',2000,'Efectivo','2024-11-05',0, 'Abonó actividad Yoga'),
            (201,453,'2024-11-05',1500,'Efectivo','2024-11-05',0,'Abonó actividad Pilates'),
            (202,485,'2024-11-05',1500,'Efectivo','2024-11-05',0,'Abonó actividad Taekwondo'),
            (203,457,'2024-11-05',1500,'Efectivo','2024-11-05',0,'Abonó actividad Capoeira'),
            (204,457,'2024-11-05',1100,'Efectivo','2024-11-05',0,'Abonó actividad Tai Chi'),
            (205,452, DATE('2024-11-05', '-30 days'),18000,'Efectivo',DATE('2024-11-05'),0,NULL),
            (206,454,'2024-11-02',17100, 'Tarjeta de crédito','2024-12-02',1, NULL),
            (207,456,'2024-11-01',18000, 'Efectivo','2024-12-01',0, NULL),
            (208,458,'2024-11-01',18000, 'Efectivo','2024-12-01',0, NULL),
            (209,460,'2024-11-03',18000, 'Efectivo','2024-12-03',0, NULL),
            (210,462,'2024-10-02',17100, 'Tarjeta de crédito','2024-11-02',1, NULL),
            (211,464,'2024-11-02',17100, 'Tarjeta de crédito','2024-12-02',1, NULL),
            (212,466,'2024-11-02',18000, 'Efectivo','2024-12-02',0, NULL),
            (213,467,'2024-11-03',18000, 'Efectivo','2024-12-03',0, NULL),
            (214,468,'2024-10-02',18000, 'Efectivo','2024-11-02',0, NULL),
            (215,470,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (216,472,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (217,474,DATE('now', '-30 days'),18000, 'Tarjeta de crédito',DATE('now'),0, NULL),
            (218,476,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (219,478,'2024-11-02',18000, 'Efectivo','2024-12-02',0, NULL),
            (220,480,'2024-11-03',18000, 'Efectivo','2024-12-03',0, NULL),
            (221,482,'2024-11-03',17100, 'Tarjeta de crédito','2024-12-03',1, NULL),
            (222,484,'2024-11-03',18000, 'Efectivo','2024-12-03',0, NULL),
            (223,487,'2024-11-03',18000, 'Efectivo','2024-12-03',0, NULL),
            (224,488,'2024-11-04',18000, 'Tarjeta de crédito','2024-12-04',0, NULL),
            (225,490,'2024-11-04',18000, 'Efectivo','2024-12-04',0, NULL),
            (226,491,'2024-11-04',18000, 'Efectivo','2024-12-04',0, NULL),
            (227,492,'2024-11-05',18000, 'Efectivo','2024-12-05',0, NULL),
            (228,494,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (229,495,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (230,496,DATE('now', '-30 days'),17100, 'Tarjeta de crédito',DATE('now'),1, NULL),
            (231,497,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (232,498,DATE('now', '-30 days'),18000, 'Efectivo',DATE('now'),0, NULL),
            (233,500,'2024-11-05',18000, 'Efectivo','2024-12-05',0, NULL);
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS roles");
        db?.execSQL("DROP TABLE IF EXISTS usuario");
        db?.execSQL("DROP TABLE IF EXISTS clientes");
        db?.execSQL("DROP TABLE IF EXISTS cuotas");
        db?.execSQL("DROP TABLE IF EXISTS actividades");
        db?.execSQL("DROP TABLE IF EXISTS clienactiv");
        onCreate(db)
    }


    fun obtenerUsuLogueado(nombreUsuario: String, clave: String): Usuario? {
        val db = this.readableDatabase
        val query = """
            SELECT usuario.NombreUsu, roles.NomRol 
            FROM usuario 
            JOIN roles ON usuario.RolUsu = roles.RolUsu
            WHERE NombreUsu = ? AND PassUsu = ?
        """
        val cursor = db.rawQuery(query, arrayOf(nombreUsuario, clave))
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("NombreUsu"))
            val rol = cursor.getString(cursor.getColumnIndexOrThrow("NomRol"))
            usuario = Usuario(nombre, rol)
        }

        cursor.close()
        db.close()
        return usuario
    }

    // Método para insertar un cliente en la tabla clientes
    fun registrarCliente(
        nombreC: String,
        apellidoC: String,
        dniC: String,
        telC: String,
        correoC: String,
        tipoC: String,
        aptoFisico: Int
    ): Long {
        val db = this.writableDatabase

        // Primero verificamos por el DNI si el cliente ya existe en la base de datos
        val cursor = db.rawQuery("SELECT 1 FROM clientes WHERE DNIC = ?", arrayOf(dniC))
        val existe = cursor.moveToFirst()
        cursor.close()

        if (existe) {
            db.close()
            return -2
        }

        // Si el DNI no existe, procedemos a registrar al nuevo cliente
        val valores = ContentValues().apply {
            put("nombreC", nombreC)
            put("apellidoC", apellidoC)
            put("DNIC", dniC)
            put("telC", telC)
            put("correoC", correoC)
            put("tipoC", tipoC)
            put("aptoFisico", aptoFisico)
            put("estadoC", 1)
        }

        val result = db.insert("clientes", null, valores)
        db.close()

        return if (result == -1L) -1 else result

    }


    // Método para insertar el registro de pago en la tabla cuotas (SOCIO)
    fun registrarPago(
        idCliente: Int,
        fecha: String,
        monto: Double,
        formaPago: String,
        fechaVence: String,
        tienePromo: Boolean,
        detalle: String
    ): Long {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("idCliente", idCliente)
            put("fecha", fecha)
            put("monto", monto)
            put("formaPago", formaPago)
            put("fechaVence", fechaVence)
            put("tienePromo", if (tienePromo) 1 else 0)
            put("detalle", detalle)
        }
        // Insertamos los valores en la tabla "cuotas"
        return db.insert("cuotas", null, valores).also { db.close() }

    }


    fun listarSociosCuotasAVenc(fecha: String): List<MemberDetails>  {
        val membersList = mutableListOf<MemberDetails>()
        val db = this.readableDatabase
        val query = """
            SELECT c.idCliente, concat(c.nombreC, ' ', c.apellidoC) as nombreCompleto, 
            c.telC, c.correoC, cu.monto, cu.fecha, cu.fechaVence
            FROM clientes as c
            INNER JOIN cuotas as cu ON c.idCliente = cu.idCliente
            WHERE c.tipoC = 'Socio' AND cu.fechaVence = ? ORDER BY nombreCompleto;
        """
        val cursor = db.rawQuery(query, arrayOf(fecha))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"))
                val nombreCompleto = cursor.getString(cursor.getColumnIndexOrThrow("nombreCompleto"))
                val tel = cursor.getString(cursor.getColumnIndexOrThrow("telC"))
                val correo = cursor.getString(cursor.getColumnIndexOrThrow("correoC"))
                val monto = cursor.getFloat(cursor.getColumnIndexOrThrow("monto")).toDouble()
                val fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val fechaVence = cursor.getString(cursor.getColumnIndexOrThrow("fechaVence"))

                membersList.add(MemberDetails(id,nombreCompleto,tel,correo,monto,fechaPago,fechaVence))

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return membersList
    }

    //Metodo para devolver tipo de cliente
    fun ObtenerTipoCliente(id: Int) :  String {
        val db = this.readableDatabase
        val query = """
            SELECT tipoC
            FROM clientes
            WHERE idCliente = ?
        """
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query, selectionArgs)
        var resultado = "0"

        try {
            if (cursor.moveToFirst()) {
                resultado = cursor.getString(cursor.getColumnIndexOrThrow("tipoC"))
            }
        }
        catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener el tipo de cliente: ${e.message}")
            e.printStackTrace()
        }
        finally {
            cursor.close()
            db.close()
        }
        return resultado
    }


    // Metodo para devolver ultimo vencimiento de cuota del SOCIO
    fun ObtenerUltimoVencimientoSocio (id: Int) : String {
        val db = this.readableDatabase
        val query = """
            SELECT fechaVence
            FROM cuotas
            WHERE idCliente = ?
            ORDER BY fechaVence DESC
            LIMIT 1
        """
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query, selectionArgs)
        var resultado = "0"
        try {
            if (cursor.moveToFirst()) {
                resultado = cursor.getString(cursor.getColumnIndexOrThrow("fechaVence"))
            }
        }
        catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener el ultimo registro de vencimiento del cliente: ${e.message}")
            e.printStackTrace()
        }
        finally {
            cursor.close()
            db.close()
        }
        return resultado
    }


    // Metodo para obtener cupo disponible de la actividad a inscribir
    fun ObtenerCupoDisp (id : Int) : Int {
        val db = this.readableDatabase
        val query = """
            SELECT cupoDisp
            FROM actividades
            WHERE idActividad = ?
        """
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query,selectionArgs)
        var resultado = 0
        try {
            if (cursor.moveToFirst()) {
                resultado  =  cursor.getInt(cursor.getColumnIndexOrThrow("cupoDisp"))
            }
        }
        catch (e: Exception) {
            Log.e("DatabaseError", "Error: No se pudo obtener el cupo disponible de la actividad")
            e.printStackTrace()
        }
        finally {
            cursor.close()
            db.close()
        }
        return resultado
    }


    // Metodo para cambiar el cupo luego de inscripcion
    fun ModificarCupoDisponible (id : Int, nuevoCupo : Int) : String {
        val db = this.writableDatabase
        var valores = ContentValues().apply {
            put("cupoDisp", nuevoCupo)
        }
        var resultado = db.update("actividades", valores, "idActividad=?", arrayOf(id.toString()))
        if (resultado == 0) {
            return "0"
        }
        else {
            return "1"
        }
    }

    // Metodo para inscribir al no socio en la tabla clienactv
    fun InscripcionNoSocio (idC : Int, idA: Int, fecIn: LocalDate) : Long {
        val db = this.writableDatabase
        var valores = ContentValues().apply {
            put("idCliente", idC)
            put("idActividad", idA)
            put("fechaInscripcion", fecIn.toString())
        }
        // Insertamos los valores en la tabla "cuotas"
        return db.insert("clienactiv", null, valores).also { db.close() }
    }

    // Metodo para obtener actividades con cupo disponible
    fun ObtenerActividadesDisponibles (id : Int): MutableList<ActivityModel> {
        val db = this.readableDatabase
        val query = """
            SELECT a.nombreA, a.idActividad, a.precio 
            FROM actividades AS a LEFT JOIN clienactiv AS ca 
                ON a.idActividad = ca.idActividad 
                AND ca.idCliente = ?
                AND DATE(ca.fechaInscripcion) = DATE('now')
            WHERE a.estadoA = 1 
            AND a.cupoDisp > 0
            AND ca.idActividad IS NULL
        """
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query,selectionArgs)
        val listaActividades = mutableListOf<ActivityModel>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val nombreAct = cursor.getString(cursor.getColumnIndexOrThrow("nombreA"))
                    val idAct = cursor.getInt(cursor.getColumnIndexOrThrow("idActividad")).toString()
                    val precioAct = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")).toString()
                    val actividad = ActivityModel(nombreAct, idAct, precioAct)
                    listaActividades.add(actividad)
                } while (cursor.moveToNext())
            }
        }
        catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener lista de actividades: ${e.message}") 
            e.printStackTrace()
        }
        finally {
            cursor.close()
            db.close()
        }
        return listaActividades
    }
}

