package org.dimchik.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GenreControllerITest extends AbstractBaseITest {
    //ENDPOINTS
    private static final String GENRES_API_URL = "/api/v1/genres";

    //RESPONSE JSON
    private static final String FIND_ALL_GENRES_JSON = "response/genre/find-all.json";

    @Test
    @DataSet(value = "datasets/genres.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/genres.yml")
    void findAllShouldReturnGenreList() throws Exception {

        SQLStatementCountValidator.reset();
        mockMvc.perform(get(GENRES_API_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(FIND_ALL_GENRES_JSON)));

        assertSelectCount(1);
    }
}
