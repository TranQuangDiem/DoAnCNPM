package sources.config;


import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

/**
 * This class will extend Spring's AuthenticationEntryPoint class and override its method commence.
 * It rejects every unauthenticated request and send error code 401
 */

@Component
public class UserDetailFilter implements AuthenticationEntryPoint, Serializable, AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        System.out.println("COMMENCE");
        response.sendRedirect("/login?error=true");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.sendRedirect("/");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        UrlPathHelper pathHelper = new UrlPathHelper();
        String error = exception.getMessage();
        String[] es = error!= null ?error.split(" ") : null;
        String esd="";
        if(es.length == 3)
          esd = es[2];
        if(esd.equals("disabled"))
        response.sendRedirect(pathHelper.getContextPath(request)+"/login?error=blocked");
        else
            response.sendRedirect(pathHelper.getContextPath(request)+"/login?error=true");

    }
}
