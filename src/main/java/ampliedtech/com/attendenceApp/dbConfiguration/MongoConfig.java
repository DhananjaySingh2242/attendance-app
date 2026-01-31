package ampliedtech.com.attendenceApp.dbConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "ampliedtech.com.attendenceApp.mongoRepo"
)
public class MongoConfig {

}