package api.steps.customer;

public class CustomerStepsFactory {

    public static CustomerSteps byCredentials(String username, String password) {
        return new CustomerStepsByAuth(username, password);
    }

    public static CustomerSteps byToken(String token) {
        return new CustomerStepsByToken(token);
    }
}

