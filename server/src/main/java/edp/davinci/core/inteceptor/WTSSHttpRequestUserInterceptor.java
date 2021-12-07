package edp.davinci.core.inteceptor;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpRequestUserInterceptor;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import edp.core.utils.TokenUtils;
import edp.davinci.core.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info(username + " enters the createDssToken method ,and ignore operation.");

        String userTicketId = CookieUtils.getCookieValue(req,Constants.USER_TICKET_ID_STRING);

        final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(req);
        logger.info("visualis userTicketId {} for user {} .", userTicketId,username);
        return httpServletRequestWrapper;
    }

    @Override
    public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
        String userTicket = CookieUtils.getCookieValue(httpServletRequest,Constants.USER_TICKET_ID_STRING);
        logger.info("dss userTicket {} ", userTicket);
        if (userTicket != null && !userTicket.isEmpty()) {
            String username = SecurityFilter.getLoginUsername(httpServletRequest);
            if(username == null || username.isEmpty()){
                return false;
            }
            //说明已SSO统一登录
            httpServletRequest.getSession().setAttribute("username", username);
            return true;
        }
        return false;
    }

    @Override
    public String getUser(HttpServletRequest httpServletRequest) {
        Object username = httpServletRequest.getSession().getAttribute("username");
        return username == null?null:username.toString();
    }
}
