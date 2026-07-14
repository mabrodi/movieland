package org.dimchik.web.controller;

import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.dto.response.UserResponse;
import org.dimchik.security.AuthFilter;
import org.dimchik.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest extends AbstractBaseTest {
    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private AuthFilter authFilter;

    private String reviewCreateRequestJson;
    private String reviewCreateResponseJson;

    @BeforeEach
    void setUp() {
        reviewCreateRequestJson = readJson("reviewCreateRequest.json");
        reviewCreateResponseJson = readJson("reviewCreateResponse.json");
    }

    @Test
    @WithMockUser(authorities = "USER")
    void createShouldReturnReviewWhenAuthorized() throws Exception {
        var reviewResponse = new ReviewResponse(
                10L, "Отличный фильм", new UserResponse(1L, "Test User"));
        when(reviewService.create(any(), any())).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewCreateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(reviewCreateResponseJson));

        verify(reviewService).create(any(), any());
    }
}
