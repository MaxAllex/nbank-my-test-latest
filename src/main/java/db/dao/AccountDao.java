package db.dao;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDao extends BaseDao {
  private Long id;
  private String accountNumber;
  private Double balance;
  private Long customerId;
}
