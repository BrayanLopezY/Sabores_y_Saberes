package mx.edu.sabores.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Comunicación mínima entre la app móvil y la app de TV.
 *
 * Durante las pruebas con dos emuladores de Android Studio, 10.0.2.2 apunta
 * a la computadora anfitriona. En esa computadora se ejecuta sync_server.py.
 */
data class TvSelectionRequest(
    val dishId: String
)

data class TvSelectionResponse(
    val dishId: String? = null,
    val version: Long = 0
)

data class TvSelectionAck(
    val ok: Boolean = false,
    val dishId: String? = null,
    val version: Long = 0
)

interface TvSyncApi {
    @POST("selection")
    suspend fun selectDish(@Body request: TvSelectionRequest): TvSelectionAck

    @GET("selection")
    suspend fun getSelection(): TvSelectionResponse
}

object TvSyncClient {
    // Dirección especial del emulador Android para acceder al localhost de Windows/macOS/Linux.
    const val DEFAULT_BASE_URL = "http://10.0.2.2:8765/"

    fun create(baseUrl: String = DEFAULT_BASE_URL): TvSyncApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TvSyncApi::class.java)
}
