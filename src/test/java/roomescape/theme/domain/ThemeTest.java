package roomescape.theme.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.global.exception.validation.InvalidIdException;
import roomescape.util.fixture.ThemeFixture;

class ThemeTest {

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void 식별자가_0보다_작은_경우_예외가_발생한다(Long id) {
        // when & then
        Assertions.assertThatThrownBy(() -> Theme.createWithId(id, "hello", "world", "/resources/image/..."))
                .isInstanceOf(InvalidIdException.class);
    }

    @Test
    void 식별자가_null_인_경우_예외가_발생한다() {
        //given
        Long id = null;

        // when & then
        Assertions.assertThatThrownBy(() -> Theme.createWithId(id, "hello", "world", "/resources/image/..."))
                .isInstanceOf(InvalidIdException.class);
    }

    @Test
    void 식별자가_없는_상태의_객체를_생성한다() {
        // when
        Theme theme = Theme.create("hello", "world", "/resources/image/...");

        // then
        Assertions.assertThat(theme.getId()).isNull();
    }

    @Test
    void 식별자가_같은_경우_동등한_객체로_판단한다() {
        // given
        Theme themeA = ThemeFixture.createByIdAndName(1L, "A");
        Theme themeB = ThemeFixture.createByIdAndName(1L, "B");

        // when & then
        Assertions.assertThat(themeA).isEqualTo(themeB);
    }
}
