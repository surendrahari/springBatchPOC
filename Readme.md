---
    Approach 1: (handle the exception at the job level) 
        - any retry exception will be restart the following process again
            (ie connection, network errors, etc.. ) 
        - any non retry exception will be end the job processing
            (ie bad file validation, SQL error, DB Constraint error, etc..)
        
        Pros and Cons: 
            - handled at the job level.
            - easy to implement.
            - file read, process(validation), write operation will be executed again
            - file read operation from Bridge would be costly 
                (suggestion : if the file read operation performed outside of the job 
                then performance would be improved, also it's much easier implementation and clean code)
                
        Technology:
            - using spring batch we could able to achive             

    Approach 2: ( Handling at the job step level if required job level also handled ) 
        input {
            RETRY (maxRetryCount, backOffDelay) {
                remote call - get
            } 
            if RETRY (fails) {
                error log
                file unlock (remote call), 
                    (Note: if file unlock failed next batch job run will be considered)  
                stop the remaining process and process the next time. 
                (throw retry failed exception)
            }
        }
        process {
            RETRY (maxRetryCount, backOffDelay) {
                remote call - get
            }
            if RETRY (fails) {
                error log
                file unlock (remote call), 
                    (Note: if file unlock failed next batch job run will be considered)  
                stop the remaining process and process the next time. 
                (throw retry failed exception)
            }
        }       
        write {
            RETRY (maxRetryCount, backOffDelay) {
                remote call (SAVE/POST)
            }
            if RETRY (fails) {
                file unlock (remote call), 
                    (Note: if file unlock failed next batch job run will be considered)  
                stop the remaining process and process the next time. 
                (throw retry failed exception)
            }
        }
        
    Pros and Cons:  
        - handling at the job step level, flexible to handle 
        - if the remote service call due to network glitch, quick retry would be faster resolution.
        - we could able to define more exception classification like
            - retriable error
            - failed to retry error
            - non retry error 
        - code complexity / testing effort more
             
    Technology:
        - Spring Batch
        - Spring Retry
        - Circute Breaker
        
    Spring Batch:
        step level we shall use:
            .faultTolerant()
            .retry(RetriableException.class)
            .retryLimit(<n>)
            .listener(new StepExecutionListenerSupport() {
                @Override
                public ExitStatus afterStep(StepExecution stepExecution) {
                .. condition baed exit status code we can set
                }
            });      
        job flow level control:
            .start(chunkStep())
            .on("RETRY_FAILED").stop() // we shall use stopAndRestart(..)
            .on("FAILED").fail()
            .on("COMPLETED").end()            
    
    Spring Retry:
        How to handle the DB Read Errors from micro services (https://www.baeldung.com/spring-retry ) 
            Implementation: 
                - @EnableRetry // Application or Config level
                - @Retryable(value = {<RetryableException>}, maxAttempts = <MaxRetryCount>, backoff = @Backoff(delay = <DelayInMilliSeconds>) )   
                - @Recover

    
---
References:
    https://docs.spring.io/spring-batch/docs/current/reference/html/transaction-appendix.html#transactionStatelessRetry

Fault Tolerance: (micro services):

https://docs.microsoft.com/en-us/azure/architecture/patterns/category/resiliency

https://spring.io/blog/2019/04/16/introducing-spring-cloud-circuit-breaker

https://spring.io/projects/spring-cloud-circuitbreaker
https://cloud.spring.io/spring-cloud-static/spring-cloud-circuitbreaker/1.0.3.RELEASE/reference/html/

spring retry :

https://www.baeldung.com/spring-retry
https://dzone.com/articles/how-to-use-spring-retry
https://dzone.com/articles/retry-design-pattern-with-istio
https://docs.microsoft.com/en-us/azure/architecture/patterns/retry

https://azure.microsoft.com/en-us/blog/using-the-retry-pattern-to-make-your-cloud-application-more-resilient/

Circute breaker:
https://resilience4j.readme.io/docs/getting-started-3
https://www.baeldung.com/spring-cloud-circuit-breaker
https://techblog.constantcontact.com/software-development/circuit-breakers-and-microservices/
https://docs.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker
https://dev.to/silviobuss/resilience-pattern-for-java-microservices-the-circuit-breaker-b2g

https://dev.to/silviobuss/resilience-for-java-microservices-circuit-breaker-with-resilience4j-5c81
https://www.exoscale.com/syslog/migrate-from-hystrix-to-resilience4j/
https://github.com/resilience4j/resilience4j

https://howtodoinjava.com/spring-cloud/spring-hystrix-circuit-breaker-tutorial/
https://www.exoscale.com/syslog/istio-vs-hystrix-circuit-breaker/.  -> hystrix (while box approach)  Vs Istio ( proxy management  tool uses block box way - wins )
