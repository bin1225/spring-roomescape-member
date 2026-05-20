package roomescape.theme.domain;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.exception.validation.InvalidIdException;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Theme {

    @EqualsAndHashCode.Include
    private final Long id;
    private final String name;
    private final String description;
    private final String imageUrl;

    private Theme(Long id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static Theme create(String name, String description, String imageUrl) {
        return new Theme(null, name, description, imageUrl);
    }

    public static Theme createWithId(Long id, String name, String description, String imageUrl) {
        validateId(id);
        return new Theme(id, name, description, imageUrl);
    }

    public Theme withId(Long id) {
        validateId(id);
        return new Theme(id, name, description, imageUrl);
    }

    private static void validateId(Long id) {
        if (Objects.isNull(id) || id <= 0) {
            throw new InvalidIdException(id);
        }
    }
}

