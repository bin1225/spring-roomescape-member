package roomescape.theme.mapper;

import roomescape.theme.domain.Theme;
import roomescape.theme.repository.entity.ThemeEntity;

public class ThemeMapper {

    private ThemeMapper() {
    }

    public static Theme toTheme(ThemeEntity themeEntity) {
        return Theme.createWithId(themeEntity.getId(), themeEntity.getName(), themeEntity.getDescription(),
                themeEntity.getImageUrl());
    }
}
