package ni.edu.uca.mediaupload

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("{fileName}")
    suspend fun uploadImage(@Path("fileName") fileName: String, @Body image: RequestBody): ResponseBody

    @GET(".")
    suspend fun getAllImages(): ImagesResponse
}