/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.internal.producer;

import java.io.Serializable;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.SessionScoped;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import br.gov.frameworkdemoiselle.internal.configuration.JAASConfig;
import br.gov.frameworkdemoiselle.security.SecurityException;
import br.gov.frameworkdemoiselle.util.Beans;

@SessionScoped
public class LoginContextFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient LoginContext loginContext;

	private String name;

	private CallbackHandler callbackHandler;

	private LoginContext getLoginContext() throws LoginException {
		if (this.loginContext == null) {
			this.loginContext = new LoginContext(getName(), getCallbackHandler());
		}

		return this.loginContext;
	}

	public static LoginContext createLoginContext() {
		LoginContext loginContext;

		try {
			loginContext = Beans.getReference(LoginContextFactory.class).getLoginContext();

		} catch (ContextNotActiveException cause) {
			loginContext = null;

		} catch (LoginException cause) {
			throw new SecurityException(cause);
		}

		if (loginContext == null) {
			try {
				loginContext = new LoginContextFactory().getLoginContext();

			} catch (LoginException cause) {
				throw new SecurityException(cause);
			}
		}

		return loginContext;
	}

	private String getName() {
		if (this.name == null) {
			this.name = Beans.getReference(JAASConfig.class).getLoginModuleName();
		}

		return this.name;
	}

	private CallbackHandler getCallbackHandler() {
		if (this.callbackHandler == null) {
			this.callbackHandler = Beans.getReference(CallbackHandler.class);
		}

		return this.callbackHandler;
	}
}