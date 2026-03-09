package common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MakeTransfer {
     double amount() default 1.00;
     int receiverAccount() default 1;
     int senderAccount() default 2;
     int receiverUser() default 1;
     int senderUser() default 1;
}
