package at.wrk.fmd.mls.gateway.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This controller provides a combined access to all proxied APIs
 */
@RestController
public class SwaggerController {

    private final GatewayProperties properties;

    @Autowired
    public SwaggerController(GatewayProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/swagger-resources/configuration/security")
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfigurationBuilder.builder().build();
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder().build();
    }

    @GetMapping("/swagger-resources")
    public List<SwaggerResource> swaggerResources() {
        return properties.getRoutes().stream().map(this::createResource).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Optional<String> getRouteUrl(RouteDefinition route) {
        return Optional.ofNullable(route.getPredicates().get(0).getArgs().values().toArray()[0])
                .map(String::valueOf)
                .map(s -> s.replaceAll("[/*]*$", ""));
    }

    private SwaggerResource createResource(RouteDefinition route) {
        Optional<String> url = getRouteUrl(route);
        if (url.isEmpty()) {
            return null;
        }

        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(route.getId());
        swaggerResource.setUrl(url.get() + "/v2/api-docs");
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}