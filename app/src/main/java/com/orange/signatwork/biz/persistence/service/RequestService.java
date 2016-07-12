package com.orange.signatwork.demo.biz.persistence.service;

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

import com.orange.signatwork.demo.biz.domain.Request;
import com.orange.signatwork.demo.biz.domain.Requests;

public interface RequestService {
  Requests all();

  Requests requestsforUser(long id);

  Request changeSignRequest(long requestId, long signId);

  Request withId(long id);

  Requests withName(String name);

  Request create(Request request);
}
