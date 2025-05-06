package com.example.animerecs.api;

import com.example.animerecs.api.model.AnimeResponse;
import com.example.animerecs.api.model.MangaResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JikanApi {
    @GET("anime")
    Call<AnimeResponse> getTopAnime(
        @Query("page") int page,
        @Query("limit") int limit,
        @Query("order_by") String orderBy,
        @Query("sort") String sort,
        @Query("type") String type
    );
    
    @GET("anime")
    Call<AnimeResponse> searchAnime(
        @Query("q") String query,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("anime/{id}")
    Call<AnimeResponse> getAnimeDetails(@Path("id") int animeId);
    
    @GET("manga")
    Call<MangaResponse> getTopManga(
        @Query("page") int page,
        @Query("limit") int limit,
        @Query("order_by") String orderBy,
        @Query("sort") String sort,
        @Query("type") String type
    );
    
    @GET("manga")
    Call<MangaResponse> searchManga(
        @Query("q") String query,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("manga/{id}")
    Call<MangaResponse> getMangaDetails(@Path("id") int mangaId);
} 