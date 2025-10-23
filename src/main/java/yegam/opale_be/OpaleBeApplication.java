package yegam.opale_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "yegam.opale_be")
public class OpaleBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpaleBeApplication.class, args);
  }

}
