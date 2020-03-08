package com.thorinhood.dataworker;

import com.thorinhood.dataworker.configs.CassandraConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
//@ContextConfiguration(classes = CassandraConfig.class)
class DataworkerApplicationTests {

//	@BeforeClass
//	public static void startCassandraEmbedded() {
//		EmbeddedCassandraServerHelper.startEmbeddedCassandra();
//		Cluster cluster = Cluster.builder()
//				.addContactPoints("127.0.0.1").withPort(9142).build();
//		Session session = cluster.connect();
//	}
//
//	@Test
//	void contextLoads() {
//	}

}
