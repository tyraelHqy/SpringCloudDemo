package com.tyrael.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 对请求做一定的限制，比如请求中含有Token便让请求继续往下走，如果请求不带Token就直接返回并给出提示。
 */

public class TokenFilter extends ZuulFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TokenFilter.class);

    // 可以在请求被路由之前调用
    @Override
    public String filterType() {
        return "pre";
    }

    // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
    @Override
    public int filterOrder() {
        return 0;
    }

    // 是否执行该过滤器，此处为true，说明需要过滤
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        LOG.info("---->>> TokenFilter {} , {} ",request.getMethod(),request.getRequestURL().toString());

        // 获取请求时候的参数
        String token = request.getParameter("token");

        if(StringUtils.isNotBlank(token)){
            ctx.setSendZuulResponse(true);
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess",true);
            return null;
        }else{
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(400);
            ctx.setResponseBody("token is empty");
            ctx.set("isSuccess",false);
            return null;
        }
    }
}
