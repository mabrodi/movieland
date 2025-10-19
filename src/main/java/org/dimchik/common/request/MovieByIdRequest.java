package org.dimchik.common.request;

import lombok.Getter;
import lombok.Setter;
import org.dimchik.common.enums.Currency;

@Setter
@Getter
public class MovieByIdRequest {
    private Currency currency = Currency.UAH;

}
