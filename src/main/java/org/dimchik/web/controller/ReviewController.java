package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.service.ReviewService;
import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.dto.request.CreateReviewRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping
    public ReviewResponse create(@RequestBody @Valid CreateReviewRequest request, @AuthenticationPrincipal UserDetails user) {
        return reviewService.create(request, user);
    }
}
