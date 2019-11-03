package com.rawa.cloud.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        // ParameterBuilder ticketPar = new ParameterBuilder();
        // List<Parameter> pars = new ArrayList<Parameter>();
        // ticketPar.name("Authorization").description("登录校验")//name表示名称，description表示描述
        // .modelRef(new ModelRef("string")).parameterType("header")
        // .required(false).defaultValue("Bearer
        // ").build();//required表示是否必填，defaultvalue表示默认值
        // pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.rawa.cloud.web"))  //指定暴露的AIP接口
                .paths(PathSelectors.any()).build();
        // .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("rawa Cloud api 文档").description("rawa Cloud Rest Api 接口文档的说明")  //自定义标题和说明
                .termsOfServiceUrl("http://www.XXX.com/")   //指定访问的URL
                .contact(new Contact("张银", "", "zhangyinag@126.com")).build();
    }
}
