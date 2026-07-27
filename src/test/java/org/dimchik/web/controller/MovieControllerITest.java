package org.dimchik.web.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.dimchik.dto.RateDTO;
import org.dimchik.enums.SortDirection;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.service.cache.MovieRatingCacheService;
import org.dimchik.service.cache.RateCacheService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MovieControllerITest extends AbstractBaseITest {
    @MockitoBean
    private RateCacheService rateCacheService;

    @Autowired
    private MovieCacheService movieCacheService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MovieRatingCacheService movieRatingCacheService;

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

    @BeforeEach
    void setUp() {
        Mockito.when(rateCacheService.findAll())
                .thenReturn(List.of(
                        new RateDTO(840, "Долар США", "USD", 44.8),
                        new RateDTO(978, "Євро", "EUR", 50.9)
                ));
    }

    @Test
    @DataSet(value = "datasets/movies.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies.yml")
    void findAllShouldReturnMovieList() throws Exception {
        SQLStatementCountValidator.reset();

        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(get(MOVIES_API_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_MOVIES_JSON)));

        movieRatingCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movies.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies.yml")
    void findAllShouldReturnMovieListFromQueryParams() throws Exception {
        SQLStatementCountValidator.reset();

        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(get(MOVIES_API_URL)
                        .queryParam("ratingSortDirection", SortDirection.ASC.name())
                        .queryParam("priceSortDirection",  SortDirection.DESC.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_MOVIES_JSON)));

        movieRatingCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movie.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movie.yml")
    void findByIdShouldReturnMovie() throws Exception {

        SQLStatementCountValidator.reset();
        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(get(MOVIES_API_URL + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_BY_ID_MOVIE_JSON)));

        movieRatingCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movie.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movie.yml")
    void findByIdShouldReturnMovieFromRates() throws Exception {

        SQLStatementCountValidator.reset();

        movieRatingCacheService.fillMovieRatingCache();

        for (RateDTO rateDTO : rateCacheService.findAll()) {
            mockMvc.perform(get(MOVIES_API_URL + "/1")
                            .queryParam("currency", rateDTO.getCurrency())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            readJsonObjectWithCurrencyRate(FIND_BY_ID_MOVIE_JSON, BigDecimal.valueOf(rateDTO.getRate()))));
        }


        movieRatingCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movies.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies.yml")
    void findRandomShouldReturnMovieRandomList() throws Exception {

        SQLStatementCountValidator.reset();
        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(get(MOVIES_API_URL + "/random").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_MOVIES_JSON)));

//        assertSelectCount(1);

        movieRatingCacheService.clear();
    }

    @Test
    @DataSet(value = "datasets/movies_by_genre_id.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/movies_by_genre_id.yml")
    void findByGenreIdShouldReturnMoviesByGenreId() throws Exception {

        SQLStatementCountValidator.reset();
        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(get(MOVIES_API_URL + "/genre/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_BY_GENRE_ID_MOVIE_JSON)));

        movieRatingCacheService.clear();
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

        movieRatingCacheService.fillMovieRatingCache();
        mockMvc.perform(put(MOVIES_API_URL + "/1")
                        .content(readJson(REQUEST_UPDATE_MOVIE_JSON))
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(UPDATE_MOVIE_JSON)));

        movieRatingCacheService.clear();
    }

    private String readJsonObjectWithCurrencyRate(String jsonPath, BigDecimal rate) {
        try {
            Map<String, Object> movie =
                    objectMapper.readValue(
                            readJson(jsonPath), new TypeReference<>() {});

            BigDecimal price = new BigDecimal(movie.get("price").toString());
            movie.put("price", price.multiply(rate).setScale(2, RoundingMode.HALF_UP));

            return objectMapper.writeValueAsString(movie);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
