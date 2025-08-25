package org.dimchik.service.base;

import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.entity.Movie;
import org.dimchik.exception.InternalServerException;
import org.dimchik.exception.NotFoundException;
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

    public MovieServiceBase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<MovieResponseDTO> findAll(HashMap<String, String> filter) {
        List<MovieResponseDTO> movieResponseDTOList;

        try {
            Sort sort = Sort.unsorted();
            if (filter.containsKey("rating")) {
                sort = sort.and(MovieSortSpecification.sortByRating(filter.get("rating")));
            }
            if (filter.containsKey("price")) {
                sort = sort.and(MovieSortSpecification.sortByPrice(filter.get("price")));
            }

            if (sort.isUnsorted()) {
                sort = Sort.by("id").ascending();
            }

            List<Movie> movieList = movieRepository.findAll(sort);

            if (!movieList.isEmpty()) {
                movieResponseDTOList = movieList.stream()
                        .map(MovieRowMapper::convertToDTO)
                        .toList();
            } else {
                movieResponseDTOList = Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Exception occurred while retrieving movies from database , Exception message {}", e.getMessage());
            throw new InternalServerException("Exception occurred while fetch all movies from Database");
        }

        return movieResponseDTOList;
    }

    @Override
    public List<MovieResponseDTO> random(int count) {
        List<MovieResponseDTO> movieResponseDTOList;

        try {
            List<Movie> movieList = movieRepository.findAll();
            if (!movieList.isEmpty()) {
                Collections.shuffle(movieList);
                movieResponseDTOList = movieList.stream()
                        .map(MovieRowMapper::convertToDTO)
                        .limit(count)
                        .toList();
            } else {
                movieResponseDTOList = Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Exception occurred while retrieving random movies from database , Exception message {}", e.getMessage());
            throw new InternalServerException("Exception occurred while fetch multiple random movies from Database");
        }

        return movieResponseDTOList;
    }

    @Override
    public MovieResponseDTO findById(long id) {
        MovieResponseDTO movieResponseDTO;

        try {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));

            movieResponseDTO = MovieRowMapper.convertToDTO(movie);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving movie {} from database , Exception message {}", id, e.getMessage());
            throw new InternalServerException("Exception occurred while fetch movie from Database " + id);
        }

        return movieResponseDTO;
    }

    @Override
    public List<MovieResponseDTO> findByGenreId(long genreId) {
        List<MovieResponseDTO> movieResponseDTOList;

        try {
            List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);
            if (!movieList.isEmpty()) {
                movieResponseDTOList = movieList.stream()
                        .map(MovieRowMapper::convertToDTO)
                        .toList();
            } else {
                movieResponseDTOList = Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Exception occurred while retrieving movies from database , Exception message {}", e.getMessage());
            throw new InternalServerException("Exception occurred while fetch all movies from Database");
        }

        return movieResponseDTOList;
    }
}
