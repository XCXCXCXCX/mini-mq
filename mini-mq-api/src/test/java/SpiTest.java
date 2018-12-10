import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceServiceFactory;
import org.junit.Test;

/**
 * @author XCXCXCXCX
 * @Since 1.0
 */
public class SpiTest {

    @Test
    public void spiTest(){
        PersistenceService service = PersistenceServiceFactory.create();
        System.out.println(service);
    }
}
