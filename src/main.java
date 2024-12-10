//DEPS com.pulumi:pulumi:0.+
//DEPS com.pulumi:github:6.4.0
import com.pulumi.Pulumi;
import com.pulumi.core.Output;

public class main {
    public static void main(String[] args) {
        Pulumi.run(ctx -> {
            ctx.export("exampleOutput", Output.of("example"));
        });
    }
}
