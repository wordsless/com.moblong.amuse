package com.moblong.amuse;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/LoggingFilter")
public final class LoggingFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
