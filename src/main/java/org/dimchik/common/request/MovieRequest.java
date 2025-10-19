package org.dimchik.common.request;

import lombok.Getter;
import org.dimchik.common.enums.SortDirection;

@Getter
public class MovieRequest {
    private SortDirection ratingSortDirection = SortDirection.DESC;
    private SortDirection priceSortDirection;

    public void setRatingSortDirection(String direction) {
        if (direction != null) {
            this.ratingSortDirection = SortDirection.valueOf(direction.toUpperCase());
        }
    }

    public void setPriceSortDirection(String direction) {
        if (direction != null) {
            this.priceSortDirection = SortDirection.valueOf(direction.toUpperCase());
        }
    }
}
