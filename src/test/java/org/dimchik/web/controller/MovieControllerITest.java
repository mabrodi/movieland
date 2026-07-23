package org.dimchik.web.controller;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.dimchik.service.cache.MovieCacheService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MovieControllerITest extends AbstractBaseITest {
    @Autowired
    private MovieCacheService movieCacheService;

    //ENDPOINTS
    private static final String MOVIES_API_URL = "/api/v1/movies";

    //RESPONSE JSON
    private static final String FIND_ALL_MOVIES_JSON = "response/movie/find-all.json";
    private static final String FIND_BY_ID_MOVIE_JSON = "response/movie/find-by-id.json";
    private static final String FIND_BY_GENRE_ID_MOVIE_JSON = "response/movie/find-by-genre-id.json";
    private static final String CREATE_MOVIE_JSON = "response/movie/create.json";
    private static final String UPDATE_MOVIE_JSON = "response/movie/update.json";

    //REQUEST JSON
    private static final String REQUEST_CREATE_MOVIE_JSON = "request/movie/create.json";
    private static final String REQUEST_UPDATE_MOVIE_JSON = "request/movie/update.json";


    @AfterEach
    void clearCache() {
        movieCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movies.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies.yml")
    void findAllShouldReturnMovieList() throws Exception {

        SQLStatementCountValidator.reset();
        mockMvc.perform(get(MOVIES_API_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_MOVIES_JSON)));

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/movie.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movie.yml")
    void findByIdShouldReturnMovie() throws Exception {

        SQLStatementCountValidator.reset();
        mockMvc.perform(get(MOVIES_API_URL + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_BY_ID_MOVIE_JSON)));

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/movies.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies.yml")
    void findRandomShouldReturnMovieRandomList() throws Exception {

        SQLStatementCountValidator.reset();
        mockMvc.perform(get(MOVIES_API_URL + "/random").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_MOVIES_JSON)));

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/movies_by_genre_id.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies_by_genre_id.yml")
    void findByGenreIdShouldReturnMoviesByGenreId() throws Exception {

        SQLStatementCountValidator.reset();
        mockMvc.perform(get(MOVIES_API_URL + "/genre/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_BY_GENRE_ID_MOVIE_JSON)));

        assertSelectCount(1);
    }

    @Test
    @DataSet(
            value = "datasets/create_movie_before.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/create_movie_after.yml")
    void createMovieReturnsStatusOkAndMovie() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(post(MOVIES_API_URL)
                        .content(readJson(REQUEST_CREATE_MOVIE_JSON))
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(CREATE_MOVIE_JSON)));
    }

    @Test
    @DataSet(
            value = "datasets/update_movie_before.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/update_movie_after.yml")
    void updateMovieReturnsStatusOkAndMovie() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(put(MOVIES_API_URL + "/1")
                        .content(readJson(REQUEST_UPDATE_MOVIE_JSON))
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(UPDATE_MOVIE_JSON)));
    }
}
