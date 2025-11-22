package apinexo.common.configuration;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CacheRequestResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        CachedBodyHttpServletRequest wrappedRequest =
                new CachedBodyHttpServletRequest((HttpServletRequest) req);

        CachedBodyHttpServletResponse wrappedResponse =
                new CachedBodyHttpServletResponse((HttpServletResponse) res);

        long startTime = System.currentTimeMillis();

        wrappedRequest.setAttribute("startTime", startTime);

        chain.doFilter(wrappedRequest, wrappedResponse);

        res.getOutputStream().write(wrappedResponse.getCachedBody());
    }
}
