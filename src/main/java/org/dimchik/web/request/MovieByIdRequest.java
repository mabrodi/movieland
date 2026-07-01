package org.dimchik.web.request;

import lombok.Getter;
import lombok.Setter;
import org.dimchik.enums.Currency;

@Setter
@Getter
public class MovieByIdRequest {
    private Currency currency = Currency.UAH;

}
