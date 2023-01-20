package com.lucasjosino.hawapi.resolvers;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class IndexFallbackResourceResolver extends PathResourceResolver {

    // Ref: https://stackoverflow.com/a/69647129
    //
    // TODO: Review this workaround
    @Override
    protected Resource resolveResourceInternal(
            HttpServletRequest request,
            String requestPath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
    ) {
        // Redirect all pages to '/index.html'
        //
        // E.g:
        //  * /        -> /index.html
        //  * /try-it  -> /try-it/index.html
        //  * /docs    -> /docs/index.html
        Resource resource = super.resolveResourceInternal(request, requestPath, locations, chain);
        boolean isAPage = !requestPath.contains("assets")
                && !requestPath.contains("data")
                && !requestPath.contains("resources")
                && !requestPath.contains("fonts");

        if (resource == null && isAPage) {
            resource = super.resolveResourceInternal(
                    request,
                    requestPath + "/index.html",
                    locations,
                    chain
            );
        }

        return resource;
    }
}
