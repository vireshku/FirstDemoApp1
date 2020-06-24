package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.measure.quantity.Mass;
import javax.sql.DataSource;

import org.jscience.physics.amount.Amount;
import org.jscience.physics.model.RelativisticModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import static javax.measure.unit.SI.KILOGRAM;

@Controller
@SpringBootApplication
public class Main {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index() {
		return "index";
	}
	
	
	
	@RequestMapping("/confdemo")
	String hello(Map<String, Object> model) {
		RelativisticModel.select();
		String energy = System.getenv().get("ENERGY");
		if (energy == null) {
			energy = "12 GeV";
		}
		Amount<Mass> m = Amount.valueOf(energy).to(KILOGRAM);
		model.put("science", "E=mc^2: " + energy + " = " + m.toString());
		return "confdemo";
	}
	
	
	
	@Autowired
	  private DataSource dataSource;

	@RequestMapping("/db")
	  String db(Map<String, Object> model) {
	    try (Connection connection = dataSource.getConnection()) {
	      Statement stmt = connection.createStatement();
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
	      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
	      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

	      ArrayList<String> output = new ArrayList<String>();
	      while (rs.next()) {
	        output.add("Read from DB: " + rs.getTimestamp("tick"));
	      }

	      model.put("records", output);
	      return "db";
	    } catch (Exception e) {
	      model.put("message", e.getMessage());
	      return "error";
	    }
	  }

	  @Bean
	  public DataSource dataSource() throws SQLException {
	    if (dbUrl == null || dbUrl.isEmpty()) {
	      return new HikariDataSource();
	    } else {
	      HikariConfig config = new HikariConfig();
	      config.setJdbcUrl(dbUrl);
	      return new HikariDataSource(config);
	    }
	  }
	
	
	
	
	
}