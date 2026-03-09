package api.specs;

import api.http.facade.Endpoint;
import api.http.facade.ValidatedHTTPRequestImp;
import api.configs.AppConfig;
import api.dto.authentication.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private RequestSpecs() {
    }

    private static final String baseUri = AppConfig.getProperty("server") + AppConfig.getProperty("apiVersion");
    public static final String adminToken = AppConfig.getProperty("adminToken");
    private static Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder().
                addHeader("Authorization", adminToken)
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        String userAuthHeader = new ValidatedHTTPRequestImp<LoginRequest, ValidatableResponse>(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(new LoginRequest(username, password))
                .extract()
                .header("Authorization");

        return defaultRequestBuilder().addHeader("Authorization", userAuthHeader).build();
    }

    public static RequestSpecification authByToken(String token) {
        return defaultRequestBuilder().addHeader("Authorization", token).build();
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri(baseUri);
    }

    public static String getUserAuthHeader(String username, String password) {
        String userAuthHeader;

        if (!authHeaders.containsKey(username)) {
            userAuthHeader = new ValidatedHTTPRequestImp<LoginRequest, ValidatableResponse>(
                    RequestSpecs.unauthSpec(),
                    Endpoint.LOGIN,
                    ResponseSpecs.requestReturnsOK())
                    .post(new LoginRequest(username, password))
                    .extract()
                    .header("Authorization");

            authHeaders.put(username, userAuthHeader);
        } else {
            userAuthHeader = authHeaders.get(username);
        }

        return userAuthHeader;
    }

    public static void clearAuthHeaders() {
        authHeaders = new HashMap<>(Map.of("admin", adminToken));
    }
}
