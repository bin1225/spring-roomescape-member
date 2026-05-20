package roomescape.util.fixture;

import java.util.concurrent.atomic.AtomicLong;
import roomescape.theme.domain.Theme;

public class ThemeFixture {

    private static final AtomicLong idSequence = new AtomicLong(1L);

    public static Theme createDefault() {
        return Theme.createWithId(idSequence.getAndIncrement(), "default", "default", "/image/...");
    }

    public static Theme createByIdAndName(Long id, String name) {
        return Theme.createWithId(id, name, "default", "/image/...");
    }
}
