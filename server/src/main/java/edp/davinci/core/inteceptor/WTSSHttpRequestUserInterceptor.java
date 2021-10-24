package edp.davinci.core.inteceptor;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpRequestUserInterceptor;
import edp.core.utils.TokenUtils;
import edp.davinci.core.common.Constants;
import edp.davinci.model.User;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class WTSSHttpRequestUserInterceptor implements HttpRequestUserInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WTSSHttpRequestUserInterceptor.class.getName());

    private TokenUtils tokenUtils;

    public WTSSHttpRequestUserInterceptor(TokenUtils tokenUtils){
        this.tokenUtils = tokenUtils;
    }

    @Override
    public HttpServletRequest addUserToRequest(String s, HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute("username", s);
        return createDssToken(s, httpServletRequest);
    }

    private HttpServletRequest createDssToken(final String username, final HttpServletRequest req) {

        logger.info(username + " enters the createDssToken method");

        String token = req.getHeader(Constants.TOKEN_HEADER_STRING);

        if (token == null) {
            User user = new User();
            user.setUsername(username);
            token = tokenUtils.generateToken(user);
        }else{
            logger.info("dss exists token {} for user {}.", token, username);
        }

        final Cookie cookie = new Cookie(Constants.TOKEN_HEADER_STRING, token);
        cookie.setPath("/");
        HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(req) {

            @Override
            public Cookie[] getCookies() {
                final Cookie[] cookies = (Cookie[]) ArrayUtils.add(super.getCookies(), cookie);
                return cookies;
            }
        };
        logger.info("dss new token {} for user {}.", token, username);
        return httpServletRequestWrapper;
    }

    @Override
    public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
        String token = "";
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.TOKEN_HEADER_STRING)) {
                    token = cookie.getValue();
                }
            }
        }
        logger.info("dss token {} ", token);
        if (token != null && !token.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public String getUser(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getSession().getAttribute("username").toString();
    }
}
