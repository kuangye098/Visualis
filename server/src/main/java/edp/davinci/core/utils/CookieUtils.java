package edp.davinci.core.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static String getCookieValue(HttpServletRequest httpServletRequest,
                                        String cookieKey){

        final Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies == null){
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieKey)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
