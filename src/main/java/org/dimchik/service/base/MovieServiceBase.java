package org.dimchik.service.base;

import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.entity.Movie;
import org.dimchik.exception.ResourceNotFoundException;
import org.dimchik.repository.MovieRepository;
import org.dimchik.mapper.MovieRowMapper;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.dimchik.service.MovieService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MovieServiceBase implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieRowMapper movieRowMapper;

    public MovieServiceBase(MovieRepository movieRepository, MovieRowMapper movieRowMapper) {
        this.movieRepository = movieRepository;
        this.movieRowMapper = movieRowMapper;
    }

    @Override
    public List<MovieResponseDTO> findAll(HashMap<String, String> filter) {
        Sort sort = buildSortFromFilter(filter);
        List<Movie> movieList = movieRepository.findAll(sort);

        return convertToDtoList(movieList);
    }

    @Override
    public List<MovieResponseDTO> random(int count) {
        List<MovieResponseDTO> movieResponseDTOList;

        List<Movie> movieList = movieRepository.findAll();
        if (!movieList.isEmpty()) {
            Collections.shuffle(movieList);
            movieResponseDTOList = movieList.stream()
                    .map(movieRowMapper::convertToDTO)
                    .limit(count)
                    .toList();
        } else {
            movieResponseDTOList = Collections.emptyList();
        }

        return movieResponseDTOList;
    }

    @Override
    public MovieResponseDTO findById(long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        return movieRowMapper.convertToDTO(movie);
    }

    @Override
    public List<MovieResponseDTO> findByGenreId(long genreId) {
        List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);

        return convertToDtoList(movieList);
    }

    private Sort buildSortFromFilter(Map<String, String> filter) {
        Sort sort = Sort.unsorted();

        if (filter.containsKey("rating")) {
            sort = sort.and(MovieSortSpecification.sortByRating(filter.get("rating")));
        }
        if (filter.containsKey("price")) {
            sort = sort.and(MovieSortSpecification.sortByPrice(filter.get("price")));
        }

        return sort.isUnsorted() ? Sort.by("id").ascending() : sort;
    }

    private List<MovieResponseDTO> convertToDtoList(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movieRowMapper::convertToDTO)
                .toList();
    }
}
