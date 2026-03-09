package db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    private String column;
    private Object value;
    private String operator;


    public static Condition equalTo(String column, Object value) {
        return new Condition(column, value, "=");
    }

    public static Condition notEqualTo(String column, Object value) {
        return new Condition(column, value, "!=");
    }

    public static Condition like(String column, String value) {
        return new Condition(column, value, "LIKE");
    }
}
