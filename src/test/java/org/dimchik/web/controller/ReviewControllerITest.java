package org.dimchik.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerITest extends AbstractBaseITest {
    //ENDPOINTS
    private static final String REVIEWS_API_URL = "/api/v1/reviews";

    //RESPONSE JSON
    private static final String CREATE_REVIEW_JSON = "response/review/create.json";

    //REQUEST JSON
    private static final String REQUEST_CREATE_REVIEW_JSON = "request/review/create.json";

    @Test
    @DataSet(
            value = "datasets/create_review_before.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/create_review_after.yml")
    void createMovieReturnsStatusOkAndReview() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(post(REVIEWS_API_URL)
                        .content(readJson(REQUEST_CREATE_REVIEW_JSON))
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJson(CREATE_REVIEW_JSON)));
    }
}
