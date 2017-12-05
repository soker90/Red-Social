package modelo;
import org.springframework.context.annotation.*;
import java.net.*;

@Configuration
public class InetConfig {
    @Bean
    public String hostname() {
        try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}