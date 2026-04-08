package Payment.Integration.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class DemoController {

    @GetMapping
    public String sayHello()
    {
        return "helllo there!";
    }
}
