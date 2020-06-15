package edu.core.batch3;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobLaunchingController {

    @Autowired
    private JobOperator jobOperator;

    //how to call: curl localhost:8080/job/start/input.csv
    @RequestMapping(value = "/job/start/{filename}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long start(@PathVariable("filename") String name) throws Exception {
        return jobOperator.start("job", String.format("filename=%s", name));
    }

    //how to call : curl localhost:8080/job/restart/1
    @RequestMapping(value = "/job/restart/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long restart(@PathVariable("id") Long id) throws Exception {
        return jobOperator.restart(id);
    }

    /*
        delete from batch_step_execution_context;
        delete from batch_step_execution_seq;

        delete from batch_job_execution_context;
        delete from batch_job_Execution_params;
        delete from batch_job_execution_seq;
        delete from batch_job_seq;

        delete from batch_step_execution;
        delete from batch_job_execution;
        delete from batch_job_instance;

        use mysql;
        show tables;
        select * from employee;
     */
}
