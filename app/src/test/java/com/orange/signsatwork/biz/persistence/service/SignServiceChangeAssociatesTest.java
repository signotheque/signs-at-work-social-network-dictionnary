package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.ClearDB;
import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.Sign;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignServiceChangeAssociatesTest {

  @Autowired
  SignService signService;

  @Autowired
  TestUser testUser;

  @Autowired
  ClearDB clearDB;

  long userId1;
  long userId2;

  @Before
  public void setup() {
    clearDB.deleteAll();
    userId1 = testUser.get("user1").id;
    userId2 = testUser.get("user2").id;
  }

  @Test
  public void associateSigns() {
    // given
    Sign sign1 = signService.create(userId1, "s1", "//video1");
    Sign sign2 = signService.create(userId2, "s2", "//video2");
    Sign sign3 = signService.create(userId2, "s3", "//video3");

    // do
    signService.changeSignAssociates(sign1.id, Arrays.asList(new Long[]{sign3.id}));
    signService.changeSignAssociates(sign2.id, Arrays.asList(new Long[]{sign1.id}));
    signService.changeSignAssociates(sign3.id, Arrays.asList(new Long[]{sign2.id, sign1.id}));

    sign1 = signService.withIdLoadAssociates(sign1.id);
    sign2 = signService.withIdLoadAssociates(sign2.id);
    sign3 = signService.withIdLoadAssociates(sign3.id);

    // then
    Assertions.assertThat(sign1.associateSignsIds).hasSize(1);
    Assertions.assertThat(sign1.referenceBySignsIds).hasSize(2);
    Assertions.assertThat(sign1.associateSignsIds).contains(sign3.id);
    Assertions.assertThat(sign1.referenceBySignsIds).contains(sign2.id);
    Assertions.assertThat(sign1.referenceBySignsIds).contains(sign3.id);

    Assertions.assertThat(sign2.associateSignsIds).hasSize(1);
    Assertions.assertThat(sign2.referenceBySignsIds).hasSize(1);
    Assertions.assertThat(sign2.associateSignsIds).contains(sign1.id);
    Assertions.assertThat(sign2.referenceBySignsIds).contains(sign3.id);

    Assertions.assertThat(sign3.associateSignsIds).hasSize(2);
    Assertions.assertThat(sign3.referenceBySignsIds).hasSize(1);
    Assertions.assertThat(sign3.associateSignsIds).contains(sign1.id);
    Assertions.assertThat(sign3.associateSignsIds).contains(sign2.id);
    Assertions.assertThat(sign3.referenceBySignsIds).contains(sign1.id);
  }
}
