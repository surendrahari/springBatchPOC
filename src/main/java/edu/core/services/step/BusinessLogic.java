package edu.core.services.step;

import edu.core.services.exception.ProcessNonRetriableException;
import edu.core.services.model.Item;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BusinessLogic {

    public Item getRemoteResponse(Item item) throws ProcessNonRetriableException {
        ResponseEntity<Item> responseEntity =
                new RestTemplate().exchange("http://localhost:8080/api/item", HttpMethod.POST, new HttpEntity<>(item), Item.class, new Object[0]);

        System.out.println("Rest Response : " + responseEntity);
        if ( responseEntity.getStatusCode().is2xxSuccessful() ) {
            return responseEntity.getBody();
        }
        throw new ProcessNonRetriableException("retry start from reader level");
    }
}
