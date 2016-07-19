package com.orange.signsatwork.biz.view.controller.admin;

/*
 * #%L
 * Signs at work
 * %%
 * Copyright (C) 2016 Orange
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.orange.signsatwork.biz.domain.User;
import com.orange.signsatwork.biz.persistence.service.MessageByLocaleService;
import com.orange.signsatwork.biz.persistence.service.Services;
import com.orange.signsatwork.biz.view.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserAdminController {

  @Autowired
  private Services services;
  @Autowired
  MessageByLocaleService messageByLocaleService;

  @Secured("ROLE_ADMIN")
  @RequestMapping("/sec/admin/users")
  public String users(Model model) {

    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("users"));
    model.addAttribute("users", UserView.from(services.user().all()));
    return "admin/users";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/{id}")
  public String userDetails(@PathVariable long id, Model model) {
    User user = services.user().withId(id);

    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("user_details"));

    UserProfileView userProfileView = new UserProfileView(user, services.community());
    model.addAttribute("userProfileView", userProfileView);

    model.addAttribute("requestView", new RequestView());
    model.addAttribute("favoriteView", new FavoriteView());
    model.addAttribute("signView", new SignView());

    return "admin/user";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/{userId}/add/communities", method = RequestMethod.POST)
  /**
   * We retrieve all form parameters directly from the raw request since in this case
   * we can not rely on a json object deserialization.
   * Indeed, POST form parameters look like this:
   *  - userCommunitiesIds -> "12"
   *  - userCommunitiesIds -> "34"
   *  - ...
   *  which in this case means that the user belongs to communities with id 12 & 34
   *
   *  Then we resend the user details page
   */
  public String changeUserCommunities(
          HttpServletRequest req, @PathVariable long userId, Model model) {

    List<Long> communitiesIds =
            transformCommunitiesIdsToLong(req.getParameterMap().get("userCommunitiesIds"));

    services.user().changeUserCommunities(userId, communitiesIds);

    return userDetails(userId, model);
  }


  /** The form POST provides Ids as String, we convert it back to Long */
  private List<Long> transformCommunitiesIdsToLong(String[] userCommunitiesIds) {
    if (userCommunitiesIds == null) {
      return new ArrayList<>();
    }
    return Arrays.asList(userCommunitiesIds).stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/{userId}/add/sign", method = RequestMethod.POST)
  public String newSign(
          HttpServletRequest req, @PathVariable long userId, Model model) {

    String signName = req.getParameter("name");
    String signUrl = req.getParameter("url");

    services.sign().create(userId, signName, signUrl);

    return userDetails(userId, model);
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/{userId}/add/request", method = RequestMethod.POST)
  public String createUserRequest(
          HttpServletRequest req, @PathVariable long userId, Model model) {

    String requestName = req.getParameter("requestName");
    if (services.request().withName(requestName).list().isEmpty()) {
      services.user().createUserRequest(userId, requestName);
    }

    return userDetails(userId, model);
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/{userId}/add/favorite", method = RequestMethod.POST)
  public String createUserFavorite(
          HttpServletRequest req, @PathVariable long userId, Model model) {

    String favoriteName = req.getParameter("favoriteName");
    services.user().createUserFavorite(userId, favoriteName);

    return userDetails(userId, model);
  }
}
