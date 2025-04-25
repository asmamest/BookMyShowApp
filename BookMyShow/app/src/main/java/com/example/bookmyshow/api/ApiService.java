package com.example.bookmyshow.api;

import com.example.bookmyshow.enums.CategoryEvent;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.models.BackendEventSchedule;
import com.example.bookmyshow.models.BackendLieu;

import java.time.LocalDateTime;
import java.util.List;




import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Endpoints pour les événements
    @GET("/api/events")
    Call<List<BackendEvent>> getAllEvents();

    @GET("/api/events/{id}")
    Call<BackendEvent> getEventById(@Path("id") Long id);

    @GET("/api/events/category/{category}")
    Call<List<BackendEvent>> getEventsByCategory(@Path("category") String category);

    @GET("/api/events/search")
    Call<List<BackendEvent>> searchEventsByTitle(@Query("title") String title);
    @POST("api/events")
    Call<BackendEvent> createEvent(@Body BackendEvent event);

    // Endpoints pour les horaires d'événements
    @GET("/api/event-schedules")
    Call<List<BackendEventSchedule>> getAllEventSchedules();

    @GET("/api/event-schedules/event/{eventId}")
    Call<List<BackendEventSchedule>> getEventSchedulesByEventId(@Path("eventId") Long eventId);

    @GET("/api/event-schedules/upcoming")
    Call<List<BackendEventSchedule>> getUpcomingEventSchedules();

    // Endpoints pour les lieux
    @GET("/api/lieux")
    Call<List<BackendLieu>> getAllLieux();

    @GET("/api/lieux/{id}")
    Call<BackendLieu> getLieuById(@Path("id") Long id);
    @PUT("api/events/{id}")
    Call<BackendEvent> updateEvent(@Path("id") Long id, @Body BackendEvent event);

    @DELETE("api/events/{id}")
    Call<Void> deleteEvent(@Path("id") Long id);

    // Endpoints pour les horaires d'événements

    @GET("api/event-schedules/{id}")
    Call<BackendEventSchedule> getEventScheduleById(@Path("id") Long id);


    @GET("api/event-schedules/lieu/{lieuId}")
    Call<List<BackendEventSchedule>> getEventSchedulesByLieuId(@Path("lieuId") Long lieuId);

    @GET("api/event-schedules/date-range")
    Call<List<BackendEventSchedule>> getEventSchedulesByDateRange(
            @Query("start") LocalDateTime start,
            @Query("end") LocalDateTime end);

    @POST("api/event-schedules")
    Call<BackendEventSchedule> createEventSchedule(@Body BackendEventSchedule eventSchedule);

    @PUT("api/event-schedules/{id}")
    Call<BackendEventSchedule> updateEventSchedule(@Path("id") Long id, @Body BackendEventSchedule eventSchedule);

    @DELETE("api/event-schedules/{id}")
    Call<Void> deleteEventSchedule(@Path("id") Long id);

    // Endpoints pour les lieux


    @GET("api/lieux/search")
    Call<List<BackendLieu>> searchLieuxByName(@Query("name") String name);

    @GET("api/lieux/capacity")
    Call<List<BackendLieu>> getLieuxByMinCapacity(@Query("capacity") int capacity);

    @POST("api/lieux")
    Call<BackendLieu> createLieu(@Body BackendLieu lieu);

    @PUT("api/lieux/{id}")
    Call<BackendLieu> updateLieu(@Path("id") Long id, @Body BackendLieu lieu);

    @DELETE("api/lieux/{id}")
    Call<Void> deleteLieu(@Path("id") Long id);
}
