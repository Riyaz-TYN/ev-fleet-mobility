package complaint_resolution.Complaint_Resolution;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/hello")
   String syaHelloWorld(){
        return "Hello World!";
    }

}
