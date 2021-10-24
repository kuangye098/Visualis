package edp.davinci.core.filter;

import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginFilter;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.UserInterceptor;
import edp.core.utils.SpringUtil;
import edp.core.utils.TokenUtils;
import edp.davinci.core.inteceptor.WTSSHttpRequestUserInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;

public class DSSOriginSSOFilter extends OriginSSOPluginFilter {

    private static final Logger logger = LoggerFactory.getLogger(DSSOriginSSOFilter.class.getName());

    private TokenUtils tokenUtils;

    @Override
    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        tokenUtils = SpringUtil.getBean(TokenUtils.class);
        logger.info("The DSSOriginSSOFilter Init,tokenUtils = [{}].",tokenUtils);
    }

    @Override
    public UserInterceptor getUserInterceptor(FilterConfig filterConfig) {
        return new WTSSHttpRequestUserInterceptor(tokenUtils);
    }
}
