package org.dimchik.mapper;

import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    @Mapping(target = "user", source = "author")
    ReviewResponse toResponse(Review review);
}
