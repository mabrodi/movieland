package org.dimchik.mapper;

import org.dimchik.dto.response.GenreResponse;
import org.dimchik.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenreMapper {
    List<GenreResponse> toResponseList(List<Genre> genre);
}
