package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemChangeDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemChangeDtoJsonTest {
    private final JacksonTester<ItemChangeDto> json;

    @Test
    void testItemChangeDto() throws Exception {
        ItemChangeDto itemDto = ItemChangeDto.builder()
                .name("Chainsaw")
                .description("Very cool chainsaw")
                .isAvailable(true)
                .build();

        JsonContent<ItemChangeDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Chainsaw");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Very cool chainsaw");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }
}
