package com.orange.signsatwork.biz.view.controller;

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

import com.orange.signsatwork.biz.persistence.service.MessageByLocaleService;
import com.orange.signsatwork.biz.persistence.service.SignService;
import com.orange.signsatwork.biz.persistence.service.UserService;
import com.orange.signsatwork.biz.view.model.SignView;
import com.orange.signsatwork.biz.webservice.model.SignCreationView;
import com.orange.signsatwork.biz.view.model.AuthentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

  @Autowired
  private UserService userService;
  @Autowired
  private SignService signService;
  @Autowired
  MessageByLocaleService messageByLocaleService;

  @RequestMapping("/")
  public String index(Principal principal, Model model) {
    AuthentModel.addAuthentModelWithUserDetails(model, principal, userService);

    model.addAttribute("title", messageByLocaleService.getMessage("app_name"));
    List<SignView> signsView = SignView.from(signService.all());
    model.addAttribute("signs", signsView);
    model.addAttribute("signCreationView", new SignCreationView());

    return "index";
  }
}
