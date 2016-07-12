package com.orange.signatwork.demo.biz.persistence.repository;

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

import com.orange.signatwork.demo.biz.persistence.model.SignDB;
import com.orange.signatwork.demo.biz.persistence.model.VideoDB;
import com.orange.signatwork.demo.biz.persistence.model.UserDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends CrudRepository<VideoDB, Long> {

    @Query("select distinct c FROM VideoDB c inner join c.sign sign where sign = :signDB")
    List<VideoDB> findBySign(@Param("signDB") SignDB signDB);

    @Query("select distinct c FROM VideoDB c inner join c.user user where user = :userDB")
    List<VideoDB> findByUser(@Param("userDB") UserDB userDB);
}
