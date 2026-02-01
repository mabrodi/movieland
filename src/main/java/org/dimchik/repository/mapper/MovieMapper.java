package org.dimchik.repository.mapper;

import org.dimchik.dto.*;
import org.dimchik.entity.Movie;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {
    @Mapping(target = "picturePath", source = "poster.picturePath")
    MovieDTO toDto(Movie movie);

    @Mapping(target = "picturePath", source = "poster.picturePath")
    MovieFullDTO toFullDto(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "poster", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "countries", ignore = true)
    Movie toEntity(CreateMovieRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "poster", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "countries", ignore = true)
    void updateMovieFromRequest(UpdateMovieRequest request, @MappingTarget Movie movie);
}
