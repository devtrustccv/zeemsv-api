package cv.zeemsv.api.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultStatusApp {
    ATIVO("ATIVO"),
    INATIVO("INATIVO");

    private final String key;
}
