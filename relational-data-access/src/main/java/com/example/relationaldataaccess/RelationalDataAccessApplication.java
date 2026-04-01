package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * このクラスは、このアプリの開始地点です。
 * Spring Bootアプリとして起動されます。
 */
@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {
	
	/*
	 * ログ出力用。
	 * System.out.println の少しSpringらしい版と思えばOKです。
	 */
	private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}
	
	/*
	 * DBに対してSQLを実行するための道具です。
	 * Springが自動で用意してくれます。
	 */
	private final JdbcTemplate jdbcTemplate;

	/*
	 * コンストラクタです。
	 * Springが用意した JdbcTemplate を受け取って、
	 * このクラスの中で使えるように保存しています。
	 */
	public RelationalDataAccessApplication(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/*
	 * CommandLineRunner を implements しているので、
	 * Spring Bootの起動が終わったあとに、この run() が自動実行されます。
   */
	@Override
	public void run(String... strings) throws Exception {
		
		// 1. テーブルを作り始めることをログに出す
		log.info("Creating tables");

		// 2. もし customers テーブルがあれば削除
		jdbcTemplate.execute("DROP TABLE IF EXISTS customers");
		// 3. customers テーブルを作成
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		// Split up the array of whole names into an array of first/last names
		// 4. 名前のリストを作る
		List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
				// 5. 名前をスペースで分割して、first_name と last_name に分ける
				.map(name -> name.split(" "))
				// 6. それをリストにする
				.collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		// 7. 名前のリストをログに出す
		splitUpNames.forEach(name -> log.info("Inserting customer record for {} {}", name[0], name[1]));

		// Use JdbcTemplate's batchUpdate operation to bulk load data
		// 8. 名前のリストを customers テーブルに一括で挿入
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
		// 9. first_name が Josh の人だけ検索するに出す
		log.info("Querying for customer records where first_name = 'Josh':");
		/*
		 * 10. SQLで検索する
		 * WHERE first_name = ? の ? に "Josh" を入れている
		 *
		 * 検索結果1行ごとに Customer オブジェクトを作って、
		 * 最後にログへ出力している
		 * (引数) -> { 処理本体 } ラムダ式
		 */
		jdbcTemplate.query(
				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")), "Josh")
		.forEach(customer -> log.info(customer.toString()));
	}
}
