package org.dimchik.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.service.ReviewService;
import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.dto.request.CreateReviewRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Tag(name="${swagger.reviews.tag.name}", description = "${swagger.reviews.tag.description}")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "${swagger.reviews.create.summary}", description = "${swagger.reviews.create.description}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    public ReviewResponse create(@RequestBody @Valid CreateReviewRequest request, @AuthenticationPrincipal JwtUserDetails user) {
        return reviewService.create(request, user);
    }
}
