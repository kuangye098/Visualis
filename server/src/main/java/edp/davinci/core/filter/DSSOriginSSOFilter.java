package edp.davinci.core.filter;

import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginFilter;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.UserInterceptor;
import edp.core.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;

public class DSSOriginSSOFilter extends OriginSSOPluginFilter {

    private static final Logger logger = LoggerFactory.getLogger(DSSOriginSSOFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        logger.info("The DSSOriginSSOFilter Init.");
    }

    @Override
    public UserInterceptor getUserInterceptor(FilterConfig filterConfig) {
        return SpringUtil.getBean(UserInterceptor.class);
    }
}
