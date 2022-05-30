package com.howtodoinjava;

import com.howtodoinjava.test.SpringTestContext;
import com.howtodoinjava.test.SpringTestContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringTestContextExtension.class)
public class AppTest {
  public final SpringTestContext spring = new SpringTestContext(this);

  @Test
  void contextLoads() throws Exception {
  }
}
