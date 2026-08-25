package mx.edu.sabores.data

import retrofit2.http.GET

/** Contrato preparado para una fase futura. El MVP usa LocalDishRepository y no requiere servidor. */
interface RemoteDishApi { @GET("dishes") suspend fun getDishes(): List<RemoteDishDto> }
data class RemoteDishDto(val id: String, val name: String, val region: String)
