package org.dimchik.converter;

import org.dimchik.enums.SortDirection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortDirectionConverter implements Converter<String, SortDirection> {

    @Override
    public SortDirection convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        return SortDirection.from(source);
    }
}
