package api.steps;

import api.dto.admin.CreateUserRequest;
import api.generators.DTOGenerator;
import api.generators.NameGenerator;
import api.generators.UserNameType;

import java.util.Map;

public class DataSteps {
    protected static CreateUserRequest generateTestUser() {
        return DTOGenerator.generate(CreateUserRequest.class);
    }

    protected static CreateUserRequest generateTestUser(Map<String, Object> overrides) {
        return DTOGenerator.generate(CreateUserRequest.class, overrides);
    }

    public String generateTestUserName(UserNameType type) {
        return NameGenerator.generate(type);
    }
}
