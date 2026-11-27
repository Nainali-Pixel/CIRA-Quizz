# ♻️ CIRA-Quizz

Aplicación móvil Android (Java) educativa del proyecto **C.I.R.A. — Centro Inteligente de Reciclaje Automático**.
Ayuda a aprender a clasificar residuos de forma entretenida: escaneando el código QR de los contenedores, respondiendo un quiz y compitiendo en un ranking de puntajes.

Los datos se guardan en la nube con **Firebase Cloud Firestore** y el acceso de administración usa **Firebase Authentication**.

---

## 📱 Funcionalidades

| Pantalla | Descripción | Firebase |
|---|---|---|
| **Menú principal** | Acceso a todas las secciones de la app | — |
| **Escanear QR** | Lee el QR del contenedor y muestra el residuo, su contenedor y un consejo | — |
| **Quiz** | 10 preguntas al azar (con imagen o teóricas) y 4 alternativas | Lee `preguntas` |
| **Quiz finalizado** | Muestra el puntaje, lo guarda con el nombre del jugador y muestra el Top 3 | Crea y lee `puntajes` |
| **Historial** | Lista de todos los puntajes guardados | Lee `puntajes` |
| **Opinión / Sugerencias** | Formulario para dejar comentarios | Crea `comentarios` |
| **Sobre nosotros** | Información del proyecto y del equipo | — |
| **Administración** | Registro, inicio y cierre de sesión. CRUD de preguntas, respuestas de participantes y comentarios | Authentication + CRUD |

---

## 🛠️ Tecnologías

- **Lenguaje:** Java
- **IDE:** Android Studio
- **SDK mínimo:** Android 12 (API 31) · **SDK objetivo:** API 36
- **Arquitectura:** MVVM (Model – View – ViewModel) + Repositorio
- **Base de datos:** Firebase Cloud Firestore
- **Autenticación:** Firebase Authentication (correo y contraseña)
- **Lector QR:** [zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded) 4.3.0

---

## 🗂️ Estructura del proyecto

```
app/src/main/java/com/devst/ciraquizz/
├── model/                  # Clases de datos (con constructor vacío para Firestore)
│   ├── Pregunta.java
│   ├── Puntaje.java
│   └── Residuo.java
├── repositorio/            # Comunicación con Firestore (CRUD)
│   ├── PreguntaRepositorio.java
│   └── PuntajeRepositorio.java
├── viewmodel/              # Lógica de cada pantalla y validaciones
│   ├── QuizViewModel.java
│   ├── ResultadoViewModel.java
│   └── HistorialViewModel.java
├── util/                   # Constantes y datos de apoyo
│   ├── Constantes.java
│   ├── CatalogoResiduos.java
│   └── PreguntasEjemplo.java
├── MenuActivity.java
├── EscanearQrActivity.java
├── QuizActivity.java
├── ResultadoActivity.java
└── HistorialActivity.java
```

**Flujo MVVM:** `Activity` (vista) → `ViewModel` (lógica y validaciones) → `Repositorio` (Firestore) → `LiveData` → la vista se actualiza sola.

---

## 🔥 Base de datos (Cloud Firestore)

Los nombres de las colecciones están en `util/Constantes.java`.

### `preguntas`
| Campo | Tipo | Descripción |
|---|---|---|
| `texto` | string | Enunciado de la pregunta |
| `opciones` | array (4 strings) | Alternativas A, B, C y D |
| `correcta` | number (0–3) | Posición de la alternativa correcta |
| `tipo` | string | `IMAGEN` o `TEORICA` |
| `imagen` | string | Emoji del residuo (vacío si es teórica) |
| `residuo` | string | Nombre corto del residuo |
| `creadoPor` | string | UID del administrador que la creó |

> La primera vez que se abre el Quiz, si la colección está vacía, se cargan automáticamente 12 preguntas de ejemplo.

### `puntajes`
| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | Nombre del jugador (2 a 20 letras) |
| `puntaje` | number | Respuestas correctas |
| `total` | number | Total de preguntas |
| `fecha` | timestamp | Fecha y hora del servidor |
| `respuestas` | array de mapas | `pregunta`, `respondio`, `correcta`, `acierto` |

### `comentarios`
| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | Nombre o "Anónimo" |
| `texto` | string | Comentario |
| `origen` | string | `QUIZ` o `MENU` |
| `fecha` | timestamp | Fecha y hora del servidor |

### `usuarios/{uid}`
| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | Nombre del administrador |
| `correo` | string | Correo registrado |
| `fechaRegistro` | timestamp | Fecha de registro |

> 🔒 Las **contraseñas nunca se guardan en Firestore**. Las administra Firebase Authentication.

---

## 🚀 Cómo ejecutar el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Nainali-Pixel/CIRA-Quizz.git
   ```
2. Abrir la carpeta en **Android Studio** y presionar **Trust Project**.
3. Verificar que exista `app/google-services.json` (conexión con el proyecto de Firebase `cira-quizz`).
4. Esperar el **Gradle Sync**.
5. Conectar un celular con Android 12 o superior (con depuración USB activada) o usar un emulador.
6. Presionar **▶️ Run**.

Para compilar desde la terminal:
```bash
.\gradlew assembleDebug
```

---

## 🌿 Ramas de trabajo

| Rama | Contenido |
|---|---|
| `main` | Versión estable |
| `firebase-base` | Conexión con Firebase (Firestore + Authentication) |
| `feature/menu-quiz` | Menú, Escanear QR, Quiz, Quiz finalizado e Historial |

Cada integrante trabaja en su propia rama creada desde `firebase-base`, y los cambios se unen a `main` con un **Pull Request**.

---

## 🧪 Códigos QR de prueba

El escáner reconoce estos códigos (texto del QR → residuo):

`CAJA_CEREAL` · `PAPEL_PERIODICO` · `CAJA_ZAPATOS` · `LATA_ALUMINIO` · `LATA_ATUN` · `LATA_REFRESCO` · `BOTELLA_REFRESCO` · `BOLSA_PLASTICO` · `TAPA_BOTELLA` · `CASCARA_PLATANO`

---

## 👥 Equipo

Proyecto del curso **Desarrollo de Aplicaciones Móviles** — Analista Programador, Instituto Profesional Santo Tomás.

- [NOMBRE INTEGRANTE 1:Benjamin Ahumada ]
- [NOMBRE INTEGRANTE 2:Milena Escobar]
- [NOMBRE INTEGRANTE 3:Yeimy Morales]