package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.Community;
import com.orange.signsatwork.biz.domain.Favorite;
import com.orange.signsatwork.biz.domain.Sign;
import com.orange.signsatwork.biz.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FavoriteServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveFavorite() {
    // given
    User user = testUser.get("user-canRemoveFavorite");
    Sign sign = services.signService().create(user.id, "sign-canRemoveFavorite", "//video-canRemoveFavorite");

    // do/then
    Favorite favorite = services.userService().createUserFavorite(user.id, "favorite-canRemoveFavorite");
    services.favoriteService().changeFavoriteSigns(favorite.id, Arrays.asList(new Long[]{sign.id}));
    Assertions.assertThat(services.favoriteService().all().stream().filter(f -> f.id == favorite.id).count()).isEqualTo(1);
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().favorites.ids()).contains(favorite.id);

    // do/then
    services.favoriteService().delete(favorite);
    Assertions.assertThat(services.favoriteService().all().stream().filter(f -> f.id == favorite.id).count()).isEqualTo(0);
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().favorites.ids()).doesNotContain(favorite.id);
  }
}
