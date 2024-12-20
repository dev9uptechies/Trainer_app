package com.example

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

open class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val responseListener: Response.Listener<String>,
    private val errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    private val params: MutableMap<String, String> = HashMap()

    private var imageFile: File? = null
    private var imageParamName: String? = null
    private var imageMimeType: String? = null

    // To add additional parameters to the request
    fun addStringParam(key: String, value: String) {
        params[key] = value
    }

    // To add the image file to the multipart body
    fun addFileParam(paramName: String, file: File, mimeType: String) {
        imageParamName = paramName
        imageFile = file
        imageMimeType = mimeType
    }

    override fun getParams(): Map<String, String> {
        return params
    }

    // Method to prepare the body of the request as a byte array
    override fun getBody(): ByteArray {
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)

        // Add string parameters to the body
        for ((key, value) in params) {
            multipartBody.addFormDataPart(key, value)
        }

        // Add image file to the body if provided
        if (imageFile != null && imageMimeType != null) {
            val requestBody = RequestBody.create(imageMimeType!!.toMediaTypeOrNull(), imageFile!!)
            multipartBody.addFormDataPart(imageParamName.toString(), imageFile!!.name, requestBody)
        }

        // Build the final body
        val requestBody = multipartBody.build()

        // Use Buffer as the required BufferedSink
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bufferedSink = Buffer().writeTo(byteArrayOutputStream)  // Wrap the ByteArrayOutputStream in a Buffer
        requestBody.writeTo(bufferedSink)

        return byteArrayOutputStream.toByteArray()
    }

    // Parse the network response
    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return if (response != null) {
            // Parse the response body as a String (you can also use JSON here depending on your API response)
            val responseString = String(response.data)
            // Return a success response
            Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
        } else {
            // Return an error response if the response is null
            Response.error(ParseError())
        }
    }

    // Override to add headers if needed
    @Throws(AuthFailureError::class)
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        // Set your headers if needed (e.g., Authorization, Content-Type)
        return headers
    }

    // Deliver the response to the listener
    @Throws(VolleyError::class)
    override fun deliverResponse(response: String) {
        responseListener.onResponse(response)
    }
}

// Function to upload a file using the custom VolleyMultipartRequest class
fun uploadFile(context: Context, url: String, uri: Uri) {
    val file = File(uri.path) // Get the file from the URI
    val queue = Volley.newRequestQueue(context)

    val params = HashMap<String, String>()
    params["name"] = "example_name"
    params["description"] = "example_description"

    // Initialize the custom request
    val request = object : VolleyMultipartRequest(
        Method.POST, url,
        Response.Listener { response ->
            Log.d("UploadSuccess", "Response: $response")
        },
        Response.ErrorListener { error ->
            Log.e("UploadError", "Error: ${error.message}")
        }) {

        init {
            // Add form parameters
            params.forEach { (key, value) -> addStringParam(key, value) }

            // Add the file parameter
            addFileParam("image", file, "image/jpeg")
        }
    }

    // Add request to queue
    queue.add(request)
}

// Function to convert URI to File (for content URIs)
fun getFileFromUri(context: Context, uri: Uri): File? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndex("_data")
    val filePath = cursor?.getString(columnIndex ?: -1)
    cursor?.close()
    return if (filePath != null) File(filePath) else null
}
