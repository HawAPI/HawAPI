package com.lucasjosino.hawapi.resolvers;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Handler requests for static files (web).
 * <p>
 * E.g:
 * <ul>
 * <li>/        -> /index.html</li>
 * <li>/try-it  -> /try-it/index.html</li>
 * <li>/docs    -> /docs/index.html</li>
 * </ul>
 */
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
        Resource resource = super.resolveResourceInternal(request, requestPath, locations, chain);
        boolean isAPage = !requestPath.contains("assets")
                && !requestPath.contains("data")
                && !requestPath.contains("resources")
                && !requestPath.contains("fonts");

        // Redirect all pages to '/index.html'
        if (resource == null && isAPage) {
            resource = super.resolveResourceInternal(
                    request,
                    requestPath + "/index.html",
                    locations,
                    chain
            );
        }

        // Handler requests for '/resources' (css, js, etc...)
        if (resource == null && requestPath.contains("resources")) {
            resource = super.resolveResourceInternal(
                    request,
                    "docs/" + requestPath,
                    locations,
                    chain
            );
        }

        return resource;
    }
}
