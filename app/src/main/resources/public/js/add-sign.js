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
var addNewSignUrl = "/ws/sec/sign/create";

function addNewSign() {
    var signName, videoUrl, json, xhr;

    signName = $("#signName").val();
    videoUrl = $("#videoUrl").val();
    json = JSON.stringify(
        { signName: signName, videoUrl: videoUrl }
    );

    xhr = $.ajax({
      type: "POST",
      url: addNewSignUrl,
      data: json,
      contentType: "application/json; charset=utf-8",
    })
    .success(function () {
      console.log("success");
    })
    .error(function () {
      console.log("error: " + xhr.responseText);
    });
};

$("#addNewSign").click(addNewSign);