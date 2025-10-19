package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.common.request.CreateReviewRequest;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.dto.ReviewDTO;
import org.dimchik.dto.UserAuthDTO;
import org.dimchik.service.ReviewService;
import org.dimchik.web.security.annotation.CurrentUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDTO create(@RequestBody @Valid CreateReviewRequest request, @CurrentUser AuthSessionDTO currentUser) {
        return reviewService.create(request, currentUser);
    }
}
