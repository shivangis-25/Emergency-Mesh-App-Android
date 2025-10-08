package com.example.emergnecymesh.api



import retrofit2.Response
import retrofit2.http.*

/**
 * API endpoints for backend communication
 */
interface ApiService {

    @POST("api/emergency-alerts")
    suspend fun sendEmergencyAlert(
        @Body request: EmergencyAlertRequest
    ): Response<ApiResponse<Any>>

    @POST("api/mesh-messages")
    suspend fun syncMeshMessage(
        @Body request: MeshMessageRequest
    ): Response<ApiResponse<Any>>

    @POST("api/mesh-messages/batch")
    suspend fun syncMeshMessagesBatch(
        @Body requests: List<MeshMessageRequest>
    ): Response<ApiResponse<SyncStatus>>

    @GET("api/health")
    suspend fun healthCheck(): Response<ApiResponse<String>>

    @GET("api/mesh-messages/device/{deviceId}")
    suspend fun getDeviceMessages(
        @Path("deviceId") deviceId: String
    ): Response<ApiResponse<List<MeshMessageRequest>>>
}