package com.orange.spring.demo.biz.view.controller;

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

import com.orange.spring.demo.biz.domain.*;
import com.orange.spring.demo.biz.persistence.service.*;
import com.orange.spring.demo.biz.security.AppSecurityAdmin;
import com.orange.spring.demo.biz.view.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

  @Autowired
  private UserController userController;

  @Autowired
  private UserService userService;
  @Autowired
  private CommunityService communityService;
  @Autowired
  MessageByLocaleService messageByLocaleService;

  @Secured("ROLE_ADMIN")
  @RequestMapping("/communities")
  public String communities(Model model) {
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("communities"));
    model.addAttribute("communities", CommunityView.from(communityService.all()));
    return "communities";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/community/{id}")
  public String community(@PathVariable long id, Model model) {
    Community community = communityService.withId(id);
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("community_details"));
    model.addAttribute("community", community);
    return "community";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping("/admin")
  public String admin(Model model) {
    AuthentModel.addAuthenticatedModel(model, true);
    model.addAttribute("title", messageByLocaleService.getMessage("admin_page"));
    // for thymeleaf form management
    model.addAttribute("user", new UserCreationView());
    model.addAttribute("community", new CommunityView());
    return "admin";
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
  public String user(@ModelAttribute UserCreationView userCreationView, Model model) {
    User user = userService.create(userCreationView.toUser(), userCreationView.getPassword());
    return userController.userDetails(user.id, model);
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/admin/community/create", method = RequestMethod.POST)
  public String community(@ModelAttribute CommunityView communityView, Model model) {
    Community community = communityService.create(communityView.toCommunity());
    return community(community.id, model);
  }
}
