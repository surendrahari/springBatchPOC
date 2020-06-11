How to handle the DB Read Errors from micro services <br/>
    1) option one use Spring Retry ( https://www.baeldung.com/spring-retry )
        @EnableRetry // Application or Config level
         @Retryable(value = {<RetryableException>},
                    maxAttempts = <MaxRetryCount>,
                    backoff = @Backoff(delay = <DelayInMilliSeconds>) )   
         @Recover
         // if we unable to recover DB related issues - perform file unlock option.
         
         
         
-----
    https://docs.spring.io/spring-batch/docs/current/reference/html/transaction-appendix.html#transactionStatelessRetry

    
    Approach 1: 
        Job Level : (handle the exception at the job level) 
            - any retry exception will be restart the following process again
                (ie connection, network errors, etc.. ) 
            - any non retry exception will be end the job processing
                (ie bad file validation, SQL error, DB Constraint error, etc..)
            
            Pros and Cons: 
                - handled at the job level.
                - easy to implement.
                - file read, process(validation), write operation will be executed again
                - Minio file read operation costly 
                    (suggestion : if the file read operation performed outside of the job it's much eaier implementation and clean code)

    Approach 2: 
        - Handling at the job step level        
            input {
                RETRY {
                    remote call - get
                } 
                if RETRY (failed) {
                    error log
                    file unlock (remote call - post), 
                        if file unlock failed next batch job run will be considered.  
                    stop the current batch job request. (throw non retry exception)
                }
            }
            process {
                RETRY {
                    remote call - get
                }
                if RETRY (fails) {
                    error log
                    perform file unlock
                    ignore process / write operations.
                }
            }       
            write {
                RETRY {
                    remote access - db api call (POST / Save)
                }
                if RETRY (fails) {
                    error log
                    perform file unlock
                    ignore process / write operations.
                }
            }
             
    retry 
        - DB get api call     
        
