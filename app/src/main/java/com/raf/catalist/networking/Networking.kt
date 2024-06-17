import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.raf.catalist.R
import com.raf.catalist.networking.serialization.AppJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import io.github.cdimascio.dotenv.dotenv


/*
 * Order of okhttp interceptors is important. If logging was first,
 * it would not log the custom header.
 */


val dotenv = dotenv {
    directory = "/assets"
    filename = "env" // instead of '.env', use 'env'
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val updatedRequest = it.request().newBuilder()
            .addHeader("x-api-key", dotenv["API_KEY"])
            .build()
        it.proceed(updatedRequest)
    }
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    )
    .build()


val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.thecatapi.com/v1/")
    .client(okHttpClient) // Pass the context here
    .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
    .build()
