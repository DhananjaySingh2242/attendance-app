package ampliedtech.com.attendanceApp.dbConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "ampliedtech.com.attendanceApp.mongoRepo"
)
public class MongoConfig {

}