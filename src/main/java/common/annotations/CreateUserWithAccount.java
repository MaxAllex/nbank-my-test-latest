package common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateUserWithAccount {
  int howManyUsers() default 1;

  double amount() default 0.00;

  int howManyAccounts() default 1;

  int auth() default 1;
}
