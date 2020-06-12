package edu.core.batch1;

import edu.core.exception.ProcessNonRetriableException;
import edu.core.model.Item;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BusinessLogic1 {

    private static final String HOST = "http://localhost:8080";

    private static final String url1 = "/api1/item";
    private static final String url2 = "/api1/retry/item";

    public Item getRemoteResponse(Item item) throws ProcessNonRetriableException {
        try {
            ResponseEntity<Item> responseEntity = new RestTemplate()
                    .exchange(HOST + url2, HttpMethod.POST, new HttpEntity<>(item), Item.class);

            System.out.println("Rest Response : " + responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (Exception e) {
            System.out.println("===========> ???? error response ...... ");
            throw new ProcessNonRetriableException("retry start from reader level");
        }
        return item;
    }
}
