package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {



	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}



	@Bean
	public CommandLineRunner initData (ScoreRepository scoreRepository,SalvoRepository salvoRepository, ShipRepository shipRepository,
									   PlayerRepository repositoryPlayer, GameRepository repositoryGame,
									   GamePlayerRepository repositorygameplayer){
		return (args) -> {
				Player player1= new Player("j.bauer@ctu.gov",passwordEncoder().encode("24"));
			Player player2= new Player("c.obrian@ctu.gov",passwordEncoder().encode("42"));
			Player player3= new Player("kim_bauer@gmail.com",passwordEncoder().encode("kb"));
			Player player4= new Player("mireal@gmail.com",passwordEncoder().encode("mire"));
			repositoryPlayer.save(player1);
			repositoryPlayer.save(player2);
			repositoryPlayer.save(player3);
			repositoryPlayer.save(player4);
			Game game1= new Game();
			Game game2=new Game();
			Game game3= new Game();
			repositoryGame.save(game1);
			repositoryGame.save(game2);
			repositoryGame.save(game3);


			GamePlayer gp1= new GamePlayer(
					game1,player1);
			GamePlayer gp2= new GamePlayer(
					game1,player3);
			GamePlayer gp3=new GamePlayer(
					game2,player2);
			GamePlayer gp4=new GamePlayer(
					game3,player4);



			repositorygameplayer.save(gp1);
			repositorygameplayer.save(gp2);
			repositorygameplayer.save(gp3);
			repositorygameplayer.save(gp4);

			Ship ship= new Ship("submarine",gp1,Arrays.asList("B1","B2","B3","B4"));
			Ship ship2= new Ship("carrier",gp2,Arrays.asList("E1","C3"));
			Ship ship3= new Ship("destroyer",gp1,Arrays.asList("E1","E2","E3","E4"));
			Ship ship4= new Ship("carrier",gp3,Arrays.asList("E1","C3"));
			Ship ship5= new Ship("carrier",gp4,Arrays.asList("E1","C3"));

			shipRepository.save(ship);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);

			Salvo salvo= new Salvo(1,gp1,Arrays.asList("B1","E5","D1"));
			Salvo salvo1= new Salvo(2,gp1,Arrays.asList("A1","A5"));
			Salvo salvo2= new Salvo(3,gp3,Arrays.asList("D3","D1","D4"));
			Salvo salvo3= new Salvo(4,gp2,Arrays.asList("D3","D1","D4"));
			Salvo salvo4= new Salvo(5,gp4,Arrays.asList("D3","D1","D4"));

			salvoRepository.save(salvo);
			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);


			Score score1= new Score(1.0,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 15:30:14.332"),
					game1,player1);
			Score score2= new Score(0.5,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-16 14:30:14.332"),
					game1,player3);
			Score score3= new Score(0,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 10:30:14.332"),
					game2,player2);
			Score score4= new Score(1.0,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 10:30:14.332"),
					game3,player4);


			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);













		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
@Configuration
 class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username-> {
			Player player = playerRepository.findByUserName(username);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + username);
			}
		});
	}
}


@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/game_view/*").hasAuthority("USER")
				.antMatchers("/api/game/**").hasAuthority("USER")
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/api/games/**").permitAll();


		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());





	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}





}