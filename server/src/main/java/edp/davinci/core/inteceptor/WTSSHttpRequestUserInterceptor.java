package edp.davinci.core.inteceptor;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpRequestUserInterceptor;
import edp.core.consts.Consts;
import edp.core.utils.TokenUtils;
import edp.davinci.core.common.Constants;
import edp.davinci.model.User;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import edp.davinci.core.utils.CookieUtils;


public class WTSSHttpRequestUserInterceptor implements HttpRequestUserInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WTSSHttpRequestUserInterceptor.class.getName());

    private TokenUtils tokenUtils;

    public WTSSHttpRequestUserInterceptor(TokenUtils tokenUtils){
        this.tokenUtils = tokenUtils;
    }

    @Override
    public HttpServletRequest addUserToRequest(String username, HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute("username", username);
        return createDssToken(username, httpServletRequest);
    }

    private HttpServletRequest createDssToken(final String username, final HttpServletRequest req) {
        logger.info(username + " enters the createDssToken method");

        String token      = CookieUtils.getCookieValue(req,Constants.TOKEN_HEADER_STRING);

        User user = new User();
        user.setUsername(username);
        String newToken = tokenUtils.generateToken(user);

        if(token != null){
            logger.info("visualis exists token,old token {},new token {} for user {}.", token, newToken, username);
        } else{
            logger.info("visualis new token {} for user {}.", newToken, username);
        }

        //TODO 暂时用于测试，将完善过期时间和跨域安全问题
        final Cookie tokenCookie = new Cookie(Constants.TOKEN_HEADER_STRING, Consts.TOKEN_PREFIX + newToken);
        tokenCookie.setPath("/");

        final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(req) {
            @Override
            public Cookie[] getCookies() {
                final Cookie[] cookies = (Cookie[]) ArrayUtils.add(super.getCookies(), tokenCookie);
                return cookies;
            }
        };
        logger.info("dss new token {} for user {} .", newToken, username);
        return httpServletRequestWrapper;
    }

    @Override
    public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
        String token = CookieUtils.getCookieValue(httpServletRequest,Constants.TOKEN_HEADER_STRING);
        logger.info("dss token {} ", token);
        if (token != null && !token.isEmpty()) {
            String username = tokenUtils.getUsername(token);
            if(username == null){
                return false;
            }
            if(!username.equals(getUser(httpServletRequest))){
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String getUser(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getSession().getAttribute("username").toString();
    }
}
