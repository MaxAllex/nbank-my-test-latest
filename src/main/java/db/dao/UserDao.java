package db.dao;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDao extends BaseDao {
  private Long id;
  private String username;
  private String password;
  private String role;
  private String name;
}
