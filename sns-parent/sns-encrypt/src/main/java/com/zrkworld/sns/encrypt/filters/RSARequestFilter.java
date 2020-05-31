package com.zrkworld.sns.encrypt.filters;

import com.google.common.base.Strings;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.zrkworld.sns.encrypt.rsa.RsaKeys;
import com.zrkworld.sns.encrypt.service.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

@Component
public class RSARequestFilter extends ZuulFilter {

    @Autowired private RsaService rsaService;

    @Override
    public String filterType() {
        //过滤器在什么环境执行，解密操作需要在转发之前执行
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //设置过滤器的执行顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        //是否使用过滤器，true为使用
        return true;
    }

    @Override
    public Object run() {
        /**
         * 这里是过滤器具体执行的逻辑
         * 1. 从request body中读取出加密后的请求参数
         * 2. 将加密后的请求参数用私钥解密
         * 3. 将解密后的请求参数写回request body中
         * 4. 将请求头中的Authorization包含的token信息添加到上下文的请求头
         * 5. 转发请求（在过滤器中重置了requestContext之后，会自动转发）
         */
        //获取requestContext容器
        RequestContext ctx = RequestContext.getCurrentContext();
        //通过容器获得reqeust和reponse
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        try {
            //声明存放解密后的数据的变量
            String decryptData = null;
            HashMap dataMap = null;
            String token = null;

            String url = request.getRequestURL().toString();
            //通过request获取inputStream，在请求体中的数据，不能用getParameter，因为必须是键值对才能获取，必须要用getInputStream的方式获取流。
            InputStream stream = ctx.getRequest().getInputStream();
            //从inputStream中获得加密后的数据
            String requestParam = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));

            if(!Strings.isNullOrEmpty(requestParam)) {
                System.out.println(String.format("请求体中的密文: %s", requestParam));
                decryptData = rsaService.RSADecryptDataPEM(requestParam, RsaKeys.getServerPrvKeyPkcs8());

                System.out.println(String.format("解密后的内容: %s", decryptData));
            }

            System.out.println(String.format("request: %s >>> %s, data=%s", request.getMethod(), url, decryptData));

            //将解密后的数据进行转发，需要放到request中
            if(!Strings.isNullOrEmpty(decryptData)) {
                System.out.println("json字符串写入request body");
                //获取解密后的数据的字节数组
                final byte[] reqBodyBytes = decryptData.getBytes();
                //使用RequestContext进行数据的包装
                ctx.setRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }

                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
            }

            System.out.println("转发request");
            // 得到头信息
            String header = request.getHeader("Authorization");
            // 判断是否有头信息
            if (!StringUtils.isEmpty(header)) {
                // 把头信息继续往下传
                ctx.addZuulRequestHeader("Authorization", header);
            }
            // 设置request请求头中的Content-Type为application/json，否则api接口模块需要进行url转码操作
            ctx.addZuulRequestHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON) + ";charset=UTF-8");

        } catch (Exception e) {
            System.out.println(this.getClass().getName() + "运行出错" + e.getMessage());
        }

        return null;
    }
}