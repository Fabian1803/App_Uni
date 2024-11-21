package com.example.appuni.data.Retrofit

import com.example.appuni.data.entities.Course
import com.example.appuni.data.entities.Messages
import com.example.appuni.data.entities.Student
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/authenticate")
    fun authenticateStudent(@Field("code") code: String, @Field("password") password: String): Call<Student>

    @GET("/courses/{studentId}")
    fun getCourses(@Path("studentId") studentId: Long): Call<List<Course>>

    @GET("/students/search")
    fun searchStudents(@Query("query") query: String): Call<List<Student>>

    @POST("/messages/send")
    fun sendMessage(@Body message: Messages): Call<Messages>

    @GET("/messages/{senderId}/{receiverId}")
    fun getMessagesBetween(@Path("senderId") senderId: Long, @Path("receiverId") receiverId: Long): Call<List<Messages>>

    @POST("/verify-email")
    fun verifyEmail(@Query("email") email: String): Call<Student>
}
