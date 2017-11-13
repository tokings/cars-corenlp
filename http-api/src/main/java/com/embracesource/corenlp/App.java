package com.embracesource.corenlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
@SpringBootApplication
public class App implements EmbeddedServletContainerCustomizer
{
	@Bean(name="restTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class);
    }

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		
		ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
		container.addErrorPages(error401Page, error404Page, error500Page);
	}
    
//	@Bean
//	public EmbeddedServletContainerFactory servletContainer() {
//		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
//		factory.setPort(8013);
//		factory.setSessionTimeout(10, TimeUnit.MINUTES);
//		
//		return factory;
//	}
}
