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

import com.orange.signsatwork.biz.domain.User;
import com.orange.signsatwork.biz.persistence.service.MessageByLocaleService;
import com.orange.signsatwork.biz.domain.Sign;
import com.orange.signsatwork.biz.persistence.service.SignService;
import com.orange.signsatwork.biz.persistence.service.UserService;
import com.orange.signsatwork.biz.view.model.AuthentModel;
import com.orange.signsatwork.biz.view.model.SignProfileView;
import com.orange.signsatwork.biz.view.model.SignView;
import com.orange.signsatwork.biz.webservice.model.SignCreationView;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Slf4j
@Controller
public class SignController {

  @Autowired
  private UserService userService;

  @Autowired
  private SignService signService;

  @Autowired
  MessageByLocaleService messageByLocaleService;

  @RequestMapping(value = "/signs")
  public String signs(Principal principal, Model model) {
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));

    List<SignView> signsView = SignView.from(signService.all());
    model.addAttribute("signs", signsView);
    model.addAttribute("signCreationView", new SignCreationView());

    return "signs";
  }

  @RequestMapping(value = "/sign/{id}")
  public String sign(@PathVariable long id, Principal principal, Model model)  {
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));

    Sign sign = signService.withIdLoadAssociates(id);

    model.addAttribute("title", messageByLocaleService.getMessage("sign.info"));

    SignProfileView signProfileView = AuthentModel.isAuthenticated(principal) ?
            new SignProfileView(sign, signService, userService.withUserName(principal.getName())) :
            new SignProfileView(sign, signService);
    model.addAttribute("signProfileView", signProfileView);

    return "sign";
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sign/{id}/detail")
  public String signDetail(@PathVariable long id, Principal principal, Model model)  {
    Sign sign = signService.withIdLoadAssociates(id);

    model.addAttribute("title", messageByLocaleService.getMessage("sign.details"));

    SignProfileView signProfileView = new SignProfileView(sign, signService, userService.withUserName(principal.getName()));
    model.addAttribute("signProfileView", signProfileView);

    return "sign-detail";
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/create", method = RequestMethod.POST)
  public String createSign(HttpServletRequest req, @ModelAttribute SignCreationView signCreationView, Principal principal) {
    User user = userService.withUserName(principal.getName());
    Sign sign = signService.create(user.id, signCreationView.getSignName(), signCreationView.getVideoUrl());

    log.info("createSign: username = {} / sign name = {} / video url = {}", user.username, signCreationView.getSignName(), signCreationView.getVideoUrl());

    return "redirect:/sign/" + sign.id;
  }
}
