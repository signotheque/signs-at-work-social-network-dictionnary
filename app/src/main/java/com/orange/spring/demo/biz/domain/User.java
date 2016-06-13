package com.orange.spring.demo.biz.domain;

/*
 * #%L
 * Spring demo
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@RequiredArgsConstructor
@Getter
public class User {
  private final long id;
  private final String username;
  private final String firstName;
  private final String lastName;
  private final String email;
  // entity in the company: OLPS/SOFT for instance
  private final String entity;
  // for instance: developer, designer, etc
  private final String activity;
  private final Date lastConnectionDate;
}
