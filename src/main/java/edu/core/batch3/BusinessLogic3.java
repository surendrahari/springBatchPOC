package edu.core.batch3;

import edu.core.exception.ProcessNonRetriableException;
import edu.core.exception.RetriableException;
import edu.core.model.Employee;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BusinessLogic3 {

    private static final String HOST = "http://localhost:8080";
    private static final String URL = "/api/employee";

    private RestTemplate restTemplate = new RestTemplate();

    @Retryable(value = {RetriableException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public Employee getRemoteAccess(Employee employee) throws RetriableException, ProcessNonRetriableException {
        System.out.println("\tBegin getRemoteResponse.............");
        try {
            // Remote call
            ResponseEntity<Employee> responseEntity = restTemplate
                    .exchange(HOST + URL, HttpMethod.POST, new HttpEntity<>(employee), Employee.class);
            System.out.println("\tRest Response : " + responseEntity);

            // Remote call status check
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Remote response
                System.out.println("\tSuccess getRemoteResponse.............");
                return responseEntity.getBody();
            } else {
                // remote response failed (we consider here connection/network issue)
                throw new RetriableException("Retry Exception");
            }
        } catch (RetriableException e) {
            throw e; // re-throw the retriable exception
        } catch (Exception e) {
            // Any unknown exception
            throw new ProcessNonRetriableException("unknown exception from dbaccess api", e);
        }
    }

    @Recover
    public void recovery(ProcessNonRetriableException e) {
        System.out.println("Recovery : " + e.getMessage());
    }
}
