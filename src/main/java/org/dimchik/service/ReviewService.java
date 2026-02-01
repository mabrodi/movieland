package org.dimchik.service;

import org.dimchik.dto.ReviewDTO;
import org.dimchik.web.request.CreateReviewRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReviewService {
    ReviewDTO create(CreateReviewRequest request, UserDetails user);
}
