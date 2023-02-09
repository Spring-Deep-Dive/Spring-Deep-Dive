@Controller("/api/example")
public class ServletExample2 {
    @GetMapping("/cook")
    protected String getCook() {
        return "PSA Cook !";
    }

    @PostMapping
    protected int postExample() {
        return 1;
    }
}