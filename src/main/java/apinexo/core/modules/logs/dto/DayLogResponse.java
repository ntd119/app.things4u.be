package apinexo.core.modules.logs.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayLogResponse {

    private List<String> days;

    private List<Long> values;

    private List<Long> errors;

    public DayLogResponse(List<String> days, List<Long> values, List<Long> errors) {
        this.days = days;
        this.values = values;
        this.errors = errors;
    }
}
