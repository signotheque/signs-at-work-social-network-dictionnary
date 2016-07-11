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

import com.orange.spring.demo.biz.domain.Sign;
import com.orange.spring.demo.biz.persistence.service.MessageByLocaleService;
import com.orange.spring.demo.biz.persistence.service.SignService;
import com.orange.spring.demo.biz.view.model.AuthentModel;
import com.orange.spring.demo.biz.view.model.SignProfileView;
import com.orange.spring.demo.biz.view.model.SignView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class SignController {

  @Autowired
  private SignService signService;

  @Autowired
  MessageByLocaleService messageByLocaleService;

  @RequestMapping(value = "/signs")
  public String signs(Principal principal, Model model) {
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));

    List<SignView> signsView = SignView.from(signService.all());
    model.addAttribute("signs", signsView);
    return "signs";
  }

  @RequestMapping(value = "/sign/{id}")
  public String sign(@PathVariable long id, Principal principal, Model model)  {
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));

    Sign sign = signService.withIdForAssociate(id);

    model.addAttribute("title", messageByLocaleService.getMessage("sign.details"));

    SignProfileView signProfileView = new SignProfileView(sign, signService);
    model.addAttribute("signProfileView", signProfileView);

    return "sign";
  }
}
