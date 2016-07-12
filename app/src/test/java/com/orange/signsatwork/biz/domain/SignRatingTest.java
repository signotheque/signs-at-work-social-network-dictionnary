package com.orange.signsatwork.biz.domain;

import com.orange.signsatwork.biz.ClearDB;
import com.orange.signsatwork.biz.persistence.service.SignService;
import com.orange.signsatwork.biz.persistence.service.UserService;
import com.orange.signsatwork.biz.persistence.service.VideoService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignRatingTest {

  @Autowired
  ClearDB clearDB;

  @Autowired
  UserService userService;
  @Autowired
  VideoService videoService;
  @Autowired
  SignService signService;

  User user;
  Sign sign;

  @Before
  public void setup() {
    clearDB.deleteAll();

    user = userService.create(new User(0, "user", "titi", "toto", "titi@toto.org", "", "", null, null, null, null, null, null, null), "pass");
    sign = signService.create(user.id, "mySign", "//video");
  }

  @Test
  public void defaultRatingIsNeutral() {
    // given
    // do
    Rating rating = sign.rating(user);
    // then
    Assertions.assertThat(rating).isEqualTo(Rating.Neutral);
  }

  @Test
  public void changeAndReadRating() {
    testChangeAndReadRating(Rating.Negative);
    testChangeAndReadRating(Rating.Positive);
    testChangeAndReadRating(Rating.Neutral);
  }

  private void testChangeAndReadRating(Rating rating) {
    // given
    sign.changeUserRating(user, rating);
    // do
    Sign reloadedSign = signService.withId(sign.id);
    Rating newRating = reloadedSign.rating(user);
    // then
    Assertions.assertThat(newRating).isEqualTo(rating);
  }
}
