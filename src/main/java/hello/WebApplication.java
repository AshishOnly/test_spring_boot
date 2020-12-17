package hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/home")
public class WebApplication {

    @RequestMapping(method = RequestMethod.GET)
    public String sendMessage(){

        return "result";
    }


}