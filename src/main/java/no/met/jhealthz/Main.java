package no.met.jhealthz;

/*
Simple servlet that responds with a given files content, if lastModified
indicates that the file was updated more recent than timeoutSeconds.

Copyright (C) 2018 MET Norway, https://met.no

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import java.io.File;
import java.io.IOException;
import java.lang.SecurityException;
import java.lang.System;
import java.nio.file.Files;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

@WebServlet(
    urlPatterns = {""},
    loadOnStartup = 1,
    initParams =
    {
        @WebInitParam(name = "contentFile", value = "/run/jhealthz/status.xml"),
        @WebInitParam(name = "contentType", value = "text/xml"),
        @WebInitParam(name = "timeoutSeconds", value = "120")
    }
)
public class Main extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File contentFile = new File(getInitParameter("contentFile"));
        int timeoutSeconds = Integer.parseInt(getInitParameter("timeoutSeconds"));

        long lastModified = 0L;
        try {
            lastModified = contentFile.lastModified();
        } catch (SecurityException e) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR, "Unable to check mtime on contentFile.");
            return;
        }
        if (((System.currentTimeMillis() - lastModified) / 1e3) > timeoutSeconds) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR, "contentFile is outdated.");
            return;
        }

        response.setContentType(getInitParameter("contentType"));

        byte[] buf;
        try {
            buf = Files.readAllBytes(contentFile.toPath());
        } catch (SecurityException e) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR, "SecurityException reading contentFile.");
            return;
        } catch (OutOfMemoryError e) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR, "Out Of Memory while reading contentFile.");
            return;
        }
        response.getOutputStream().write(buf);
    }
}
