package ni.edu.uca.mediaupload

import com.google.gson.annotations.SerializedName

data class ImgFile(
    @SerializedName("name") val name: String?,
    @SerializedName("is_dir") val isDir: Boolean?
)