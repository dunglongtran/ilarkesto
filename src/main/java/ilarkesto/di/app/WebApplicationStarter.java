/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.di.app;

import ilarkesto.base.Sys;
import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanContainer;
import ilarkesto.webapp.AWebApplication;
import ilarkesto.webapp.Servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class WebApplicationStarter extends ApplicationStarter {

	private static final Log LOG = Log.get(WebApplicationStarter.class);

	public static AWebApplication startWebApplication(String applicationClassName, ServletConfig servletConfig) {
		checkWorkDir();
		AWebApplication result;
		BeanContainer beanProvider = new BeanContainer();
		beanProvider.put("contextPath", Servlet.getContextPath(servletConfig));
		ServletContext servletContext = servletConfig.getServletContext();
		beanProvider.put("serverInfo", servletContext.getServerInfo());
		beanProvider.put("servletContextName", servletContext.getServletContextName());
		beanProvider.put("servletContextRealPath", servletContext.getRealPath("/"));
		try {
			beanProvider.put("serverEndpoints", Servlet.getEndpoints());
		} catch (Exception e) {
			LOG.warn(e);
		}
		try {
			result = startApplication((Class<? extends AWebApplication>) Class.forName(applicationClassName),
				beanProvider);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		LOG.debug("Triggering Garbage Collection");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}

		return result;
	}

	private static void checkWorkDir() {
		if (Sys.isDevelopmentMode()) {
			if (Sys.getWorkDir().equals(Sys.getUsersHomeDir()))
				throw new IllegalStateException(
						"Work directory is home directory.\n\n  In development mode the work directory must be the project directory. You probably have to set the working directory of your web application server launch configuration.\n");
		}
	}

}
