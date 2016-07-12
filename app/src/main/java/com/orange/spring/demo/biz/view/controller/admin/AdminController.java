package com.orange.spring.demo.biz.view.controller.admin;

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

import com.orange.spring.demo.biz.domain.Community;
import com.orange.spring.demo.biz.domain.User;
import com.orange.spring.demo.biz.persistence.service.CommunityService;
import com.orange.spring.demo.biz.persistence.service.MessageByLocaleService;
import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.view.controller.UserController;
import com.orange.spring.demo.biz.view.model.AuthentModel;
import com.orange.spring.demo.biz.view.model.CommunityView;
import com.orange.spring.demo.biz.view.model.UserCreationView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

  @Autowired
  private UserAdminController userAdminController;

  @Autowired
  private UserService userService;
  @Autowired
  private CommunityService communityService;
  @Autowired
  MessageByLocaleService messageByLocaleService;

  @Secured("ROLE_ADMIN")
  @RequestMapping("/sec/admin")
  public String admin(Model model) {
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("admin_page"));
    // for thymeleaf form management
    model.addAttribute("user", new UserCreationView());
    model.addAttribute("community", new CommunityView());
    return "admin/index";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping("/sec/admin/communities")
  public String communities(Model model) {
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("communities"));
    model.addAttribute("communities", CommunityView.from(communityService.all()));
    return "admin/communities";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/community/{id}")
  public String community(@PathVariable long id, Model model) {
    Community community = communityService.withId(id);
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("community_details"));
    model.addAttribute("community", community);
    return "admin/community";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/user/create", method = RequestMethod.POST)
  public String user(@ModelAttribute UserCreationView userCreationView, Model model) {
    User user = userService.create(userCreationView.toUser(), userCreationView.getPassword());
    return userAdminController.userDetails(user.id, model);
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/sec/admin/community/create", method = RequestMethod.POST)
  public String community(@ModelAttribute CommunityView communityView, Model model) {
    Community community = communityService.create(communityView.toCommunity());
    return community(community.id, model);
  }
}
