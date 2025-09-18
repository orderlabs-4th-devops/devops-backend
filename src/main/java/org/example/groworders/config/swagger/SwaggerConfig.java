package org.example.groworders.config.swagger;

import org.example.groworders.config.filter.LoginFilter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.Optional;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenApiCustomizer springSecurityLoginEndpointCustomizer(ApplicationContext applicationContext) {
        FilterChainProxy springSecurityFilterChain = applicationContext.getBean("springSecurityFilterChain", FilterChainProxy.class);

        return (openApi) -> {
            for (SecurityFilterChain filterChain : springSecurityFilterChain.getFilterChains()) {
                // 스프링 시큐리티의 특정 필터를 받아오는 부분
                Optional<LoginFilter> filter = filterChain.getFilters().stream()
                        .filter(LoginFilter.class::isInstance)
                        .map(LoginFilter.class::cast)
                        .findAny();
                if(filter.isPresent()) {
                    // 문서 설정 객체
                    Operation operation = new Operation();

                    // 문서에서 요청 설정
                    Schema<?> schema = new ObjectSchema()
                            .addProperty("email", new StringSchema())
                            .addProperty("password", new StringSchema());
                    RequestBody requestBody = new RequestBody().content(
                            new Content().addMediaType("application/json", new MediaType().schema(schema)));

                    operation.setRequestBody(requestBody);


                    // 문서에서 응답 설정
                    ApiResponses response = new ApiResponses();
                    response.addApiResponse(
                            String.valueOf(HttpStatus.OK.value()),
                            new ApiResponse().description(HttpStatus.OK.getReasonPhrase())
                    );
                    response.addApiResponse(
                            String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    );
                    operation.setResponses(response);


                    // 직접 만든 필터의 문서를 swagger에 등록
                    operation.addTagsItem("회원 기능");
                    operation.summary("로그인 기능");
                    PathItem pathItem = new PathItem().post(operation);
                    openApi.getPaths().addPathItem("/login", pathItem);

                }

            }
        };
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GrowOrders API 명세서")
                        .description("이 서비스는 농·수산물의 생육 상태나 생물의 건강 데이터를 기반으로 주문 가능 여부를 자동 판단하고 관리하는 스마트 주문 관리 플랫폼입니다. ")
                        .version("1.0.0"));
    }


}