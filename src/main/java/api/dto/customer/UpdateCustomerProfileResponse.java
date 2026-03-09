package api.dto.customer;

import lombok.Data;

@Data
public class UpdateCustomerProfileResponse {
    Customer customer;
    String message;
}
