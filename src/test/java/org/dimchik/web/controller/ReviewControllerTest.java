package org.dimchik.web.controller;

import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.dto.response.UserResponse;
import org.dimchik.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "security.jwt.secret=0123456789abcdef0123456789abcdef",
        "security.jwt.ttl-seconds=900",
        "security.jwt.blacklist.cleanup-cron=0 * * * * *"
})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @WithMockUser(authorities = "USER")
    void createShouldReturnReviewWhenAuthorized() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Test User");
        ReviewResponse reviewResponse = new ReviewResponse(10L, "Отличный фильм", userResponse);
        when(reviewService.create(any(), any())).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"movieId": 1, "text": "Отличный фильм"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.comment").value("Отличный фильм"));

        verify(reviewService).create(any(), any());
    }
}
