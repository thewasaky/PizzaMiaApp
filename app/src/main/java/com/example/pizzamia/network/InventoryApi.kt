package com.example.pizzamia.network
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


data class AssignTagRequest(
    val id: Int?,
    val numParte: String,
    val lote: String,
    val cantidad: Double,
    val Etiqueta: String,
    val usuario: String?
)
data class AssignTagResponse(
    val data:result,
    val mensaje:String?,
    val isError:Boolean?
)
data class GenericResponse(
    val mensaje:String?,
    val isError:Boolean?
)
data class AssignTagResponseNoList(
    val data:AssignTagRequest,
    val mensaje:String?,
    val isError:Boolean?
)

data class result(
    val Data:List<AssignTagRequest>,
    val TotalRecords:Int,
    val Offset:Int?,
    val Limit:Int?
)

interface InventoryApi {
    @POST("Inventario")
    suspend fun assignTag(@Body request: AssignTagRequest): retrofit2.Response<AssignTagResponse>
}

interface InventoryListApi{
    @GET("Inventario")
    suspend fun getRegisteredTags(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<AssignTagResponse> // Cambiado a una lista de respuestas
}

interface  InventoryUpdateApi{
    @PUT("Inventario/{id}")
    suspend fun updateQuantity(@Path("id") id: String,@Body request: AssignTagRequest): retrofit2.Response<AssignTagResponse>

}

interface InventoryReasignTagApi{
    @PUT("Inventario/Reasignar")
    suspend fun reassignTag(@Body request: AssignTagRequest): retrofit2.Response<AssignTagResponseNoList>
}

interface InventorySendEmailApi{
    @GET("Inventario/EnviarCorreo")
    suspend fun sendEmail(): retrofit2.Response<GenericResponse>
}