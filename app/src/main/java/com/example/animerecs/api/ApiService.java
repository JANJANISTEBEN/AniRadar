package com.example.animerecs.api;

import com.example.animerecs.api.model.AnimeListResponse;
import com.example.animerecs.api.model.AnimeResponse;
import com.example.animerecs.api.model.MangaListResponse;
import com.example.animerecs.api.model.MangaResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    @GET("anime/{id}")
    Call<AnimeResponse> getAnimeById(@Path("id") String id);
    
    @GET("manga/{id}")
    Call<MangaResponse> getMangaById(@Path("id") String id);
    
    @GET("top/anime")
    Call<AnimeListResponse> getTopAnime(
            @Query("page") int page, 
            @Query("limit") int limit,
            @Query("type") String type);
    
    @GET("anime")
    Call<AnimeListResponse> searchAnime(
            @Query("q") String query, 
            @Query("page") int page, 
            @Query("limit") int limit);
    
    @GET("anime")
    Call<AnimeListResponse> filterAnime(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("type") String type,
            @Query("status") String status,
            @Query("min_score") Float minScore,
            @Query("max_score") Float maxScore,
            @Query("genres") String genres,
            @Query("order_by") String orderBy,
            @Query("sort") String sort);
    
    @GET("top/manga")
    Call<MangaListResponse> getTopManga(@Query("page") int page);
    
    @GET("manga")
    Call<MangaListResponse> searchManga(
            @Query("q") String query, 
            @Query("page") int page);
    
    @GET("manga")
    Call<MangaListResponse> getMangaWithFilter(
            @Query("type") String type,
            @Query("status") String status,
            @Query("min_score") int minScore,
            @Query("max_score") int maxScore,
            @Query("genres") String genres,
            @Query("order_by") String orderBy,
            @Query("sort") String sort,
            @Query("page") int page);
}