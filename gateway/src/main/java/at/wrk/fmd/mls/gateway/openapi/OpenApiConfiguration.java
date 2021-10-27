package at.wrk.fmd.mls.gateway.openapi;

import org.springdoc.core.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfiguration {

    @Autowired
    public void setSwaggerUrls(RouteDefinitionLocator locator, SwaggerUiConfigProperties swaggerUiConfig) {
        Set<SwaggerUrl> urls = locator
                .getRouteDefinitions()
                .filter(route -> !route.getId().equals("openapi"))
                .mapNotNull(this::createSwaggerUrl)
                .collect(Collectors.toSet())
                .block();
        swaggerUiConfig.setUrls(urls);
    }

    private SwaggerUrl createSwaggerUrl(RouteDefinition route) {
        Object path = route.getPredicates().get(0).getArgs().values().toArray()[0];
        if (path == null) {
            return null;
        }

        String name = route.getId();
        String url = path.toString().replaceAll("[/*]*$", "") + "/v3/api-docs";
        return new SwaggerUrl(name, url);
    }
}
