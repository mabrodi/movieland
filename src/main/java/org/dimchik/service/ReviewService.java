package org.dimchik.service;

import org.dimchik.common.request.CreateReviewRequest;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO create(CreateReviewRequest request, AuthSessionDTO currentUser);
}
