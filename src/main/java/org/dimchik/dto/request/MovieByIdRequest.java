package org.dimchik.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.dimchik.enums.Currency;

@Setter
@Getter
public class MovieByIdRequest {
    private Currency currency = Currency.UAH;

}
