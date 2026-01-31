package ampliedtech.com.attendenceApp.dbConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "ampliedtech.com.attendenceApp.jpaRepo"
)
public class JpaConfig {

}
