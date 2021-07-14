package marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @Author chengrong.yang
 * @Date 2021/3/3 14:26
 */
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"org.etocrm","cn.hutool.extra.spring"})
@EnableJpaRepositories(basePackages = {"org.etocrm"})
@EntityScan(basePackages = {"org.etocrm"})
@EnableDiscoveryClient
public class MarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketingApplication.class, args);
    }
}