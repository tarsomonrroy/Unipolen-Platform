package com.unipolen.webserver;

import com.unipolen.webserver.controller.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@ComponentScan(basePackages = {
		"com.unipolen.webserver",
		"com.unipolen.webserver.controller",
		"com.unipolen.webserver.model",
		"com.unipolen.webserver.view"
})
public class WebConfig {
	public static final String DOMAIN = "localhost";

	@Autowired StaticResource staticResource;

	@Autowired HomePage homePage;
	@Autowired LoginPage loginPage;
	@Autowired LoginAction loginAction;
	@Autowired LogoutAction logoutAction;
	@Autowired RegisterPage registerPage;
	@Autowired RegisterAction registerAction;
	@Autowired CoursesPage coursesPage;
	@Autowired UnitsPage unitsPage;
	@Autowired FaqPage faqPage;
	@Autowired AccountPage accountPage;
	@Autowired ChangeUserProperty changeUserProperty;
	@Autowired DeleteAccountPage deleteAccountPage;
	@Autowired DeleteAccountAction deleteAccountAction;
	@Autowired AdminPage adminPage;
	@Autowired AdminQuery adminQuery;
	@Autowired GetCourseList getCourseList;

	@Bean
	public RouterFunction<ServerResponse> routes() {
		return route()
				.GET("/static/**", staticResource::handle)
				.GET("/", homePage::handle)
				.GET("/login", loginPage::handle)
				.POST("/login", loginAction::handle)
				.GET("/logout", logoutAction::handle)
				.GET("/registrar", registerPage::handle)
				.POST("/registrar", registerAction::handle)
				.path("/cursos", builder -> builder
						.GET("/{page}", coursesPage::handleWithPagination)
						.GET(coursesPage::handleWithoutPagination))
				.GET("/polos", unitsPage::handle)
				.GET("/faq", faqPage::handle)
				.path("/conta", builder -> builder
						.GET("", accountPage::handle)
						.GET("/alterar/{field}", changeUserProperty::displayPage)
						.POST("/alterar/{field}", changeUserProperty::handleChangeRequest)
						.GET("/apagar", deleteAccountPage::handle)
						.POST("/apagar", deleteAccountAction::handle))
				.GET("/admin", adminPage::handle)
				.POST("/admin/query", adminQuery::handle)
				.GET("/course-list", getCourseList::handle)
				.build();
	}

	@Bean
	public WebProperties.Resources resources() {
		return new WebProperties.Resources();
	}

}
