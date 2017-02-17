package com.orange.signsatwork.biz.persistence.repository;

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

import com.orange.signsatwork.biz.persistence.model.RatingDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<RatingDB, Long> {

    @Query(value="select  b.sign_id, count(*) as nbr from ratings a inner join videos b on a.video_id = b.id and a.rating='Positive' group by b.sign_id order by nbr desc", nativeQuery = true)
    Long[] findMostRating();

    @Query(value="select  b.sign_id, count(*) as nbr from ratings a inner join videos b on a.video_id = b.id and a.rating='Positive' group by b.sign_id order by nbr asc", nativeQuery = true)
    Long[] findLowRating();
}
