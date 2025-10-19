package org.dimchik.common.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateMovieRequest {
    private String nameRussian;
    private String nameNative;
    private String picturePath;
    private List<Long> countries;
    private List<Long> genres;
}
