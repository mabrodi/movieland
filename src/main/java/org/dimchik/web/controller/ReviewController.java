package org.dimchik.web.controller;

import jakarta.persistence.PrePersist;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.ReviewDTO;
import org.dimchik.service.ReviewService;
import org.dimchik.web.request.CreateReviewRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping
    public ReviewDTO create(@RequestBody @Valid CreateReviewRequest request, @AuthenticationPrincipal UserDetails user) {
        return reviewService.create(request, user);
    }
}
