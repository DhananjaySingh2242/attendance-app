package ampliedtech.com.attendanceApp.dbConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "ampliedtech.com.attendanceApp.jpaRepo"
)
public class JpaConfig {

}
